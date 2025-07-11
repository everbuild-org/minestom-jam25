package org.everbuild.celestia.orion.platform.minestom.api.world

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import net.kyori.adventure.nbt.*
import net.kyori.adventure.text.logger.slf4j.ComponentLogger.logger
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.*
import net.minestom.server.instance.block.Block
import net.minestom.server.registry.RegistryKey
import net.minestom.server.utils.MathUtils
import net.minestom.server.utils.validate.Check
import org.everbuild.celestia.orion.platform.minestom.api.world.entity.EntityRegistry
import org.slf4j.LoggerFactory

class WorldLoader(private val path: Path) : IChunkLoader {
    private val alreadyLoaded: MutableMap<String, RegionFile?> = ConcurrentHashMap()
    private val alreadyLoadedEntity: MutableMap<String, RegionFile?> = ConcurrentHashMap()
    private val levelPath: Path = path.resolve("level.dat")
    private val regionPath: Path = path.resolve("region")
    private val entitiesPath: Path = path.resolve("entities")

    private class RegionCache : ConcurrentHashMap<IntIntImmutablePair, MutableSet<IntIntImmutablePair>>()

    private val perRegionLoadedChunksLock = ReentrantLock()
    private val perRegionLoadedEntityChunksLock = ReentrantLock()

    /**
     * Represents the chunks currently loaded per region. Used to determine when a region file can be unloaded.
     */
    private val perRegionLoadedChunks = RegionCache()
    private val perRegionLoadedEntityChunks = RegionCache()

//     thread local to avoid contention issues with locks
//    private val blockStateId2ObjectCacheTLS =
//        ThreadLocal.withInitial<Int2ObjectMap<CompoundBinaryTag>> { Int2ObjectArrayMap() }

    override fun loadInstance(instance: Instance) {
        if (!Files.exists(levelPath)) {
            return
        }
        try {
            Files.newInputStream(levelPath).use { stream ->
                val tag: CompoundBinaryTag = BinaryTagIO.reader().readNamed(stream, BinaryTagIO.Compression.GZIP).value
                Files.copy(levelPath, path.resolve("level.dat_old"), StandardCopyOption.REPLACE_EXISTING)
                instance.tagHandler().updateContent(tag)
            }

        } catch (e: IOException) {
            MinecraftServer.getExceptionManager().handleException(e)
        }
    }

    override fun loadChunk(instance: Instance, chunkX: Int, chunkZ: Int): Chunk? {
        LOGGER.debug("Attempt loading at {} {}", chunkX, chunkZ)
        if (!Files.exists(path)) {
            // No world folder
            LOGGER.error("No world folder found at {}", path)
            return null
        }
        try {
            return loadMCA(instance, chunkX, chunkZ)
        } catch (e: Exception) {
            MinecraftServer.getExceptionManager().handleException(e)
        }
        return null
    }

    @Throws(IOException::class)
    private fun loadMCA(instance: Instance, chunkX: Int, chunkZ: Int): Chunk? {
        val mcaFile = getMCAFile(chunkX, chunkZ, false) ?: return null
        val mcaEntityFile = getMCAFile(chunkX, chunkZ, true)
        val chunkData = mcaFile.readChunkData(chunkX, chunkZ) ?: return null
        val chunk: Chunk = instance.chunkSupplier.createChunk(instance, chunkX, chunkZ)
        synchronized(chunk) {
            val status = chunkData.getString("status")
            if (status.isEmpty() || status == "minecraft:full") {
                // TODO make parallel

                // Blocks + Biomes
                loadSections(chunk, chunkData)

                // Block entities
                loadBlockEntities(chunk, chunkData)

                chunk.loadHeightmapsFromNBT(chunkData.getCompound("Heightmaps"))
            } else {
                LOGGER.warn("Chunk is not fully generated, skipping: {}", status)
            }
        }

        // load entities
        if (mcaEntityFile != null) {
            val chunkDataEntity = mcaEntityFile.readChunkData(chunkX, chunkZ)
            if (chunkDataEntity != null) {
                loadEntities(chunk, chunkDataEntity)
            }
        }

        calculateCachedLoadedRegion(perRegionLoadedChunks, chunkX, chunkZ)
        calculateCachedLoadedRegion(perRegionLoadedEntityChunks, chunkX, chunkZ)

        return chunk
    }

