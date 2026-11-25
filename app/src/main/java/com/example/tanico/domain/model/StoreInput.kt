package com.example.tanico.domain.model

import java.math.BigDecimal
import java.time.Instant

/** Editable store fields. Owner is derived server-side from the session, never from the client. */
data class StoreInput(
    val storeName: String,
    val description: String?,
    val address: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal
)