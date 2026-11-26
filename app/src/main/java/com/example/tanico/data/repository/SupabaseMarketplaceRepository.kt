package com.example.tanico.data.repository

import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.domain.model.MarketplaceProduct
import com.example.tanico.domain.repository.MarketplaceRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseMarketplaceRepository(
    private val clientProvider: SupabaseClientProvider
) : MarketplaceRepository {
    override suspend fun loadNearbyActiveProducts(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Result<List<MarketplaceProduct>> = try {
        check(clientProvider.isConfigured) {
            "Supabase belum dikonfigurasi. Isi URL dan publishable key di local.properties."
        }
        val client = clientProvider.client
        val products = client.postgrest.rpc(
            function = "get_nearby_active_products",
            parameters = buildJsonObject {
                put("p_latitude", latitude)
                put("p_longitude", longitude)
                put("p_radius_km", radiusKm)
            }
        )
            .decodeList<ProductRow>()
        val categories = client.from("categories")
            .select(columns = Columns.list("id", "name"))
            .decodeList<CategoryRow>()
            .associate { it.id to it.name }

        Result.success(
            products.map { product ->
                MarketplaceProduct(
                    id = product.id,
                    categoryName = categories[product.categoryId] ?: "Limbah pertanian",
                    name = product.name,
                    description = product.description,
                    price = product.price,
                    stock = product.stock,
                    unit = product.unit,
                    weightPerUnitKg = product.weightPerUnitKg,
                    distanceKm = product.distanceKm
                )
            }
        )
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    @Serializable
    private data class ProductRow(
        val id: String,
        @SerialName("category_id") val categoryId: String,
        val name: String,
        val description: String? = null,
        val price: Double,
        val stock: Int,
        val unit: String,
        @SerialName("weight_per_unit_kg") val weightPerUnitKg: Double,
        @SerialName("distance_km") val distanceKm: Double
    )

    @Serializable
    private data class CategoryRow(
        val id: String,
        val name: String
    )
}
