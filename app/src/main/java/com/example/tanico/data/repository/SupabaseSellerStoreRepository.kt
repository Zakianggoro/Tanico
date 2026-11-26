package com.example.tanico.data.repository

import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.domain.repository.SellerStoreRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseSellerStoreRepository(
    private val clientProvider: SupabaseClientProvider
) : SellerStoreRepository {
    override suspend fun createStore(
        ownerId: String,
        storeName: String,
        address: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> = try {
        check(clientProvider.isConfigured) {
            "Supabase belum dikonfigurasi. Isi URL dan publishable key di local.properties."
        }
        clientProvider.client.from("stores").insert(
            StoreInsert(
                ownerId = ownerId,
                storeName = storeName.trim(),
                address = address.trim(),
                latitude = latitude,
                longitude = longitude
            )
        )
        Result.success(Unit)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    @Serializable
    private data class StoreInsert(
        @SerialName("owner_id") val ownerId: String,
        @SerialName("store_name") val storeName: String,
        val address: String,
        val latitude: Double,
        val longitude: Double
    )
}
