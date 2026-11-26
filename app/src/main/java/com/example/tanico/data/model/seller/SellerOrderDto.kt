package com.example.tanico.data.model.seller

import com.example.tanico.data.model.BigDecimalSerializer
import java.math.BigDecimal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
/**
 * Table: orders. Query with `select=*,order_items(*)` to fill [orderItems].
 * payment_method / payment_status / address_id are intentionally not mapped
 * (payment workflow is undecided; ignoreUnknownKeys must be enabled on the Json instance).
 */
@Serializable
data class SellerOrderDto(
    val id: String,
    @SerialName("buyer_id") val buyerId: String,
    @SerialName("store_id") val storeId: String,
    val status: String,
    @Serializable(with = BigDecimalSerializer::class) val subtotal: BigDecimal,
    @SerialName("shipping_fee")
    @Serializable(with = BigDecimalSerializer::class) val shippingFee: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val total: BigDecimal,
    @SerialName("shipping_distance_km")
    @Serializable(with = BigDecimalSerializer::class) val shippingDistanceKm: BigDecimal,
    @SerialName("total_weight_kg")
    @Serializable(with = BigDecimalSerializer::class) val totalWeightKg: BigDecimal,
    @SerialName("shipping_address_snapshot") val shippingAddressSnapshot: JsonElement,
    @SerialName("store_address_snapshot") val storeAddressSnapshot: JsonElement,
    @SerialName("shipping_calculation_details") val shippingCalculationDetails: JsonElement? = null,
    @SerialName("stock_restored_at") val stockRestoredAt: String? = null,
    @SerialName("order_items") val orderItems: List<SellerOrderItemDto> = emptyList(),
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

/** Table: order_items */
@Serializable
data class SellerOrderItemDto(
    val id: String,
    @SerialName("order_id") val orderId: String,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("product_name") val productName: String,
    @SerialName("unit_price")
    @Serializable(with = BigDecimalSerializer::class) val unitPrice: BigDecimal,
    val quantity: Int,
    val unit: String,
    @SerialName("weight_per_unit_kg")
    @Serializable(with = BigDecimalSerializer::class) val weightPerUnitKg: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val subtotal: BigDecimal
)
/** Table: order_status_history */
@Serializable
data class OrderStatusHistoryDto(
    val id: String,
    @SerialName("order_id") val orderId: String,
    @SerialName("changed_by") val changedBy: String? = null,
    @SerialName("old_status") val oldStatus: String? = null,
    @SerialName("new_status") val newStatus: String,
    val note: String? = null,
    @SerialName("created_at") val createdAt: String
)
