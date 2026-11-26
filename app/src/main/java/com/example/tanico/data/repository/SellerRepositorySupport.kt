package com.example.tanico.data.repository

import com.example.tanico.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable

@Serializable
internal data class IdRow(val id: String)

/**
 * RLS (stores_select_own) returns only the caller's store and stores.owner_id is unique,
 * so this yields at most one row. Not cached on purpose: repositories are singletons and a
 * cached id could leak across logout/login of a different user.
 */
internal suspend fun SupabaseClientProvider.requireMyStoreId(): String =
    client.from("stores").select(Columns.raw("id"))
        .decodeSingleOrNull<IdRow>()?.id
        ?: error("Toko penjual tidak ditemukan.")

/** Same error-handling pattern as SupabaseSellerStoreRepository. */
internal suspend fun <T> SupabaseClientProvider.safeCall(block: suspend () -> T): Result<T> = try {
    check(isConfigured) {
        "Supabase belum dikonfigurasi. Isi URL dan publishable key di local.properties."
    }
    Result.success(block())
} catch (exception: CancellationException) {
    throw exception
} catch (exception: Exception) {
    Result.failure(exception)
}
