package com.example.tanico.data.repository

import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.domain.model.SellerAnalytics
import com.example.tanico.domain.model.SellerOrderMetric
import com.example.tanico.domain.usecase.calculateSellerAnalytics
import com.example.tanico.domain.repository.SellerAnalyticsRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.YearMonth
import java.time.ZoneOffset

class SupabaseSellerAnalyticsRepository(
    private val clientProvider: SupabaseClientProvider
) : SellerAnalyticsRepository {
    override suspend fun loadOverview(userId: String): Result<SellerAnalytics?> = try {
        check(clientProvider.isConfigured) {
            "Supabase belum dikonfigurasi. Isi URL dan publishable key di local.properties."
        }
        val client = clientProvider.client
        val store = client.from("stores")
            .select(columns = Columns.list("id")) {
                filter { eq("owner_id", userId) }
            }
            .decodeSingleOrNull<StoreRow>()
            ?: return Result.success(null)

        val currentMonth = YearMonth.now(ZoneOffset.UTC)
        val firstMonth = currentMonth.minusMonths(5)
        val since = firstMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC).toString()
        val orders = client.from("orders")
            .select(columns = Columns.list("status", "subtotal", "created_at")) {
                filter {
                    eq("store_id", store.id)
                    gte("created_at", since)
                }
            }
            .decodeList<OrderRow>()

        Result.success(
            calculateSellerAnalytics(
                orders = orders.map { SellerOrderMetric(it.status, it.subtotal, it.createdAt) },
                currentMonth = currentMonth
            )
        )
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    @Serializable
    private data class StoreRow(val id: String)

    @Serializable
    private data class OrderRow(
        val status: String,
        val subtotal: Double,
        @SerialName("created_at") val createdAt: String
    )
}
