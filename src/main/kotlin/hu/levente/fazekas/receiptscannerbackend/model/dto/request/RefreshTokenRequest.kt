package hu.levente.fazekas.receiptscannerbackend.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest (
    val refreshToken: String
)