package com.example.tanico.data.repository

import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.domain.model.BuyerDeliveryAddress
import com.example.tanico.domain.repository.BuyerAddressRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseBuyerAddressRepository(
    private val clientProvider: SupabaseClientProvider
) : BuyerAddressRepository {
    override suspend fun loadDefault(userId: String): Result<BuyerDeliveryAddress?> = try {
        check(clientProvider.isConfigured) {
            "Supabase belum dikonfigurasi. Isi URL dan publishable key di local.properties."
        }
        val address = clientProvider.client.from("user_addresses")
            .select(
                columns = Columns.list(
                    "id",
                    "user_id",
                    "recipient_name",
                    "phone",
                    "address",
                    "city",
                    "postal_code",
                    "latitude",
                    "longitude",
                    "is_default"
                )
            ) {
                filter { eq("user_id", userId) }
            }
            .decodeList<AddressRow>()
            .let { addresses -> addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull() }

        Result.success(address?.toDomain())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    override suspend fun saveDefault(
        address: BuyerDeliveryAddress
    ): Result<BuyerDeliveryAddress> = try {
        check(clientProvider.isConfigured) {
            "Supabase belum dikonfigurasi. Isi URL dan publishable key di local.properties."
        }
        val client = clientProvider.client
        val payload = AddressWrite(
            userId = address.userId,
            recipientName = address.recipientName.trim(),
            phone = address.phone.trim(),
            address = address.address.trim(),
            city = address.city.trim(),
            postalCode = address.postalCode?.trim()?.takeIf(String::isNotEmpty),
            latitude = address.latitude,
            longitude = address.longitude,
            isDefault = true
        )
        val saved = if (address.id == null) {
            client.from("user_addresses")
                .insert(payload) { select() }
                .decodeSingle<AddressRow>()
        } else {
            client.from("user_addresses")
                .update(payload) {
                    select()
                    filter {
                        eq("id", address.id)
                        eq("user_id", address.userId)
                    }
                }
                .decodeSingle<AddressRow>()
        }

        Result.success(saved.toDomain())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    private fun AddressRow.toDomain() = BuyerDeliveryAddress(
        id = id,
        userId = userId,
        recipientName = recipientName,
        phone = phone,
        address = address,
        city = city,
        postalCode = postalCode,
        latitude = latitude,
        longitude = longitude
    )

    @Serializable
    private data class AddressRow(
        val id: String,
        @SerialName("user_id") val userId: String,
        @SerialName("recipient_name") val recipientName: String,
        val phone: String,
        val address: String,
        val city: String,
        @SerialName("postal_code") val postalCode: String? = null,
        val latitude: Double,
        val longitude: Double,
        @SerialName("is_default") val isDefault: Boolean
    )

    @Serializable
    private data class AddressWrite(
        @SerialName("user_id") val userId: String,
        @SerialName("recipient_name") val recipientName: String,
        val phone: String,
        val address: String,
        val city: String,
        @SerialName("postal_code") val postalCode: String?,
        val latitude: Double,
        val longitude: Double,
        @SerialName("is_default") val isDefault: Boolean
    )
}
