package com.example.tanico.domain.model

import java.time.Instant

/** Product category (table: categories). Needed by the seller product form. */
data class Category(
    val id: String,
    val name: String,
    val description: String?,
    val createdAt: Instant
)
