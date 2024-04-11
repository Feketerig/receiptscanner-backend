package hu.levente.fazekas.receiptscannerbackend.security.jwt

import hu.levente.fazekas.receiptscannerbackend.model.User
import hu.levente.fazekas.receiptscannerbackend.security.service.UserDetailsImpl
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.MacAlgorithm
import io.jsonwebtoken.security.SignatureException
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*
import javax.crypto.SecretKey

@ConfigurationProperties("hu.levente.fazekas.receiptscannerbackend.auth")
data class JwtUtils(
    val jwtSecret: String,
    val jwtRefreshSecret: String,
    val jwtExpirationMs: String,
    val jwtRefreshExpirationMs: String
) {
    companion object{
        const val NAME = "name"
        const val ID = "id"
        val logger: Logger = LoggerFactory.getLogger(JwtUtils::class.java)
        val alg: MacAlgorithm = Jwts.SIG.HS512
        val key: SecretKey = alg.key().build()
    }

    fun generateJwtToken(userDetails: UserDetailsImpl): String {
        val expiration = Date(
            Clock.System.now()
                .plus(jwtExpirationMs.toLong(), DateTimeUnit.MILLISECOND)
                .toEpochMilliseconds()
        )
        return Jwts.builder()
            .subject(userDetails.email)
            .issuedAt(Date())
            .claim("type", "access")
            .claim(NAME, userDetails.name)
            .claim(ID, userDetails.id)
            .expiration(expiration)
            .signWith(key, alg)
            .compact()
    }

    fun generateJwtRefreshToken(userDetails: UserDetailsImpl): String {
        val expiration = Date(
            Clock.System.now()
                .plus(jwtExpirationMs.toLong(), DateTimeUnit.MILLISECOND)
                .toEpochMilliseconds()
        )
        return Jwts.builder()
            .subject(userDetails.email)
            .issuedAt(Date())
            .claim("type", "refresh")
            .expiration(expiration)
            .signWith(key, Jwts.SIG.HS512)
            .compact()
    }

    fun getUserFromJwtToken(token: String?): User {
        val payload = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        val email = payload.subject
        val name = payload[NAME] as String
        val id = payload[ID] as Int
        val user = User(userId = id.toLong(), name = name, email = email)
        return user
    }

    fun getEmailFromJwtRefreshToken(token: String?): String {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload.subject
    }

    fun validateJwtToken(authToken: String): Boolean {
        return validateToken(authToken)
    }

    fun getRefreshTokenExpiration(token: String): Date {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload.expiration
    }

    private fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }

        return false
    }
}