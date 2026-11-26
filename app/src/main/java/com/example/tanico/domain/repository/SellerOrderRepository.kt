package com.example.tanico.domain.repository

import com.example.tanico.domain.model.SellerOrder

/**
 * Orders of the authenticated seller's store. The store is resolved inside the
 * implementation. Status changes go through server-side RPCs
 * (see supabase/migrations/*_seller_order_actions.sql), never direct updates.
 */
interface SellerOrderRepository {
    suspend fun getMyOrders(): Result<List<SellerOrder>>

    suspend fun acceptOrder(orderId: String): Result<Unit>

    suspend fun rejectOrder(orderId: String, reason: String?): Result<Unit>
}