    private fun calculateCachedLoadedRegion(cache: RegionCache, chunkX: Int, chunkZ: Int) {
        synchronized(cache) {
            val regionX = toRegionCoordinate(chunkX)
            val regionZ = toRegionCoordinate(chunkZ)
            val chunks: MutableSet<IntIntImmutablePair> =
                cache
                    .computeIfAbsent(
                        IntIntImmutablePair(regionX, regionZ)
                    ) { hashSetOf() } // region cache may have been removed on another thread due to unloadChunk
            chunks.add(IntIntImmutablePair(chunkX, chunkZ))
        }
    }

    private fun toRegionCoordinate(chunkX: Int): Int {
        return chunkX shr 5
    }

    private fun loadEntities(chunk: Chunk, chunkDataEntity: CompoundBinaryTag) {
        val entities = chunkDataEntity.getList("Entities", BinaryTagTypes.COMPOUND)
        for (entity in entities) {
            val tag = entity as CompoundBinaryTag
            val entityID = tag.getString("id")
            if (entityID.isNotEmpty()) {
                val entityDissector = EntityRegistry.dissectorForType(entityID)
                defer { entityDissector?.dissect(chunk, entity) }
            }
        }
    }

    private fun defer(block: () -> Unit) {
        MinecraftServer.getSchedulerManager().buildTask { block() }.schedule()
    }

    private fun getMCAFile(chunkX: Int, chunkZ: Int, entity: Boolean = false): RegionFile? {
        val regionX = toRegionCoordinate(chunkX)
        val regionZ = toRegionCoordinate(chunkZ)
        val loaderCache = if (entity) alreadyLoadedEntity else alreadyLoaded
        val path = if (entity) entitiesPath else regionPath
        val loaded = if (entity) perRegionLoadedEntityChunks else perRegionLoadedChunks
        val lock = if (entity) perRegionLoadedEntityChunksLock else perRegionLoadedChunksLock

        return loaderCache.computeIfAbsent(RegionFile.getFileName(regionX, regionZ)) { name ->
            val resolvedPath = path.resolve(name)
            if (!Files.exists(resolvedPath)) {
                // No region file
                return@computeIfAbsent null
            }

            lock.lock()

            try {
                val previousVersion: Set<IntIntImmutablePair>? =
                    loaded.put(IntIntImmutablePair(regionX, regionZ), HashSet())
                assert(previousVersion == null) { "The AnvilLoader cache should not already have data for this region." }
                return@computeIfAbsent RegionFile(resolvedPath)
            } catch (e: IOException) {
                MinecraftServer.getExceptionManager().handleException(e)
                return@computeIfAbsent null
            } finally {
                lock.unlock()
            }
        }
    }

    private fun unpack(output: IntArray, input: LongArray, bitsPerEntry: Int) {
        var bitsLeft = 64
        var bits = 0L
        for ((index, i) in input.indices.withIndex()) {
            var value = input[i]
            while (bitsLeft < bitsPerEntry) {
                bits = bits or (value and ((1L shl bitsLeft) - 1)) shl bits.toInt()
                value = value ushr bitsLeft
                bitsLeft += 64
            }
            bits = bits or (value and ((1L shl bitsPerEntry) - 1)) shl bits.toInt()
            output[index] = (bits and ((1 shl bitsPerEntry) - 1).toLong()).toInt()
            bits = bits ushr bitsPerEntry
            bitsLeft -= bitsPerEntry
        }
    }

