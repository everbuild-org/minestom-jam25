package org.everbuild.asorda.resources

import aws.smithy.kotlin.runtime.InternalApi
import aws.smithy.kotlin.runtime.text.encoding.encodeHex
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest

object ChecksumGenerator {

    const val STREAM_BUFFER_LENGTH = 1024

    fun getCheckSumFromFile(digest: MessageDigest, filePath: String): String {
        val file = File(filePath)
        return getCheckSumFromFile(digest, file)
    }

    @OptIn(InternalApi::class)
    fun getCheckSumFromFile(digest: MessageDigest, file: File): String {
        val fis = FileInputStream(file)
        val byteArray = updateDigest(digest, fis).digest()
        fis.close()
        val hexCode = encodeHex(byteArray)
        return hexCode
    }

    /**
     * Reads through an InputStream and updates the digest for the data
     *
     * @param digest The MessageDigest to use (e.g. MD5)
     * @param data Data to digest
     * @return the digest
     */
    private fun updateDigest(digest: MessageDigest, data: InputStream): MessageDigest {
        val buffer = ByteArray(STREAM_BUFFER_LENGTH)
        var read = data.read(buffer, 0, STREAM_BUFFER_LENGTH)
        while (read > -1) {
            digest.update(buffer, 0, read)
            read = data.read(buffer, 0, STREAM_BUFFER_LENGTH)
        }
        return digest
    }

    fun sha1(file: File): String = getCheckSumFromFile(MessageDigest.getInstance("SHA-1"), file)
}
