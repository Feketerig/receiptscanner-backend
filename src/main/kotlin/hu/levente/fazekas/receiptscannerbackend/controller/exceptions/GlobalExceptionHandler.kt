package hu.levente.fazekas.receiptscannerbackend.controller.exceptions

import hu.levente.fazekas.receiptscannerbackend.filestorage.exceptions.StorageException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(AbstractException::class)
    fun handleExceptions(e: AbstractException): ResponseEntity<String> {
        return ResponseEntity(e.errorMessage, e.httpStatus)
    }

    @ExceptionHandler(StorageException::class)
    fun handleExceptions(e: StorageException): ResponseEntity<String> {
        return ResponseEntity(e.errorMessage, HttpStatus.NOT_FOUND)
    }
}