package hu.levente.fazekas.receiptscannerbackend.mapper

import hu.levente.fazekas.receiptscannerbackend.model.Receipt
import hu.levente.fazekas.receiptscannerbackend.model.User
import hu.levente.fazekas.receiptscannerbackend.model.dto.request.ReceiptRequest
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.ReceiptResponse


fun Receipt.toDto(): ReceiptResponse = ReceiptResponse(
    clientId = clientId,
    name = name,
    date = date,
    currency = currency,
    sumOfPrice = sumOfPrice,
    description = description,
    tags = tags,
    items = items,
    syncVersion = syncVersion,
    imageUri = imageUri
)

fun ReceiptRequest.toModelWithUser(user: User): Receipt = Receipt(
    clientId = clientId,
    name = name,
    date = date,
    currency = currency,
    sumOfPrice = sumOfPrice,
    description = description,
    user = user,
    items = items,
    tags = tags,
    syncVersion = syncVersion
)