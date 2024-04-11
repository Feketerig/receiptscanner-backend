package hu.levente.fazekas.receiptscannerbackend.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)