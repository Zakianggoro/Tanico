package com.example.tanico.domain.repository

import com.example.tanico.domain.model.SellerAnalytics

interface SellerAnalyticsRepository {
    suspend fun loadOverview(userId: String): Result<SellerAnalytics?>
}
