package hu.levente.fazekas.receiptscannerbackend.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class NewTokenResponse(
    val accessToken: String,
    val refreshToken: String
)