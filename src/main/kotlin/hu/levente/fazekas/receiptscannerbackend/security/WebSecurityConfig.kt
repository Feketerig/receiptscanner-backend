package hu.levente.fazekas.receiptscannerbackend.security

import hu.levente.fazekas.receiptscannerbackend.security.jwt.AuthEntryPointJwt
import hu.levente.fazekas.receiptscannerbackend.security.jwt.AuthTokenFilter
import hu.levente.fazekas.receiptscannerbackend.security.service.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class WebSecurityConfig {

    @Autowired
    private lateinit var userDetailsService: UserDetailsServiceImpl

    @Autowired
    private lateinit var unauthorizedHandler: AuthEntryPointJwt

    @Bean
    fun authenticationJwtTokenFilter(): AuthTokenFilter {
        return AuthTokenFilter()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeRequests {
                authorize(AntPathRequestMatcher("/api/auth/register", HttpMethod.POST.name()), permitAll)
                authorize(AntPathRequestMatcher("/api/auth/login", HttpMethod.POST.name()), permitAll)
                authorize(AntPathRequestMatcher("/api/auth/refresh", HttpMethod.POST.name()), permitAll)
                authorize(AntPathRequestMatcher("/api/auth/logout", HttpMethod.POST.name()), permitAll)
                authorize(AntPathRequestMatcher("/*", HttpMethod.POST.name()), permitAll)
                authorize(AntPathRequestMatcher("/static/**", HttpMethod.POST.name()), permitAll)
                authorize(anyRequest, authenticated)
            }
            exceptionHandling {
                authenticationEntryPoint = unauthorizedHandler
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(authenticationJwtTokenFilter())

        }

        return http.build()
    }
}