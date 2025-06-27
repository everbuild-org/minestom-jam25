package org.everbuild.asorda.resources.data.api.lockfile

import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.everbuild.asorda.resources.data.api.inSoloExec

@Serializable
data class Lockfile(
    val dataVersion: Int = 1,
    val basicBlockStates: BasicBlockStateRegistry = BasicBlockStateRegistry(),
    val groundBlockStateRegistry: GroundBlockStateRegistry = GroundBlockStateRegistry()
)

object LockfileService {
    private const val FILENAME = "../resources.lock.json"
    private val beautifiedJson = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    val lockfile: Lockfile =
        getFile().inputStream().use { beautifiedJson.decodeFromString(it.bufferedReader().readText()) }

    private fun getFile(): File {
        val file = File(FILENAME)
        val resource = javaClass.getResource(FILENAME)
        if (file.exists()) {
            return file
        }
        if (resource != null) {
            val resourceFile = File(resource.file)
            if (resourceFile.exists()) {
                return resourceFile
            }
        }

        if (!inSoloExec) {
            throw IllegalStateException("Lockfile $FILENAME not found")
        }

        file.createNewFile()
        file.writeText(beautifiedJson.encodeToString(Lockfile()))
        return file
    }

    fun save() {
        getFile().writeText(beautifiedJson.encodeToString(lockfile))
    }
}