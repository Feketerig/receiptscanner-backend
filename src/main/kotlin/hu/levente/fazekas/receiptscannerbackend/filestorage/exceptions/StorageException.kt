package hu.levente.fazekas.receiptscannerbackend.filestorage.exceptions

sealed class StorageException(val errorMessage: String) : RuntimeException(errorMessage)

class CouldNotOpenFileException(errorMessage: String): StorageException(errorMessage)
class FileNotFoundException(errorMessage: String): StorageException(errorMessage)