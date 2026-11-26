package com.example.tanico.domain.repository

interface SellerStoreRepository {
    suspend fun createStore(
        ownerId: String,
        storeName: String,
        address: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit>
}
