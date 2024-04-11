package hu.levente.fazekas.receiptscannerbackend.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsResponse(
    val id: Long,
    val name: String,
    val email: String
)