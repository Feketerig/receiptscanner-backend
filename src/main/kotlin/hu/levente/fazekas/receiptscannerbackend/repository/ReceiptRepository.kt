package hu.levente.fazekas.receiptscannerbackend.repository

import hu.levente.fazekas.receiptscannerbackend.model.Receipt
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ReceiptRepository: JpaRepository<Receipt, Long> /*CrudRepository<Receipt, Long>*/{

    fun getReceiptsByUser_UserIdAndSyncVersionIsGreaterThan(userId: Long, syncVersion: Long, pageable: Pageable): List<Receipt>

    fun getReceiptsByUser_UserId(userId: Long, pageable: Pageable): List<Receipt>

    fun getFirstByClientIdAndUser_UserId(clientId: Long, userId: Long): Receipt?
}