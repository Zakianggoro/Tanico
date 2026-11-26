package com.example.tanico.domain.model

import java.time.Instant

/** Product image metadata (table: product_images). Only the storage path is persisted. */
data class ProductImage(
    val id: String,
    val productId: String,
    val storagePath: String,
    val isPrimary: Boolean,
    val createdAt: Instant
)
