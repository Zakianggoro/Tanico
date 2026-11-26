package com.example.tanico.domain.model

data class MarketplaceProduct(
    val id: String,
    val categoryName: String,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val unit: String,
    val weightPerUnitKg: Double
)
