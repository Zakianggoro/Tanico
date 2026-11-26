package com.example.tanico.domain.model

data class UserProfile(
    val id: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val role: UserRole,
    val location: String?
)

data class RegistrationRequest(
    val fullName: String,
    val phone: String,
    val email: String,
    val location: String,
    val role: UserRole,
    val password: String
)

data class RegistrationOutcome(val verificationRequired: Boolean)

fun isValidEmailAddress(value: String): Boolean =
    Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(value.trim())

fun normalizeIndonesianPhoneNumber(value: String): String? {
    val trimmed = value.trim()
    val digits = trimmed.filter(Char::isDigit)
    if (digits.length !in 9..15) return null

    return when {
        trimmed.startsWith("+") -> "+$digits"
        digits.startsWith("0") -> "+62${digits.drop(1)}"
        digits.startsWith("62") -> "+$digits"
        else -> null
    }
}
