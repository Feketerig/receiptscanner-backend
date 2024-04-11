package hu.levente.fazekas.receiptscannerbackend

import hu.levente.fazekas.receiptscannerbackend.filestorage.FileStorageService
import hu.levente.fazekas.receiptscannerbackend.filestorage.StorageProperties
import hu.levente.fazekas.receiptscannerbackend.security.jwt.JwtUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class, JwtUtils::class)
class ReceiptscannerBackendApplication {

	@Bean
	fun init(storageService: FileStorageService): CommandLineRunner {
		return CommandLineRunner { args: Array<String?>? ->
			storageService.deleteAll()
			storageService.init()
		}
	}
}

fun main(args: Array<String>) {
	runApplication<ReceiptscannerBackendApplication>(*args)
}
