package com.example.tanico.domain.usecase

class ValidationException(val field: String, val reason: Reason) :
    IllegalArgumentException("Invalid $field: $reason") {
    enum class Reason { REQUIRED, NEGATIVE, OUT_OF_RANGE }
}

internal fun requireNotBlank(field: String, value: String?) {
    if (value.isNullOrBlank()) throw ValidationException(field, ValidationException.Reason.REQUIRED)
}

internal fun requireNonNegative(field: String, value: java.math.BigDecimal) {
    if (value.signum() < 0) throw ValidationException(field, ValidationException.Reason.NEGATIVE)
}

internal fun requireNonNegative(field: String, value: Int) {
    if (value < 0) throw ValidationException(field, ValidationException.Reason.NEGATIVE)
}

internal inline fun <T> validated(block: () -> Unit, action: () -> Result<T>): Result<T> =
    try { block(); action() } catch (e: ValidationException) { Result.failure(e) }
