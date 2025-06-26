package org.everbuild.asorda.resources.data.api

import java.io.File
import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.ResourceGenerator
import team.unnamed.creative.base.Writable

val inSoloExec by lazy { System.getProperty("asorda.rpgen") != null }
private val classLoader = ResourceGenerator::class.java.classLoader

fun resource(path: String): Writable {
    return if (inSoloExec) {
        Writable.file(resourceFile(path))
    } else {
        Writable.resource(classLoader, path)
    }
}

fun resourceFile(path: String): File {
    return File(
        if (inSoloExec)
            "resources/data/resources/$path"
        else
            classLoader.getResource(path)?.file!!
    )
}

fun Key.withFileType(type: String) = Key.key(namespace(), value() + ".$type")
