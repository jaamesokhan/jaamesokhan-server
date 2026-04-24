package ir.jaamebaade.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.SetBucketPolicyArgs
import ir.jaamebaade.dto.RecitationDto
import ir.jaamebaade.model.Recitation
import ir.jaamebaade.repository.RecitationRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.useDirectoryEntries

@Service
open class RecitationService(
    private val recitationRepository: RecitationRepository,
) {
    open fun listByPoemId(poemId: Int): List<RecitationDto> {
        return recitationRepository.findAllByPoemIdOrderByAudioId(poemId).map {
            RecitationDto(
                audioId = it.audioId!!,
                artistName = it.artistName!!,
                poemId = it.poemId!!,
                audioFileUrl = it.audioFileUrl,
                syncFileUrl = it.syncFileUrl,
            )
        }
    }
}

@Component
@ConditionalOnProperty(prefix = "recitations.import", name = ["enabled"], havingValue = "true")
open class RecitationImportRunner(
    private val minioClient: MinioClient,
    private val objectMapper: ObjectMapper,
    private val recitationRepository: RecitationRepository,
    @Value("\${recitations.import.directory}") private val importDirectory: String,
    @Value("\${minio.public-url}") private val minioPublicUrl: String,
    @Value("\${minio.recitations.bucket.name}") private val bucketName: String,
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(RecitationImportRunner::class.java)

    override fun run(vararg args: String?) {
        val root = Path.of(importDirectory)
        require(root.exists() && root.isDirectory()) {
            "Recitations directory not found: ${root.pathString}"
        }

        logger.info("Starting recitation import from '{}' into bucket '{}'", root.pathString, bucketName)
        ensureBucketExists()
        val totalRecitations = countRecitations(root)
        logger.info("Discovered {} recitation directories to import", totalRecitations)

        val importedCount = importRecitations(root, totalRecitations)
        logger.info(
            "Finished recitation import. Imported {} of {} recitations into bucket '{}'",
            importedCount,
            totalRecitations,
            bucketName
        )
    }

    private fun ensureBucketExists() {
        val exists = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(bucketName)
                .build()
        )

        if (!exists) {
            logger.info("Bucket '{}' does not exist, creating it", bucketName)
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
            )
        } else {
            logger.info("Bucket '{}' already exists", bucketName)
        }

        val publicPolicy = """
            {
              "Version":"2012-10-17",
              "Statement":[
                {
                  "Effect":"Allow",
                  "Principal":{"AWS":["*"]},
                  "Action":["s3:GetBucketLocation","s3:ListBucket"],
                  "Resource":["arn:aws:s3:::$bucketName"]
                },
                {
                  "Effect":"Allow",
                  "Principal":{"AWS":["*"]},
                  "Action":["s3:GetObject"],
                  "Resource":["arn:aws:s3:::$bucketName/*"]
                }
              ]
            }
        """.trimIndent()

        minioClient.setBucketPolicy(
            SetBucketPolicyArgs.builder()
                .bucket(bucketName)
                .config(publicPolicy)
                .build()
        )
        logger.info("Applied public read policy to bucket '{}'", bucketName)
    }

    private fun countRecitations(root: Path): Int {
        var totalCount = 0
        root.useDirectoryEntries { poemDirectories ->
            poemDirectories
                .filter { it.isDirectory() }
                .forEach { poemDirectory ->
                    poemDirectory.useDirectoryEntries { audioDirectories ->
                        totalCount += audioDirectories.count { it.isDirectory() }
                    }
                }
        }
        return totalCount
    }

    private fun importRecitations(root: Path, totalRecitations: Int): Int {
        var importedCount = 0
        root.useDirectoryEntries { poemDirectories ->
            poemDirectories
                .filter { it.isDirectory() }
                .sortedBy { it.name.toIntOrNull() ?: Int.MAX_VALUE }
                .forEach { poemDirectory ->
                    val poemId = poemDirectory.name.toIntOrNull()
                        ?: error("Invalid poem directory name: ${poemDirectory.name}")
                    logger.info("Processing poem {} from '{}'", poemId, poemDirectory.pathString)
                    poemDirectory.useDirectoryEntries { audioDirectories ->
                        val recitations = audioDirectories
                            .filter { it.isDirectory() }
                            .sortedBy { it.name.toIntOrNull() ?: Int.MAX_VALUE }
                            .toList()
                        logger.info("Found {} recitations for poem {}", recitations.size, poemId)
                        recitations.forEach { audioDirectory ->
                            importedCount += importRecitation(root, poemId, audioDirectory)
                            logger.info(
                                "Imported recitation {} of {}",
                                importedCount,
                                totalRecitations
                            )
                        }
                    }
                }
        }
        return importedCount
    }

    private fun importRecitation(root: Path, poemId: Int, audioDirectory: Path): Int {
        val metaFile = audioDirectory.resolve("meta.json")
        val metadata = objectMapper.readValue(metaFile.toFile(), RecitationMetadata::class.java)
        val audioId = audioDirectory.name.toIntOrNull() ?: metadata.id
        require(poemId == metadata.poemId) {
            "Poem id mismatch for ${audioDirectory.pathString}: expected $poemId, got ${metadata.poemId}"
        }
        require(audioId == metadata.id) {
            "Audio id mismatch for ${audioDirectory.pathString}: expected $audioId, got ${metadata.id}"
        }

        logger.info(
            "Importing recitation audioId={} for poemId={} by '{}'",
            audioId,
            poemId,
            metadata.audioArtist
        )

        var audioFileUrl: String? = null
        var syncFileUrl: String? = null

        Files.list(audioDirectory).use { files ->
            files
                .filter { Files.isRegularFile(it) }
                .forEach { file ->
                    val objectName = root.relativize(file).toString().replace('\\', '/')
                    logger.info("Uploading '{}' to '{}/{}'", file.fileName, bucketName, objectName)
                    uploadFile(objectName, file)

                    when (file.fileName.toString().lowercase(Locale.ROOT)) {
                        "audio.mp3" -> audioFileUrl = publicObjectUrl(objectName)
                        "text.xml" -> syncFileUrl = publicObjectUrl(objectName)
                    }
                }
        }

        val recitation = recitationRepository.findByAudioId(audioId).orElse(Recitation())
        recitation.audioId = audioId
        recitation.poemId = poemId
        recitation.artistName = metadata.audioArtist
        recitation.audioFileUrl = audioFileUrl
        recitation.syncFileUrl = syncFileUrl
        recitationRepository.save(recitation)
        logger.info(
            "Saved recitation audioId={} with audioUrlPresent={} and syncUrlPresent={}",
            audioId,
            audioFileUrl != null,
            syncFileUrl != null
        )
        return 1
    }

    private fun uploadFile(objectName: String, file: Path) {
        file.inputStream().use { inputStream ->
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .stream(inputStream, Files.size(file), -1)
                    .contentType(contentType(file))
                    .build()
            )
        }
    }

    private fun contentType(file: Path): String {
        return Files.probeContentType(file) ?: when (file.fileName.toString().lowercase(Locale.ROOT)) {
            "audio.mp3" -> "audio/mpeg"
            "text.xml" -> "application/xml"
            "meta.json" -> "application/json"
            else -> "application/octet-stream"
        }
    }

    private fun publicObjectUrl(objectName: String): String {
        return "${minioPublicUrl.trimEnd('/')}/$bucketName/$objectName"
    }

    data class RecitationMetadata(
        val id: Int,
        val poemId: Int,
        val audioArtist: String,
    )
}
