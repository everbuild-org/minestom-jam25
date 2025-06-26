package org.everbuild.celestia.orion.platform.minestom.api.world

import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import it.unimi.dsi.fastutil.booleans.BooleanList
import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.utils.chunk.ChunkUtils
import net.minestom.server.utils.validate.Check
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.ceil

/**
 * Implements a thread-safe reader and writer for Minecraft region files.
 *
 * @see [Region file format](https://minecraft.wiki/w/Region_file_format)
 *
 * @see [Hephaistos implementation](https://github.com/Minestom/Hephaistos/blob/master/common/src/main/kotlin/org/jglrxavpok/hephaistos/mca/RegionFile.kt)
 */
internal class RegionFile(path: Path) : AutoCloseable {
    private val lock = ReentrantLock()
    private val file = RandomAccessFile(path.toFile(), "rw")

    private val locations = IntArray(MAX_ENTRY_COUNT)
    private val timestamps = IntArray(MAX_ENTRY_COUNT)
    private val freeSectors: BooleanList = BooleanArrayList(2)

    init {
        readHeader()
    }

    fun hasChunkData(chunkX: Int, chunkZ: Int): Boolean {
        lock.lock()
        try {
            return locations[getChunkIndex(chunkX, chunkZ)] != 0
        } finally {
            lock.unlock()
        }
    }

    @Throws(IOException::class)
    fun readChunkData(chunkX: Int, chunkZ: Int): CompoundBinaryTag? {
        lock.lock()
        try {
            if (!hasChunkData(chunkX, chunkZ)) return null

            val location = locations[getChunkIndex(chunkX, chunkZ)]
            file.seek((location shr 8).toLong() * SECTOR_SIZE) // Move to start of first sector
            val length = file.readInt()
            val compression = when (val compressionType = file.readByte().toInt()) {
                1 -> BinaryTagIO.Compression.GZIP
                COMPRESSION_ZLIB -> BinaryTagIO.Compression.ZLIB
                3 -> BinaryTagIO.Compression.NONE
                else -> throw IOException("Unsupported compression type: $compressionType")
            }

            // Read the raw content
            val data = ByteArray(length - 1)
            file.read(data)

            // Parse it as a compound tag
            return TAG_READER.read(ByteArrayInputStream(data), compression)
        } finally {
            lock.unlock()
        }
    }

    @Throws(IOException::class)
    fun writeChunkData(chunkX: Int, chunkZ: Int, data: CompoundBinaryTag) {
        // Write the data (compressed)
        val out = ByteArrayOutputStream()
        TAG_WRITER.writeNamed(MapEntry("", data), out, BinaryTagIO.Compression.ZLIB)
        val dataBytes = out.toByteArray()
        val chunkLength = CHUNK_HEADER_LENGTH + dataBytes.size

        val sectorCount = ceil(chunkLength / SECTOR_SIZE.toDouble()).toInt()
        Check.stateCondition(sectorCount >= SECTOR_1MB, "Chunk data is too large to fit in a region file")

        lock.lock()
        try {
            // We don't attempt to reuse the current allocation, just write it to a new position and free the old one.
            val chunkIndex = getChunkIndex(chunkX, chunkZ)
            val oldLocation = locations[chunkIndex]

            // Find a new location
            var firstSector = findFreeSectors(sectorCount)
            if (firstSector == -1) {
                firstSector = allocSectors(sectorCount)
            }
            val newLocation = (firstSector shl 8) or sectorCount

            // Mark the sectors as used & free the old sectors
            markLocation(oldLocation, true)
            markLocation(newLocation, false)

            // Write the chunk data
            file.seek(firstSector.toLong() * SECTOR_SIZE)
            file.writeInt(chunkLength)
            file.writeByte(COMPRESSION_ZLIB)
            file.write(dataBytes)

            // Update the header and write it
            locations[chunkIndex] = newLocation
            timestamps[chunkIndex] = (System.currentTimeMillis() / 1000).toInt()
            writeHeader()
        } finally {
            lock.unlock()
        }
    }

    @Throws(IOException::class)
    override fun close() {
        file.close()
    }

    private fun getChunkIndex(chunkX: Int, chunkZ: Int): Int {
        return (chunkZ and 31 shl 5) or (chunkX and 31)
    }

    @Throws(IOException::class)
    private fun readHeader() {
        file.seek(0)
        if (file.length() < HEADER_LENGTH) {
            // new file, fill in data
            file.write(ByteArray(HEADER_LENGTH))
        }

        //todo: addPadding()
        val totalSectors =
            ((file.length() - 1) / SECTOR_SIZE) + 1 // Round up, last sector does not need to be full size
        for (i in 0 until totalSectors) freeSectors.add(true)
        freeSectors[0] = false // First sector is locations
        freeSectors[1] = false // Second sector is timestamps

        // Read locations
        file.seek(0)
        for (i in 0 until MAX_ENTRY_COUNT) {
            locations[i] = file.readInt()
            val location = locations[i]
            if (location != 0) {
                markLocation(location, false)
            }
        }

        // Read timestamps
        for (i in 0 until MAX_ENTRY_COUNT) {
            timestamps[i] = file.readInt()
        }
    }

    @Throws(IOException::class)
    private fun writeHeader() {
        file.seek(0)
        for (location in locations) {
            file.writeInt(location)
        }
        for (timestamp in timestamps) {
            file.writeInt(timestamp)
        }
    }

    private fun findFreeSectors(length: Int): Int {
        var start = 0
        while (start < freeSectors.size - length) {
            var found = true
            for (i in 0 until length) {
                if (!freeSectors.getBoolean(start++)) {
                    found = false
                    break
                }
            }
            if (found) return start - length
            start++
        }
        return -1
    }

    @Throws(IOException::class)
    private fun allocSectors(count: Int): Int {
        val eof = file.length()
        file.seek(eof)

        val emptySector = ByteArray(SECTOR_SIZE)
        for (i in 0 until count) {
            freeSectors.add(true)
            file.write(emptySector)
        }

        return (eof / SECTOR_SIZE).toInt()
    }

    private fun markLocation(location: Int, free: Boolean) {
        val sectorCount = location and 0xFF
        val sectorStart = location shr 8
        Check.stateCondition(sectorStart + sectorCount > freeSectors.size, "Invalid sector count")
        for (i in sectorStart until sectorStart + sectorCount) {
            freeSectors[i] = free
        }
    }

    companion object {
        private const val MAX_ENTRY_COUNT = 1024
        private const val SECTOR_SIZE = 4096
        private const val SECTOR_1MB = 1024 * 1024 / SECTOR_SIZE
        private const val HEADER_LENGTH = MAX_ENTRY_COUNT * 2 * 4 // 2 4-byte fields per entry
        private const val CHUNK_HEADER_LENGTH =
            4 + 1 // Length + Compression type (todo non constant to support custom compression)

        private const val COMPRESSION_ZLIB = 2

        private val TAG_READER = BinaryTagIO.unlimitedReader()
        private val TAG_WRITER = BinaryTagIO.writer()

        fun getFileName(regionX: Int, regionZ: Int): String {
            return "r.$regionX.$regionZ.mca"
        }
    }
}