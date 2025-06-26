package org.everbuild.asorda.resources

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.fromFile
import aws.smithy.kotlin.runtime.net.url.Url
import java.io.File

class ResourcePackUploadService(
    private val host: String,
    private val bucket: String,
    private val accessKey: String,
    private val secretKey: String
) {
    private val s3Client =
        S3Client {
            endpointUrl =
                try {
                    Url.parse(host)
                } catch (e: IllegalArgumentException) {
                    println("couldn't connect to s3")
                    throw e
                }
            forcePathStyle = true
            credentialsProvider =
                StaticCredentialsProvider {
                    accessKeyId = accessKey
                    secretAccessKey = secretKey
                }

            region = "eu-central-1"
        }

    suspend fun upload(file: File, path: String) {
        println("uploading ${file.absolutePath} to s3://$bucket/$path")
        s3Client.putObject(PutObjectRequest {
            this.bucket = this@ResourcePackUploadService.bucket
            this.body = ByteStream.fromFile(file)
            this.key = path
            this.contentType = "application/zip"
            this.contentLength = file.length()
        })

        println("Upload finished")
    }
}