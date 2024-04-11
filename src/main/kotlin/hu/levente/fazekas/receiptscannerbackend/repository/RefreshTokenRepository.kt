package hu.levente.fazekas.receiptscannerbackend.repository

import hu.levente.fazekas.receiptscannerbackend.model.RefreshToken
import hu.levente.fazekas.receiptscannerbackend.model.User
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository: CrudRepository<RefreshToken, Long> {

    fun findByToken(token: String): RefreshToken?

    fun findByExpirationDateIsLessThan(expirationDate: Long): List<RefreshToken>

    fun deleteByToken(token: String)

    fun deleteAllByUser(user: User)
}