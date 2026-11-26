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

class SupabaseSellerProductRepository(
    private val clientProvider: SupabaseClientProvider
) : SellerProductRepository {

    override suspend fun getMyProducts(): Result<List<Product>> = clientProvider.safeCall {
        val storeId = clientProvider.requireMyStoreId()
        clientProvider.client.from(PRODUCTS).select(PRODUCT_COLUMNS) {
            filter { eq("store_id", storeId) }
            order("created_at", Order.DESCENDING)
        }.decodeList<ProductDto>().map { it.toDomain() }
    }

    override suspend fun getProduct(productId: String): Result<Product> = clientProvider.safeCall {
        val storeId = clientProvider.requireMyStoreId()
        clientProvider.client.from(PRODUCTS).select(PRODUCT_COLUMNS) {
            filter {
                eq("id", productId)
                eq("store_id", storeId) // active products of other stores are publicly readable
            }
        }.decodeSingle<ProductDto>().toDomain()
    }

    override suspend fun getCategories(): Result<List<Category>> = clientProvider.safeCall {
        clientProvider.client.from("categories").select {
            order("name", Order.ASCENDING)
        }.decodeList<CategoryDto>().map { it.toDomain() }
    }

    override suspend fun saveProduct(input: ProductInput): Result<Product> = clientProvider.safeCall {
        val saved = if (input.id == null) {
            clientProvider.client.from(PRODUCTS)
                .insert(input.toInsertDto(clientProvider.requireMyStoreId())) { select(PRODUCT_COLUMNS) }
                .decodeSingle<ProductDto>()
        } else {
            clientProvider.client.from(PRODUCTS)
                .update(input.toUpdateDto()) {
                    select(PRODUCT_COLUMNS)
                    filter { eq("id", input.id) }
                }
                .decodeSingle<ProductDto>() // throws if no row matched (not yours / missing)
        }
        saved.toDomain()
    }

    override suspend fun updateStock(productId: String, newStock: Int): Result<Product> =
        clientProvider.safeCall {
            clientProvider.client.from(PRODUCTS)
                .update({ set("stock", newStock) }) {
                    select(PRODUCT_COLUMNS)
                    filter { eq("id", productId) }
                }
                .decodeSingle<ProductDto>()
                .toDomain()
        }

    override suspend fun deactivateProduct(productId: String): Result<Unit> = clientProvider.safeCall {
        clientProvider.client.from(PRODUCTS)
            .update({ set("status", ProductStatus.INACTIVE.value) }) {
                select(Columns.raw("id"))
                filter { eq("id", productId) }
            }
            .decodeSingle<IdRow>()
        Unit
    }

    private companion object {
        const val PRODUCTS = "products"
        val PRODUCT_COLUMNS: Columns = Columns.raw("*, product_images(*)")
    }
}
