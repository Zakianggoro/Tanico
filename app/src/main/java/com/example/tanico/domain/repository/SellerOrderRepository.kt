package com.example.tanico.domain.repository

import com.example.tanico.domain.model.SellerOrder

interface SellerOrderRepository {
    suspend fun getMyOrders(): Result<List<SellerOrder>>

    suspend fun acceptOrder(orderId: String): Result<Unit>

    suspend fun rejectOrder(orderId: String, reason: String?): Result<Unit>
}
