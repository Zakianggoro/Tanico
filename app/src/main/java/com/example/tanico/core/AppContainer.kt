package com.example.tanico.core

import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.data.repository.SupabaseAuthRepository
import com.example.tanico.data.repository.SupabaseSellerAnalyticsRepository

object AppContainer {
    val authRepository by lazy { SupabaseAuthRepository(SupabaseClientProvider) }
    val sellerAnalyticsRepository by lazy {
        SupabaseSellerAnalyticsRepository(SupabaseClientProvider)
    }
}
