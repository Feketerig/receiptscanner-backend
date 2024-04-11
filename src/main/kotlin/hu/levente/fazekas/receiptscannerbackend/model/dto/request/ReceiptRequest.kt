package hu.levente.fazekas.receiptscannerbackend.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptRequest (
    var clientId: Long = 0,
    var name: String = "",
    var date: Long = 0,
    var currency: String = "",
    var sumOfPrice: Long = 0,
    var description: String = "",
    var tags: String = "",
    var items: String = "",
    var syncVersion: Long = 0
)