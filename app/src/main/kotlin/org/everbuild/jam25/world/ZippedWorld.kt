package org.everbuild.jam25.world

import java.io.File
import java.nio.file.Files
import java.util.zip.ZipInputStream
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.utils.chunk.ChunkSupplier
import org.everbuild.celestia.orion.platform.minestom.api.Mc

open class ZippedWorld(name: String) {
    val instance = Mc.instance.createInstanceContainer()

    init {
        val worldDir = File("worlds/$name")
        if (worldDir.exists()) worldDir.deleteRecursively()
        worldDir.mkdirs()

        ZipInputStream(javaClass.getResourceAsStream("/$name.zip")!!).use { zip ->
            generateSequence { zip.nextEntry }
                .filterNot { it.isDirectory }
                .forEach { entry ->
                    val targetFile = File(worldDir, entry.name)
                    targetFile.parentFile.mkdirs()
                    Files.copy(zip, targetFile.toPath())
                }
        }

        instance.chunkLoader = AnvilLoader(worldDir.toPath())
        instance.chunkSupplier = ChunkSupplier { i, x, y -> LightingChunk(i, x, y) }
    }
}