    private fun loadSections(chunk: Chunk, chunkData: CompoundBinaryTag) {
        for (sectionTag in chunkData.getList("sections", BinaryTagTypes.COMPOUND)) {
            val sectionData = sectionTag as CompoundBinaryTag

            val sectionY = sectionData.getInt("Y", Int.MIN_VALUE)
            Check.stateCondition(sectionY == Int.MIN_VALUE, "Missing section Y value")
            val yOffset = Chunk.CHUNK_SECTION_SIZE * sectionY

            if (sectionY < chunk.minSection || sectionY >= chunk.maxSection) {
                // Vanilla stores a section below and above the world for lighting, throw it out.
                continue
            }

            val section = chunk.getSection(sectionY)

            // Lighting
            val skyLightTag = sectionData.getCompound("SkyLight")
            if (skyLightTag is ByteArrayBinaryTag && skyLightTag.size() == 2048) {
                section.setSkyLight(skyLightTag.value())
            }

            val blockLightTag = sectionData.getCompound("BlockLight")
            if (blockLightTag is ByteArrayBinaryTag && blockLightTag.size() == 2048) {
                section.setBlockLight(blockLightTag.value())
            }

            run {
                // Biomes
                val biomesTag = sectionData.getCompound("biomes")
                val biomePaletteTag = biomesTag.getList("palette", BinaryTagTypes.STRING)
                val convertedBiomePalette: IntArray = loadBiomePalette(biomePaletteTag)
                if (convertedBiomePalette.size == 1) {
                    // One solid block, no need to check the data
                    section.biomePalette().fill(convertedBiomePalette[0])
                } else if (convertedBiomePalette.size > 1) {
                    val packedIndices = biomesTag.getLongArray("data")
                    Check.stateCondition(packedIndices.isEmpty(), "Missing packed biomes data")
                    val biomeIndices = IntArray(64)

                    var bitsPerEntry = packedIndices.size * 64 / biomeIndices.size
                    if (bitsPerEntry > 3) bitsPerEntry = MathUtils.bitsToRepresent(convertedBiomePalette.size)
                    unpack(biomeIndices, packedIndices, bitsPerEntry)

                    section.biomePalette().setAll { x, y, z ->
                        val index = x + z * 4 + y * 16
                        convertedBiomePalette[biomeIndices[index]]
                    }
                }
            }

            run {
                // Blocks
                val blockStatesTag = sectionData.getCompound("block_states")
                val blockPaletteTag = blockStatesTag.getList("palette", BinaryTagTypes.COMPOUND)
                val convertedPalette: Array<Block> = loadBlockPalette(blockPaletteTag)
                if (blockPaletteTag.size() == 1) {
                    // One solid block, no need to check the data
                    section.blockPalette().fill(convertedPalette[0].stateId())
                } else if (blockPaletteTag.size() > 1) {
                    val packedStates = blockStatesTag.getLongArray("data")
                    Check.stateCondition(packedStates.isEmpty(), "Missing packed states data")
                    val blockStateIndices =
                        IntArray(Chunk.CHUNK_SECTION_SIZE * Chunk.CHUNK_SECTION_SIZE * Chunk.CHUNK_SECTION_SIZE)
                    unpack(blockStateIndices, packedStates, packedStates.size * 64 / blockStateIndices.size)

                    for (y in 0 until Chunk.CHUNK_SECTION_SIZE) {
                        for (z in 0 until Chunk.CHUNK_SECTION_SIZE) {
                            for (x in 0 until Chunk.CHUNK_SECTION_SIZE) {
                                try {
                                    val blockIndex =
                                        y * Chunk.CHUNK_SECTION_SIZE * Chunk.CHUNK_SECTION_SIZE + z * Chunk.CHUNK_SECTION_SIZE + x
                                    val paletteIndex = blockStateIndices[blockIndex]
                                    val block = convertedPalette[paletteIndex]

                                    chunk.setBlock(x, y + yOffset, z, block)
                                } catch (e: java.lang.Exception) {
                                    MinecraftServer.getExceptionManager().handleException(e)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadBlockPalette(paletteTag: ListBinaryTag): Array<Block> {
        val convertedPalette = arrayOfNulls<Block>(paletteTag.size())
        for (i in convertedPalette.indices) {
            val paletteEntry = paletteTag.getCompound(i)
            val blockName = paletteEntry.getString("Name")
            if (blockName == "minecraft:air") {
                convertedPalette[i] = Block.AIR
            } else {
                var block = Block.fromKey(blockName)
                    ?: Block.fromKey(LegacyBlockNameTransfer.transferLegacyBlockName(blockName))
                    ?: throw IllegalStateException("Failed to find block for name $blockName")
                // Properties
                val properties: MutableMap<String, String> = HashMap()
                val propertiesNBT = paletteEntry.getCompound("Properties")
                for ((key, value) in propertiesNBT) {
                    if (value is StringBinaryTag) {
                        properties[key] = value.value()
                    } else {
                        MinecraftServer.LOGGER.warn(
                            "Fail to parse block state properties {}, expected a string for {}",
                            propertiesNBT, key
                        )
                    }
                }
                if (properties.isNotEmpty()) block = block.withProperties(properties)

                // Handler
                val handler = MinecraftServer.getBlockManager().getHandler(block.name())
                if (handler != null) block = block.withHandler(handler)

                convertedPalette[i] = block
            }
        }
        @Suppress("UNCHECKED_CAST")
        return convertedPalette as Array<Block>
    }

    private fun loadBiomePalette(paletteTag: ListBinaryTag): IntArray {
        val convertedPalette = IntArray(paletteTag.size())
        for (i in convertedPalette.indices) {
            val name = paletteTag.getString(i)
            var biomeId: Int = BIOME_REGISTRY.getId(RegistryKey.unsafeOf(name))
            if (biomeId == -1) biomeId = PLAINS_ID
            convertedPalette[i] = biomeId
        }
        return convertedPalette
    }

    private fun loadBlockEntities(loadedChunk: Chunk, chunkReader: CompoundBinaryTag) {
        for (tag in chunkReader.getList("block_entities", BinaryTagTypes.COMPOUND)) {
            val te = tag as CompoundBinaryTag;
            val x = te.getInt("x")
            val y = te.getInt("y")
            val z = te.getInt("z")
            var block: Block = loadedChunk.getBlock(x, y, z)
            val tileEntityID = te.getString("id")

            @Suppress("UnstableApiUsage")
            val handler = MinecraftServer.getBlockManager().getHandlerOrDummy(tileEntityID.toString())
            block = block.withHandler(handler)

            // Remove anvil tags
            val trimmedTag = te
                .remove("id")
                .remove("x")
                .remove("y")
                .remove("z")
                .remove("keepPacked")
            // Place block

            val finalBlock = if (trimmedTag.size() > 0) block.withNbt(trimmedTag) else block
            loadedChunk.setBlock(x, y, z, finalBlock)
        }
    }

    override fun saveInstance(instance: Instance) {
        val nbt = instance.tagHandler().asCompound()
        if (nbt.size() == 0) {
            // Instance has no data
            return
        }
        try {
            Files.newOutputStream(levelPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                .use { os ->
                    BinaryTagIO.writer().writeNamed(
                        MapEntry("", nbt), os, BinaryTagIO.Compression.GZIP
                    )
                }
        } catch (e: IOException) {
            MinecraftServer.getExceptionManager().handleException(e)
        }
        return
    }

    override fun saveChunk(chunk: Chunk) {
    }

    /**
     * Unload a given chunk. Also unloads a region when no chunk from that region is loaded.
     *
     * @param chunk the chunk to unload
     */
    override fun unloadChunk(chunk: Chunk) {
        val regionX = toRegionCoordinate(chunk.chunkX)
        val regionZ = toRegionCoordinate(chunk.chunkZ)
        val regionKey = IntIntImmutablePair(regionX, regionZ)

        perRegionLoadedChunksLock.lock()
        try {
            val chunks = perRegionLoadedChunks[regionKey]
            if (chunks != null) { // if null, trying to unload a chunk from a region that was not created by the AnvilLoader
                // don't check return value, trying to unload a chunk not created by the AnvilLoader is valid
                chunks.remove(IntIntImmutablePair(chunk.chunkX, chunk.chunkZ))

                if (chunks.isEmpty()) {
                    perRegionLoadedChunks.remove(regionKey)
                    val regionFile = alreadyLoaded.remove(RegionFile.getFileName(regionX, regionZ))
                    if (regionFile != null) {
                        try {
                            regionFile.close()
                        } catch (e: IOException) {
                            MinecraftServer.getExceptionManager().handleException(e)
                        }
                    }
                }
            }
        } finally {
            perRegionLoadedChunksLock.unlock()
        }
    }

    override fun supportsParallelLoading(): Boolean {
        return true
    }

    override fun supportsParallelSaving(): Boolean {
        return true
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorldLoader::class.java)
        private val BIOME_REGISTRY = MinecraftServer.getBiomeRegistry()
        private val PLAINS_ID = BIOME_REGISTRY.getId(RegistryKey.unsafeOf("minecraft:plains"))

        private fun preloadProgressBar(current: Int, max: Int) {
            val w = 20
            val part = max / w
            val done = current / part
            val left = w - done

            val sb = StringBuilder()
            sb.append("\u001B[32m")
            for (i in 0 until done) {
                sb.append("█")
            }

            sb.append("\u001B[31m")
            for (i in 0 until left) {
                sb.append("█")
            }

            sb.append("\u001B[0m")
            sb.append(" $current/$max")
            logger().info(sb.toString())
        }

        fun preloadWorld(instance: Instance, dx: Int, dy: Int) {
            logger().info("Preloading world...")
            val all = (dx * 2 + 1) * (dy * 2 + 1)
            var i = 0
            var lastLog = 0
            for (chunkX in -dx..dx) {
                for (chunkZ in -dy..dy) {
                    instance.loadChunk(chunkX, chunkZ)
                    i++

                    // only log every 20%
                    if (i - lastLog > all / 5) {
                        lastLog = i
                        preloadProgressBar(i, all)
                    }
                }
            }
            LightingChunk.relight(instance, instance.chunks)
            logger().info("Preloading world... Done!")
        }
    }
}