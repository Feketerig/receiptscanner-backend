package hu.levente.fazekas.receiptscannerbackend.security.service

import hu.levente.fazekas.receiptscannerbackend.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    private val password: String = "",
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("read"))
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object{
        fun build(user: User): UserDetailsImpl{
            return UserDetailsImpl(
                id = user.userId,
                name = user.name,
                email = user.email,
                password = user.password
            )
        }
    }
}