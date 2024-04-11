package hu.levente.fazekas.receiptscannerbackend.controller.exceptions

import org.springframework.http.HttpStatus

sealed class AbstractException(val errorMessage: String, val httpStatus: HttpStatus): RuntimeException(errorMessage)

class NotFoundException(errorMessage: String): AbstractException(errorMessage, HttpStatus.NOT_FOUND)
class ForbiddenException(errorMessage: String): AbstractException(errorMessage, HttpStatus.FORBIDDEN)
class InternalServerErrorException(errorMessage: String): AbstractException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
class BadRequestException(errorMessage: String): AbstractException(errorMessage, HttpStatus.BAD_REQUEST)
class UnauthorizedException(errorMessage: String): AbstractException(errorMessage, HttpStatus.UNAUTHORIZED)
