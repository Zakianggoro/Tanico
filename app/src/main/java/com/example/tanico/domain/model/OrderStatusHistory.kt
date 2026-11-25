package com.example.tanico.domain.model

import java.time.Instant

/** 
Audit trail entry for an order status transition (table: order_status_history).
Variabel-variabelnya masih belum ditentukan, nunggu tabel database jadi dulu
*/

data class OrderStatusHistory(
    val id: String,
    val orderId: String,
    val changedBy: String,
    val oldStatus: OrderStatus?,
    val newStatus: OrderStatus,
    val note: String?,
    val createdAt: Instant?
)