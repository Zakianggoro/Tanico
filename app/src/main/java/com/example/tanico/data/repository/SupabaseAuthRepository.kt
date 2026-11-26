package com.example.tanico.data.repository

import com.example.tanico.core.session.AppSessionState
import com.example.tanico.data.remote.SupabaseClientProvider
import com.example.tanico.domain.model.RegistrationOutcome
import com.example.tanico.domain.model.RegistrationRequest
import com.example.tanico.domain.model.UserProfile
import com.example.tanico.domain.model.UserRole
import com.example.tanico.domain.model.isValidEmailAddress
import com.example.tanico.domain.model.normalizeIndonesianPhoneNumber
import com.example.tanico.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class SupabaseAuthRepository(
    private val clientProvider: SupabaseClientProvider
) : AuthRepository {
    override val isConfigured: Boolean
        get() = clientProvider.isConfigured

    override fun observeSession(): Flow<AppSessionState> {
        if (!isConfigured) return flowOf(AppSessionState.ConfigurationMissing)

        return clientProvider.client.auth.sessionStatus
            .map { status ->
                try {
                    when (status) {
                    SessionStatus.Initializing -> AppSessionState.Loading
                    is SessionStatus.NotAuthenticated -> AppSessionState.Unauthenticated
                    is SessionStatus.RefreshFailure -> AppSessionState.Failure(
                        "Sesi berakhir. Silakan masuk kembali."
                    )
                    is SessionStatus.Authenticated -> {
                        val user = status.session.user
                            ?: return@map AppSessionState.ProfileMissing
                        val profile = loadProfile(user.id)
                            ?: return@map AppSessionState.ProfileMissing
                        val role = UserRole.entries.firstOrNull {
                            it.storageValue == profile.role
                        } ?: return@map AppSessionState.Unauthorized
                        val store = if (role == UserRole.Seller) loadStore(user.id) else null
                        AppSessionState.Authenticated(
                            UserProfile(
                                id = profile.id,
                                fullName = profile.fullName,
                                email = profile.email,
                                phone = profile.phone,
                                role = role,
                                location = user.userMetadata
                                    ?.get("location")
                                    ?.jsonPrimitive
                                    ?.contentOrNull,
                                storeName = store?.storeName,
                                storeAddress = store?.address
                            )
                        )
                    }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    AppSessionState.Failure(exception.userMessage())
                }
            }
    }

    override suspend fun signIn(identifier: String, password: String): Result<Unit> = safely {
        require(isValidEmailAddress(identifier)) { "Masukkan alamat email yang valid." }
        clientProvider.client.auth.signInWith(Email) {
            email = identifier.trim()
            this.password = password
        }
    }

    override suspend fun signUp(request: RegistrationRequest): Result<RegistrationOutcome> = safely {
        val client = clientProvider.client
        val phone = normalizeIndonesianPhoneNumber(request.phone)
            ?: error("Gunakan nomor telepon Indonesia yang valid, misalnya +6281234567890.")
        val metadata = buildJsonObject {
            put("full_name", request.fullName.trim())
            put("phone", phone)
            put("location", request.location.trim())
            put("role", request.role.storageValue)
            if (request.role == UserRole.Seller) {
                val storeName = request.storeName?.trim()?.takeIf(String::isNotEmpty)
                    ?: error("Nama toko wajib diisi untuk akun penjual.")
                val storeAddress = request.storeAddress?.trim()?.takeIf(String::isNotEmpty)
                    ?: error("Alamat lengkap toko wajib diisi untuk akun penjual.")
                val latitude = request.storeLatitude
                    ?.takeIf { it.isFinite() && it in -90.0..90.0 }
                    ?: error("Latitude toko harus berupa angka antara -90 dan 90.")
                val longitude = request.storeLongitude
                    ?.takeIf { it.isFinite() && it in -180.0..180.0 }
                    ?: error("Longitude toko harus berupa angka antara -180 dan 180.")
                put("store_name", storeName)
                put("store_address", storeAddress)
                put("store_latitude", latitude.toString())
                put("store_longitude", longitude.toString())
            }
        }
        val email = request.email.trim()
        require(isValidEmailAddress(email)) { "Masukkan alamat email yang valid." }
        client.auth.signUpWith(Email) {
            this.email = email
            password = request.password
            data = metadata
        }

        RegistrationOutcome(verificationRequired = client.auth.currentSessionOrNull() == null)
    }

    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> = safely {
        val client = clientProvider.client
        val updated = client.from("profiles").update(
            ProfileUpdate(fullName = profile.fullName.trim())
        ) {
            select()
            filter { eq("id", profile.id) }
        }.decodeSingle<ProfileRow>()
        profile.copy(
            fullName = updated.fullName,
            email = updated.email,
            phone = updated.phone
        )
    }

    override suspend fun signOut(): Result<Unit> = safely {
        clientProvider.client.auth.signOut()
    }

    private suspend fun loadProfile(userId: String): ProfileRow? = clientProvider.client
        .from("profiles")
        .select(columns = Columns.list("id", "full_name", "email", "phone", "role")) {
            filter { eq("id", userId) }
        }
        .decodeSingleOrNull<ProfileRow>()

    private suspend fun loadStore(userId: String): StoreRow? = clientProvider.client
        .from("stores")
        .select(columns = Columns.list("store_name", "address")) {
            filter { eq("owner_id", userId) }
        }
        .decodeSingleOrNull<StoreRow>()

    private suspend fun <T> safely(block: suspend () -> T): Result<T> = try {
        if (!isConfigured) error("Supabase belum dikonfigurasi. Isi URL dan publishable key di local.properties.")
        Result.success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    private fun Throwable.userMessage(): String = message
        ?.takeIf(String::isNotBlank)
        ?: "Tidak dapat terhubung ke layanan TANICO. Coba lagi."

    @Serializable
    private data class ProfileRow(
        val id: String,
        @SerialName("full_name") val fullName: String,
        val email: String? = null,
        val phone: String? = null,
        val role: String
    )

    @Serializable
    private data class ProfileUpdate(
        @SerialName("full_name") val fullName: String
    )

    @Serializable
    private data class StoreRow(
        @SerialName("store_name") val storeName: String,
        val address: String
    )
}
