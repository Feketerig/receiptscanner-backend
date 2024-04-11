package hu.levente.fazekas.receiptscannerbackend.security.service

import hu.levente.fazekas.receiptscannerbackend.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class LoggedInUserService {

    fun getLoggedInUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication.principal as UserDetailsImpl

        val user = User(
            userId = userDetails.id,
            name = userDetails.name,
            email = userDetails.email,
            password = userDetails.password
        )

        return user
    }
}