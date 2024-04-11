package hu.levente.fazekas.receiptscannerbackend.filestorage

import hu.levente.fazekas.receiptscannerbackend.filestorage.exceptions.CouldNotOpenFileException
import hu.levente.fazekas.receiptscannerbackend.filestorage.exceptions.FileNotFoundException
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class FileStorageService(
    properties: StorageProperties
) {
    private val rootLocation = Paths.get(properties.location)

    fun init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (exception: IOException) {
            throw CouldNotOpenFileException("Could not initialize storage")
        }
    }

    fun saveFile(file: MultipartFile): String {
        return try {
            val userId = 1L
            val originalFilename = file.contentType?.split("/")
            if (originalFilename == null || !originalFilename[0].contains("image")){
                throw FileNotFoundException("Not an image.")
            }

            if (file.isEmpty)
                throw CouldNotOpenFileException("Failed to store empty file.")

            val fileExtension = originalFilename[1]
            val filename = UUID.randomUUID().toString() + "." + fileExtension

            val folder = Files.createDirectories(
                rootLocation
                    .resolve(Paths.get("$userId"))
                    .normalize().toAbsolutePath()
            )

            val destinationFile = rootLocation
                .resolve(folder)
                .resolve(Paths.get(filename))
                .normalize().toAbsolutePath()

            if (!destinationFile.parent.contains(rootLocation)) {
                throw CouldNotOpenFileException("Cannot store file outside current directory")
            }

            file.inputStream.use { inputStream ->
                Files.copy(inputStream, destinationFile)
            }
            StringUtils.cleanPath(filename)
        } catch (exception: IOException) {
            throw CouldNotOpenFileException("Failed to read stored files")
        }
    }

    fun load(userId: Long, fileName: String): Path = rootLocation.resolve(userId.toString()).resolve(fileName)

    fun loadAsResources(fileName: String): Resource = try {
        val userId = 1L
        val resource: Resource = UrlResource(load(userId, fileName).toUri())
        if (resource.exists() || resource.isReadable) {
            resource
        } else {
            throw FileNotFoundException("Could not read file: $fileName")
        }
    } catch (e: MalformedURLException) {
        throw FileNotFoundException("Could not read file: $fileName")
    }

    fun delete(fileName: String) = try {
        val userId = 1L
        Files.delete(load(userId, fileName))
    } catch (e: MalformedURLException) {
        throw FileNotFoundException("Could not read file: $fileName")
    }

    //For testing purposes
    //TODO Remove
    fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}