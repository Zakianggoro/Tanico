package com.example.tanico.domain.repository

import com.example.tanico.domain.model.MarketplaceProduct

interface MarketplaceRepository {
    suspend fun loadAllActiveProductsForDebug(): Result<List<MarketplaceProduct>>

    suspend fun loadNearbyActiveProducts(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Result<List<MarketplaceProduct>>
}
