package com.example.tanico.domain.model

import java.math.BigDecimal

/** Immutable purchase-line snapshot (table: order_items). */
data class SellerOrderItem(
    val id: String,
    val orderId: String,
    val productId: String?,             // nullable: line may outlive the product
    val productName: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
    val unit: String,
    val weightPerUnitKg: BigDecimal,
    val subtotal: BigDecimal
)