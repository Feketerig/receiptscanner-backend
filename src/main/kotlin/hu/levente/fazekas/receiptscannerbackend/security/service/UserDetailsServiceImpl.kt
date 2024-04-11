package hu.levente.fazekas.receiptscannerbackend.security.service

import hu.levente.fazekas.receiptscannerbackend.controller.exceptions.NotFoundException
import hu.levente.fazekas.receiptscannerbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserDetailsServiceImpl @Autowired constructor(
    val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails {
        val user = userRepository.findByEmail(userName)
            ?: throw NotFoundException("User not found")

        return UserDetailsImpl.build(user)
    }
}