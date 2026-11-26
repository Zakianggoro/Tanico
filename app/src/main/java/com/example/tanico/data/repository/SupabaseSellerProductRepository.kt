package com.example.tanico.data.repository

import com.example.tanico.data.mapper.seller.toDomain
import com.example.tanico.data.mapper.seller.toInsertDto
import com.example.tanico.data.mapper.seller.toUpdateDto
import com.example.tanico.data.model.seller.CategoryDto
import com.example.tanico.data.model.seller.ProductDto
import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.domain.model.Category
import com.example.tanico.domain.model.Product
import com.example.tanico.domain.model.ProductInput
import com.example.tanico.domain.model.ProductStatus
import com.example.tanico.domain.repository.SellerProductRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable

class SupabaseSellerProductRepository(
    private val clientProvider: SupabaseClientProvider
) : SellerProductRepository {

    override suspend fun getMyProducts(): Result<List<Product>> = call {
        val storeId = requireMyStoreId()
        clientProvider.client.from(PRODUCTS).select(PRODUCT_COLUMNS) {
            filter { eq("store_id", storeId) }
            order("created_at", Order.DESCENDING)
        }.decodeList<ProductDto>().map { it.toDomain() }
    }

    override suspend fun getProduct(productId: String): Result<Product> = call {
        val storeId = requireMyStoreId()
        clientProvider.client.from(PRODUCTS).select(PRODUCT_COLUMNS) {
            filter {
                eq("id", productId)
                eq("store_id", storeId)   // active products of other stores are publicly readable
            }
        }.decodeSingle<ProductDto>().toDomain()
    }

    override suspend fun getCategories(): Result<List<Category>> = call {
        clientProvider.client.from("categories").select {
            order("name", Order.ASCENDING)
        }.decodeList<CategoryDto>().map { it.toDomain() }
    }

    override suspend fun saveProduct(input: ProductInput): Result<Product> = call {
        val saved = if (input.id == null) {
            clientProvider.client.from(PRODUCTS)
                .insert(input.toInsertDto(requireMyStoreId())) { select(PRODUCT_COLUMNS) }
                .decodeSingle<ProductDto>()
        } else {
            clientProvider.client.from(PRODUCTS)
                .update(input.toUpdateDto()) {
                    select(PRODUCT_COLUMNS)
                    filter { eq("id", input.id) }
                }
                .decodeSingle<ProductDto>()   // throws if no row matched (not yours / missing)
        }
        saved.toDomain()
    }

    override suspend fun updateStock(productId: String, newStock: Int): Result<Product> = call {
        clientProvider.client.from(PRODUCTS)
            .update({ set("stock", newStock) }) {
                select(PRODUCT_COLUMNS)
                filter { eq("id", productId) }
            }
            .decodeSingle<ProductDto>()
            .toDomain()
    }

    override suspend fun deactivateProduct(productId: String): Result<Unit> = call {
        clientProvider.client.from(PRODUCTS)
            .update({ set("status", ProductStatus.INACTIVE.value) }) {
                select(Columns.raw("id"))
                filter { eq("id", productId) }
            }
            .decodeSingle<IdRow>()
        Unit
    }

    /**
     * RLS (stores_select_own) returns only the caller's store and stores.owner_id is unique,
     * so this yields at most one row. Not cached on purpose: the repository is a singleton
     * and a cached id could leak across logout/login of a different user.
     */
    private suspend fun requireMyStoreId(): String =
        clientProvider.client.from("stores").select(Columns.raw("id"))
            .decodeSingleOrNull<IdRow>()?.id
            ?: error("Toko penjual tidak ditemukan.")

    private suspend fun <T> call(block: suspend () -> T): Result<T> = try {
        check(clientProvider.isConfigured) {
            "Supabase belum dikonfigurasi. Isi URL dan publishable key di local.properties."
        }
        Result.success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    @Serializable
    private data class IdRow(val id: String)

    private companion object {
        const val PRODUCTS = "products"
        val PRODUCT_COLUMNS: Columns = Columns.raw("*, product_images(*)")
    }
}
