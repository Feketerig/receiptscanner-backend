package hu.levente.fazekas.receiptscannerbackend.service

import hu.levente.fazekas.receiptscannerbackend.controller.exceptions.NotFoundException
import hu.levente.fazekas.receiptscannerbackend.filestorage.FileStorageService
import hu.levente.fazekas.receiptscannerbackend.mapper.toDto
import hu.levente.fazekas.receiptscannerbackend.mapper.toModelWithUser
import hu.levente.fazekas.receiptscannerbackend.model.Receipt
import hu.levente.fazekas.receiptscannerbackend.model.dto.request.ReceiptRequest
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.ReceiptResponse
import hu.levente.fazekas.receiptscannerbackend.repository.ReceiptRepository
import hu.levente.fazekas.receiptscannerbackend.security.service.LoggedInUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ReceiptService @Autowired constructor(
    val receiptRepository: ReceiptRepository,
    val fileStorageService: FileStorageService,
    val loggedInUserService: LoggedInUserService
) {

    fun getAllReceipts(pageNumber: Int, pageSize: Int): List<ReceiptResponse>{
        val userId = loggedInUserService.getLoggedInUser().userId
        return receiptRepository.getReceiptsByUser_UserId(
            userId,
            PageRequest.of(pageNumber, pageSize, Sort.by("date").descending())
        ).map { it.toDto() }
    }

    fun getAllNewReceipts(lastSyncVersion: Long, pageNumber: Int, pageSize: Int): List<ReceiptResponse>{
        val userId = loggedInUserService.getLoggedInUser().userId
        return receiptRepository.getReceiptsByUser_UserIdAndSyncVersionIsGreaterThan(
            userId,
            lastSyncVersion,
            PageRequest.of(pageNumber, pageSize, Sort.by("date").descending())
        ).map { it.toDto() }
    }

    fun saveReceipts(receipts: List<ReceiptRequest>){
        val user = loggedInUserService.getLoggedInUser()
        val saveReceipts = receipts.map { receipt ->
            receipt.toModelWithUser(user)
        }
        receiptRepository.saveAll(saveReceipts)
    }

    fun deleteReceipt(localId: Long){
        val userId = loggedInUserService.getLoggedInUser().userId
        val receipt = receiptRepository.getFirstByClientIdAndUser_UserId(clientId = localId, userId = userId)
            ?: throw NotFoundException("Receipt not found")
        receipt.imageUri?.let {
            fileStorageService.delete(it)
        }
        receiptRepository.deleteById(receipt.receiptId)
    }

    fun updateReceipt(receiptRequest: ReceiptRequest){
        val user = loggedInUserService.getLoggedInUser()
        val receipt = receiptRepository.getFirstByClientIdAndUser_UserId(receiptRequest.clientId, user.userId) ?: throw NotFoundException("Receipt not found")
        val updatedReceipt = Receipt(
            receiptId = receipt.receiptId,
            clientId = receiptRequest.clientId,
            name = receiptRequest.name,
            date = receiptRequest.date,
            currency = receiptRequest.currency,
            sumOfPrice = receiptRequest.sumOfPrice,
            description = receiptRequest.description,
            imageUri = null,
            user = user,
            tags = receiptRequest.tags,
            items = receiptRequest.items,
            syncVersion = receiptRequest.syncVersion
        )
        receiptRepository.save(updatedReceipt)
    }

    fun updateImageUrl(clientId: Long, imageUrl: String){
        val userId = loggedInUserService.getLoggedInUser().userId
        val receipt = receiptRepository.getFirstByClientIdAndUser_UserId(clientId, userId)
            ?: throw NotFoundException("Receipt not found")
        receipt.imageUri?.let {
            fileStorageService.delete(it)
        }
        val updatedReceipt = Receipt(
            receiptId = receipt.receiptId,
            clientId = receipt.clientId,
            name = receipt.name,
            date = receipt.date,
            currency = receipt.currency,
            sumOfPrice = receipt.sumOfPrice,
            description = receipt.description,
            imageUri = imageUrl,
            user = receipt.user,
            tags = receipt.tags,
            items = receipt.items,
            syncVersion = receipt.syncVersion
        )
        receiptRepository.save(updatedReceipt)
    }
}