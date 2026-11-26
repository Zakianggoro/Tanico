package com.example.tanico.domain.model

import java.math.BigDecimal

/**
 * Form input for creating or updating a product.
 * [id] is null when creating. There is no store id on purpose: the repository resolves
 * the caller's own store, and RLS verifies ownership on the server.
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
