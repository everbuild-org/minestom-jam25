package org.everbuild.asorda.resources.data

import java.io.File
import net.worldseed.resourcepack.PackBuilder

fun addWseeModels(base: File) {
    val config = PackBuilder.generate(
        base.absoluteFile.resolve("../../resources/data/resources/wsee").toPath(),
        base.absoluteFile.toPath(),
        base.absoluteFile.resolve("../../utils/build/resources/main/models").toPath(),
    )
    val mappings = config.modelMappings
    val mappingFile = File(base, "../../utils/build/resources/main/model_mappings.json")
    mappingFile.parentFile.mkdirs()
    if (!mappingFile.exists()) mappingFile.createNewFile()
    mappingFile.writeText(mappings)
}