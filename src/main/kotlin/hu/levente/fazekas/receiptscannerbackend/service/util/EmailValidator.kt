package hu.levente.fazekas.receiptscannerbackend.service.util

import java.util.regex.Pattern

object EmailValidator {

    fun validateEmail(email: String): Boolean {
        val emailPattern =
            Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$")
        val emailMatcher = emailPattern.matcher(email)

        return emailMatcher.matches()
    }
}