package hu.levente.fazekas.receiptscannerbackend.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val userDetails: UserDetailsResponse,
    val accessToken: String,
    val refreshToken: String,
)