package hu.levente.fazekas.receiptscannerbackend.filestorage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("hu.levente.fazekas.receiptscannerbackend.filestorage")
data class StorageProperties(
    val location: String
)