package com.example.tanico.domain.repository

import com.example.tanico.domain.model.BuyerDeliveryAddress

interface BuyerAddressRepository {
    suspend fun loadDefault(userId: String): Result<BuyerDeliveryAddress?>
    suspend fun saveDefault(address: BuyerDeliveryAddress): Result<BuyerDeliveryAddress>
}
