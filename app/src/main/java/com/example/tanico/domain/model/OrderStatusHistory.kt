package com.example.tanico.domain.model

import java.time.Instant

/** Audit entry untuk perintah transisi status (tabel: order_status_history). */
data class OrderStatusHistory(
    val id: String,
    val orderId: String,
    val changedBy: String?,   // nullable di database
    val oldStatus: OrderStatus?,
    val newStatus: OrderStatus,
    val note: String?,
    val createdAt: Instant
)