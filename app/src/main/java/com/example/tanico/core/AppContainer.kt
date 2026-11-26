package com.example.tanico.core

import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.data.repository.SupabaseAuthRepository
import com.example.tanico.data.repository.SupabaseBuyerAddressRepository
import com.example.tanico.data.repository.SupabaseMarketplaceRepository
import com.example.tanico.data.repository.SupabaseSellerAnalyticsRepository
import com.example.tanico.data.repository.SupabaseSellerStoreRepository

object AppContainer {
    val authRepository by lazy { SupabaseAuthRepository(SupabaseClientProvider) }
    val marketplaceRepository by lazy { SupabaseMarketplaceRepository(SupabaseClientProvider) }
    val buyerAddressRepository by lazy { SupabaseBuyerAddressRepository(SupabaseClientProvider) }
    val sellerAnalyticsRepository by lazy {
        SupabaseSellerAnalyticsRepository(SupabaseClientProvider)
    }
    val sellerStoreRepository by lazy {
        SupabaseSellerStoreRepository(SupabaseClientProvider)
    }
}
