package hu.levente.fazekas.receiptscannerbackend.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val name: String,
    val email: String,
    val password: String
)