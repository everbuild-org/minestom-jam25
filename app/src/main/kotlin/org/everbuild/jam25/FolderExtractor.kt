package org.everbuild.jam25

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.net.URI
import java.net.URL
import java.util.jar.JarFile
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.utils.logger

fun extractToDir(folderInJarToExtract: String): Path {
    val tempDir: Path = Files.createTempDirectory("extracted_jar_folder_")

    try {
        val sampleResourceInFolder = "resources.json"
        val jarUrl: URL? = object {}.javaClass.classLoader.getResource(sampleResourceInFolder)
            ?: object {}.javaClass.classLoader.getResource(folderInJarToExtract)

        if (jarUrl != null) {
            Jam.logger.info("Found JAR URL: $jarUrl")
            if (jarUrl.protocol == "jar") {
                val jarFilePathString = jarUrl.toURI().toString()
                    .substringAfter("jar:file:")
                    .substringBefore("!")
                val jarFileUri = URI("file://$jarFilePathString")
                val jarFile: File = Paths.get(jarFileUri).toFile()

                var extractedCount = 0

                JarFile(jarFile).use { jar ->
                    val entries = jar.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        if (entry.name.startsWith(folderInJarToExtract) && !entry.isDirectory) {
                            val relativePath = entry.name.removePrefix(folderInJarToExtract)
                            val outputPath: Path =
                                tempDir.resolve(if (relativePath.startsWith("/")) ".$relativePath" else relativePath)

                            Files.createDirectories(outputPath.parent)

                            jar.getInputStream(entry).use { inputStream ->
                                Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING)
                                extractedCount++
                            }
                        }
                    }
                }

                Jam.logger.info("Extraction complete! $extractedCount Files are in: $tempDir")

            } else if (jarUrl.protocol == "file") {
                val sourcePath: Path = Paths.get(jarUrl.toURI()).parent.resolve(folderInJarToExtract.removeSuffix("/"))
                Jam.logger.info("Running in development mode. Folder is already on filesystem: $sourcePath")

                Files.walk(sourcePath)
                    .forEach { sourceFile ->
                        if (Files.isRegularFile(sourceFile)) {
                            val relativePath = sourcePath.relativize(sourceFile)
                            val targetPath = tempDir.resolve(relativePath)
                            Files.createDirectories(targetPath.parent)
                            Files.copy(sourceFile, targetPath, StandardCopyOption.REPLACE_EXISTING)
                            Jam.logger.info("Copied from development: $sourceFile to $targetPath")
                        }
                    }
            } else {
                Jam.logger.error("Unsupported URL protocol: ${jarUrl.protocol}")
            }
        } else {
            Jam.logger.error("Resource '$folderInJarToExtract' or '$sampleResourceInFolder' not found in JAR or development environment.")
        }
    } catch (e: Exception) {
        Jam.logger.error("An error occurred during extraction: ${e.message}")
        e.printStackTrace()
    } finally {
        Mc.scheduler.buildShutdownTask {
            Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete)
            Jam.logger.info("Deleted temporary files in: $tempDir")
        }
    }

    return tempDir
}