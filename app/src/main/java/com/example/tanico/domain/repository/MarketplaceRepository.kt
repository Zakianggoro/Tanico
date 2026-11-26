package com.example.tanico.domain.repository

import com.example.tanico.domain.model.MarketplaceProduct

interface MarketplaceRepository {
    suspend fun loadActiveProducts(): Result<List<MarketplaceProduct>>
}
