package org.everbuild.asorda.resources

import io.ktor.util.collections.ConcurrentSet
import kotlinx.coroutines.channels.Channel
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.everbuild.asorda.resources.data.ResourceGenerator
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
                    val (pack, _) = spinner(ResourceGenerator::regenerate)
                    MinecraftResourcePackWriter.minecraft().writeToZipFile(resources, pack)
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
        val resources = File("run/resources.zip")
        val metadata = File("run/resources.json")
    }
}