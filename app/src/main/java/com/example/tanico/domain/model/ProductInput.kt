package com.example.tanico.domain.model

import java.math.BigDecimal
import java.time.Instant

/**
 * Form input for creating or updating a product.
 * [id] is null when creating. [storeId] is not included: the server must
 * resolve it from the authenticated seller's store, not trust the client.
 */
data class ProductInput(
    val id: String? = null,
    val categoryId: String,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Int,
    val unit: String,
    val weightPerUnitKg: BigDecimal,
    val status: ProductStatus
)