package org.everbuild.celestia.orion.core.configuration

import java.io.File
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.util.*
import kotlin.reflect.KProperty

class PropertyProvider(namespaceName: String) : ConfigurationProvider {
    private val watchService = FileSystems.getDefault().newWatchService()
    private val filePath = Paths.get(System.getProperty("user.dir"))
    private val watchingFile = "$namespaceName.properties"

    private var properties: HashMap<String, String> = hashMapOf()

    init {
        filePath.register(
            watchService,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_CREATE
        )

        if (File(watchingFile).exists()) loadFile()

        object : Thread() {
            override fun run() {
                var key: WatchKey
                while ((watchService.take().also { key = it }) != null) {
                    for (event in key.pollEvents()) {
                        if (event.context().toString() != watchingFile) continue

                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            unloadFile()
                        } else {
                            loadFile()
                        }
                    }
                    key.reset()
                }
            }
        }.start()
    }

    fun loadFile() {
        val newProperties = Properties()
        val file = File(watchingFile)
        if (!file.exists()) return

        try {
            newProperties.load(file.inputStream())
        } catch (e: IOException) { return }

        properties.clear()
        newProperties.entries.forEach { entry ->
            properties[entry.key.toString()] = entry.value.toString()
        }
    }

    fun unloadFile() {
        properties.clear()
    }

    override fun getDelegation(key: String): PropertyDelegate {
        return PropertyProvidedDelegate(this, key)
    }

    override fun setDefault(key: String, value: String) {
        // Properties are never written
    }

    class PropertyProvidedDelegate(private val provider: PropertyProvider, private val key: String) : PropertyDelegate {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Optional<String> {
            return Optional.ofNullable(provider.properties[key])
        }
    }
}