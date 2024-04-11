package hu.levente.fazekas.receiptscannerbackend.repository

import hu.levente.fazekas.receiptscannerbackend.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean
}