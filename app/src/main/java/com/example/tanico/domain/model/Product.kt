package com.example.tanico.domain.model

import java.math.BigDecimal
import java.time.Instant

/** Product listed by a store (table: products). ProductInput lives in its own file. */
data class Product(
    val id: String,
    val storeId: String,
    val categoryId: String,
    val name: String,
    val description: String?,
    val price: BigDecimal,              // IDR, >= 0
    val stock: Int,                     // >= 0
    val unit: String,                   // e.g. kg, karung, ikat
    val weightPerUnitKg: BigDecimal,    // used for weight-based shipping
    val status: ProductStatus,
    val images: List<ProductImage> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant
) {
    val primaryImage: ProductImage?
        get() = images.firstOrNull { it.isPrimary } ?: images.firstOrNull()
}
