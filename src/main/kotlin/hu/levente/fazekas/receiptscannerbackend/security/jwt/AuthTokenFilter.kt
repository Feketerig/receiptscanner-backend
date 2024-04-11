package hu.levente.fazekas.receiptscannerbackend.security.jwt

import hu.levente.fazekas.receiptscannerbackend.security.service.UserDetailsImpl
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class AuthTokenFilter: OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt: String = parseJwt(request)
            if (jwtUtils.validateJwtToken(jwt)) {
                val user = jwtUtils.getUserFromJwtToken(jwt)
                val userDetails = UserDetailsImpl.build(user)
                val authentication =
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            println(e.message)
        }

        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String {
        val headerAuth = request.getHeader("Authorization")

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7)
        }else{
            throw IllegalArgumentException("No Authorization header found")
        }
    }
}