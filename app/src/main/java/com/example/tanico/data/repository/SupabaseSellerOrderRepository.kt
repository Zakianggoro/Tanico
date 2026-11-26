package com.example.tanico.data.repository

import com.example.tanico.data.mapper.seller.toDomain
import com.example.tanico.data.model.seller.SellerOrderDto
import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.domain.model.SellerOrder
import com.example.tanico.domain.repository.SellerOrderRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseSellerOrderRepository(
    private val clientProvider: SupabaseClientProvider
) : SellerOrderRepository {

    override suspend fun getMyOrders(): Result<List<SellerOrder>> = clientProvider.safeCall {
        val storeId = clientProvider.requireMyStoreId()
        clientProvider.client.from("orders").select(Columns.raw("*, order_items(*)")) {
            filter { eq("store_id", storeId) }
            order("created_at", Order.DESCENDING)
            limit(50)
        }.decodeList<SellerOrderDto>().map { it.toDomain() }
    }

    override suspend fun acceptOrder(orderId: String): Result<Unit> = clientProvider.safeCall {
        clientProvider.client.postgrest.rpc(
            function = "seller_accept_order",
            parameters = buildJsonObject { put("p_order_id", orderId) }
        )
        Unit
    }

    override suspend fun rejectOrder(orderId: String, reason: String?): Result<Unit> =
        clientProvider.safeCall {
            clientProvider.client.postgrest.rpc(
                function = "seller_reject_order",
                parameters = buildJsonObject {
                    put("p_order_id", orderId)
                    put("p_reason", reason)
                }
            )
            Unit
        }
}
