package hu.levente.fazekas.receiptscannerbackend.controller

import hu.levente.fazekas.receiptscannerbackend.model.dto.request.LoginRequest
import hu.levente.fazekas.receiptscannerbackend.model.dto.request.RefreshTokenRequest
import hu.levente.fazekas.receiptscannerbackend.model.dto.request.RegistrationRequest
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.LoginResponse
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.NewTokenResponse
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.UserDetailsResponse
import hu.levente.fazekas.receiptscannerbackend.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController @Autowired constructor(
    val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val loginResponse = authService.login(loginRequest)

        return ResponseEntity(loginResponse, HttpStatus.OK)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<NewTokenResponse> {
        val newTokenResponse = authService.refreshLogin(refreshTokenRequest.refreshToken)

        return ResponseEntity(newTokenResponse, HttpStatus.OK)
    }

    @PostMapping("/register")
    fun register(@RequestBody registrationRequest: RegistrationRequest): ResponseEntity<UserDetailsResponse> {
        val userDetailsResponse = authService.register(registrationRequest)

        return ResponseEntity(userDetailsResponse, HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<*> {
        authService.logout(refreshTokenRequest.refreshToken)

        return ResponseEntity(null, HttpStatus.NO_CONTENT)
    }
}