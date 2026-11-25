package com.example.tanico.domain.model

import java.math.BigDecimal
import java.time.Instant

/** Seller store (table: stores). Coordinates are the origin for shipping calculation. */
data class Store(
    val id: String,
    val ownerId: String,
    val storeName: String,
    val description: String?,
    val address: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val createdAt: Instant?,
    val updatedAt: Instant?
)