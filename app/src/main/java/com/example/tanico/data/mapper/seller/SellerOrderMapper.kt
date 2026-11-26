package com.example.tanico.data.mapper.seller

import com.example.tanico.data.mapper.toInstant
import com.example.tanico.data.model.seller.OrderStatusHistoryDto
import com.example.tanico.data.model.seller.SellerOrderDto
import com.example.tanico.data.model.seller.SellerOrderItemDto
import com.example.tanico.domain.model.JsonSnapshot
import com.example.tanico.domain.model.OrderStatus
import com.example.tanico.domain.model.OrderStatusHistory
import com.example.tanico.domain.model.SellerOrder
import com.example.tanico.domain.model.SellerOrderItem

fun SellerOrderDto.toDomain() = SellerOrder(
    id = id,
    buyerId = buyerId,
    storeId = storeId,
    status = OrderStatus(status),
    subtotal = subtotal,
    shippingFee = shippingFee,
    total = total,
    shippingDistanceKm = shippingDistanceKm,
    totalWeightKg = totalWeightKg,
    shippingAddressSnapshot = JsonSnapshot(shippingAddressSnapshot.toString()),
    storeAddressSnapshot = JsonSnapshot(storeAddressSnapshot.toString()),
    shippingCalculationDetails = shippingCalculationDetails?.let { JsonSnapshot(it.toString()) },
    stockRestoredAt = stockRestoredAt?.toInstant(),
    items = orderItems.map { it.toDomain() },
    createdAt = createdAt.toInstant(),
    updatedAt = updatedAt.toInstant()
)

fun SellerOrderItemDto.toDomain() = SellerOrderItem(
    id = id,
    orderId = orderId,
    productId = productId,
    productName = productName,
    unitPrice = unitPrice,
    quantity = quantity,
    unit = unit,
    weightPerUnitKg = weightPerUnitKg,
    subtotal = subtotal
)

fun OrderStatusHistoryDto.toDomain() = OrderStatusHistory(
    id = id,
    orderId = orderId,
    changedBy = changedBy,
    oldStatus = oldStatus?.let(::OrderStatus),
    newStatus = OrderStatus(newStatus),
    note = note,
    createdAt = createdAt.toInstant()
)
