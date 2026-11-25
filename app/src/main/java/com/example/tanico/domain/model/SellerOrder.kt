package com.example.tanico.domain.model

import java.math.BigDecimal
import java.time.Instant

/**
 * Order as seen by the seller (table: orders). One order = one buyer + one store.
 *
 * Payment fields (payment_method, payment_status) are intentionally omitted:
 * the payment workflow is undecided. Do not show or assume a "paid" state.
 */
data class SellerOrder(
    val id: String,
    val buyerId: String,
    val storeId: String,
    val status: OrderStatus,
    val subtotal: BigDecimal,
    val shippingFee: BigDecimal,
    val total: BigDecimal,                       // subtotal + shipping_fee
    val shippingDistanceKm: BigDecimal,
    val totalWeightKg: BigDecimal,
    val shippingAddressSnapshot: JsonSnapshot,
    val storeAddressSnapshot: JsonSnapshot,
    val shippingCalculationDetails: JsonSnapshot?,
    val stockRestoredAt: Instant?,
    val items: List<SellerOrderItem> = emptyList(),
    val createdAt: Instant?,
    val updatedAt: Instant?
)