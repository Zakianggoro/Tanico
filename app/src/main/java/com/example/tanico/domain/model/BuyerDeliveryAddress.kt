package com.example.tanico.domain.model

data class BuyerDeliveryAddress(
    val id: String?,
    val userId: String,
    val recipientName: String,
    val phone: String,
    val address: String,
    val city: String,
    val postalCode: String?,
    val latitude: Double,
    val longitude: Double
)
