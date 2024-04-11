package hu.levente.fazekas.receiptscannerbackend.service

import hu.levente.fazekas.receiptscannerbackend.controller.exceptions.BadRequestException
import hu.levente.fazekas.receiptscannerbackend.controller.exceptions.NotFoundException
import hu.levente.fazekas.receiptscannerbackend.controller.exceptions.UnauthorizedException
import hu.levente.fazekas.receiptscannerbackend.mapper.toUserResponse
import hu.levente.fazekas.receiptscannerbackend.model.RefreshToken
import hu.levente.fazekas.receiptscannerbackend.model.User
import hu.levente.fazekas.receiptscannerbackend.model.dto.request.LoginRequest
import hu.levente.fazekas.receiptscannerbackend.model.dto.request.RegistrationRequest
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.LoginResponse
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.NewTokenResponse
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.UserDetailsResponse
import hu.levente.fazekas.receiptscannerbackend.repository.RefreshTokenRepository
import hu.levente.fazekas.receiptscannerbackend.repository.UserRepository
import hu.levente.fazekas.receiptscannerbackend.security.jwt.JwtUtils
import hu.levente.fazekas.receiptscannerbackend.security.service.UserDetailsImpl
import hu.levente.fazekas.receiptscannerbackend.service.util.EmailValidator
import kotlinx.datetime.Clock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import kotlin.jvm.optionals.getOrNull

@Service
@EnableScheduling
class AuthService @Autowired constructor(
    val authenticationManager: AuthenticationManager,
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtUtils: JwtUtils,
    val refreshTokenRepository: RefreshTokenRepository
) {

    fun login(loginRequest: LoginRequest): LoginResponse {
        val authentication = try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )
        } catch (e: AuthenticationException) {
            throw UnauthorizedException("User not found")
        }
        SecurityContextHolder.getContext().authentication = authentication
        val userDetails = authentication.principal as UserDetailsImpl
        val jwt = jwtUtils.generateJwtToken(userDetails)
        val refreshJwt = jwtUtils.generateJwtRefreshToken(userDetails)

        val user = userRepository.findById(userDetails.id).getOrNull() ?: throw NotFoundException("User not found")
        saveRefreshToken(refreshJwt, user)

        return LoginResponse(
            userDetails = user.toUserResponse(),
            accessToken = jwt,
            refreshToken = refreshJwt
        )
    }

    @Transactional
    fun refreshLogin(refreshToken: String): NewTokenResponse {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw UnauthorizedException("Wrong refresh token")
        }

        val refreshTokenHash = refreshToken.hashedWithSha256()
        val storedToken = refreshTokenRepository.findByToken(refreshTokenHash)
            ?: throw UnauthorizedException("Wrong refresh token")

        refreshTokenRepository.deleteByToken(storedToken.token)

        val email = jwtUtils.getEmailFromJwtRefreshToken(refreshToken)
        val user = userRepository.findByEmail(email)
            ?: throw UnauthorizedException("User not found")

        val userDetails = UserDetailsImpl.build(user)
        val jwt = jwtUtils.generateJwtToken(userDetails)
        val refreshJwt = jwtUtils.generateJwtRefreshToken(userDetails)

        return NewTokenResponse(
            accessToken = jwt,
            refreshToken = refreshJwt
        )
    }

    fun register(registrationRequest: RegistrationRequest): UserDetailsResponse {
        if (registrationRequest.name.length < 3) {
            throw BadRequestException("Error: Name should be at least 3 characters long!")
        }

        val email = registrationRequest.email.lowercase()
        if (!EmailValidator.validateEmail(email)) {
            throw BadRequestException("Error: Email is invalid!")
        }

        if (userRepository.existsByEmail(email)) {
            throw BadRequestException("Error: Email is already in use!")
        }

        if (registrationRequest.password.length < 8) {
            throw BadRequestException("Error: Password should be at least 8 character!")
        }

        val user = userRepository.save(
            User(
                name = registrationRequest.name,
                email = registrationRequest.email,
                password = passwordEncoder.encode(registrationRequest.password)
            )
        )
        return user.toUserResponse()
    }

    @Transactional
    fun logout(refreshToken: String) {
        val tokenHash = refreshToken.hashedWithSha256()
        refreshTokenRepository.deleteByToken(tokenHash)
//        val email = jwtUtils.getEmailFromJwtRefreshToken(refreshToken)
//        val user = userRepository.findByEmail(email)
//            ?: throw UnauthorizedException("User not found")
//        refreshTokenRepository.deleteAllByUser(user)
    }

    private fun saveRefreshToken(token: String, user: User){
        val tokenHash = token.hashedWithSha256()
        val expiration = jwtUtils.getRefreshTokenExpiration(token)
        refreshTokenRepository.save(
            RefreshToken(
                token = tokenHash,
                expirationDate = expiration.time,
                user = user
            )
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun String.hashedWithSha256() =
        MessageDigest.getInstance("SHA-256")
            .digest(toByteArray())
            .toHexString()

    @Scheduled(cron = "0 3 * * * ?", zone = "Europe/Budapest")
    protected fun clearExpiredRefreshTokens() {
        val expired: List<RefreshToken> = refreshTokenRepository.findByExpirationDateIsLessThan(Clock.System.now().toEpochMilliseconds())
        refreshTokenRepository.deleteAll(expired)
    }
}