package com.example.tanico.domain.repository

import com.example.tanico.domain.model.Category
import com.example.tanico.domain.model.Product
import com.example.tanico.domain.model.ProductInput

/**
 * All operations are scoped to the authenticated seller's own store. The store is
 * resolved inside the implementation (RLS + stores.owner_id is unique), so callers
 * never pass or trust a store id.
 *
 * Image upload/removal is NOT included yet: it needs the Supabase Storage module
 * (storage-kt) and the product-images bucket, which are not set up.
 */
interface SellerProductRepository {
    suspend fun getMyProducts(): Result<List<Product>>

    suspend fun getProduct(productId: String): Result<Product>

    suspend fun getCategories(): Result<List<Category>>

    /** Creates (input.id == null) or updates a product of the caller's store. */
    suspend fun saveProduct(input: ProductInput): Result<Product>

    /**
     * Sets stock to an absolute value via a direct update.
     * LIMITATION: not safe against a checkout deducting stock at the same moment;
     * replace with a row-locking RPC once migrations for it are approved.
     */
    suspend fun updateStock(productId: String, newStock: Int): Result<Product>

    /** Sets status to inactive instead of deleting, so order history stays intact. */
    suspend fun deactivateProduct(productId: String): Result<Unit>
}
