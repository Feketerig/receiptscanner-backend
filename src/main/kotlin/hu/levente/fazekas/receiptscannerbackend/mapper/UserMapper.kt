package hu.levente.fazekas.receiptscannerbackend.mapper

import hu.levente.fazekas.receiptscannerbackend.model.User
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.UserDetailsResponse

fun User.toUserResponse(): UserDetailsResponse = UserDetailsResponse(
    id = userId,
    name = name,
    email = email
)