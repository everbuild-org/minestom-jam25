package org.everbuild.asorda.resources

import io.ktor.util.collections.ConcurrentSet
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.everbuild.asorda.resources.data.ResourceGenerator
import org.everbuild.asorda.resources.data.addWseeModels
import org.zeroturnaround.zip.ZipUtil
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter

class ResourceGenerationService : Thread() {
    @Volatile
    private var generationRequired = true
    private var channels = ConcurrentSet<Channel<String>>()

    override fun run() {
        runBlocking {
            while (true) {
                if (generationRequired) {
                    generationRequired = false
                    if (resourcesDir.exists()) {
                        resourcesDir.deleteRecursively()
                    }
                    resourcesDir.mkdirs()
                    val (pack, _) = spinner(ResourceGenerator::regenerate)
                    MinecraftResourcePackWriter.minecraft().writeToDirectory(resourcesDir, pack)
                    addWseeModels(resourcesDir)
                    zipDirectory(resourcesDir, resources)

                    val sha1 = ChecksumGenerator.sha1(resources)
                    channels.forEach { it.send(sha1) }
                }

                delay(250)
            }
        }
    }

    fun requestGeneration() {
        generationRequired = true
    }

    fun newUpdateChannel(): Channel<String> {
        val ch = Channel<String>()
        channels.add(ch)
        return ch
    }

    fun dropChannel(channel: Channel<String>) {
        channels.remove(channel)
    }

    companion object {
        fun base() = if (File("gradle.properties").exists()) "" else "../"
        val resourcesDir = File("${base()}run/resources")
        val resources = File("${base()}run/resources.zip")
        val metadata = File("${base()}run/resources.json")

        fun zipDirectory(sourceDir: File, zipFile: File) {
            ZipUtil.pack(sourceDir, zipFile)
        }
    }
}