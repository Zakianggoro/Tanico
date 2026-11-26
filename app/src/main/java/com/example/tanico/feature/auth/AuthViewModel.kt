package com.example.tanico.feature.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tanico.core.AppContainer
import com.example.tanico.core.navigation.AppDestination
import com.example.tanico.core.session.AppSessionState
import com.example.tanico.domain.model.RegistrationRequest
import com.example.tanico.domain.model.isValidEmailAddress
import com.example.tanico.domain.model.UserRole
import com.example.tanico.domain.repository.AuthRepository
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val repository: AuthRepository = AppContainer.authRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AuthViewModel::class.java))
        return AuthViewModel(repository) as T
    }
}

enum class AuthPage {
    Login,
    Registration,
    EmailVerification
}

data class AuthUiState(
    val destination: AppDestination = AppDestination.Authentication,
    val sessionState: AppSessionState = AppSessionState.Loading,
    val authPage: AuthPage = AuthPage.Login,
    val selectedRole: UserRole = UserRole.Buyer,
    val loginIdentifier: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val location: String = "",
    val storeName: String = "",
    val storeAddress: String = "",
    val storeLatitude: String = "",
    val storeLongitude: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false,
    val message: String? = null,
    val messageIsError: Boolean = true,
    val editingProfile: Boolean = false,
    val isLoading: Boolean = false,
    val isPreviewMode: Boolean = false,
    val isPreviewAvailable: Boolean = true
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val mutableUiState = mutableStateOf(
        AuthUiState(isPreviewAvailable = !repository.isConfigured)
    )
    val uiState: State<AuthUiState> = mutableUiState

    init {
        viewModelScope.launch {
            repository.observeSession().collect(::applySessionState)
        }
    }

    fun showLogin() = update {
        it.copy(
            authPage = AuthPage.Login,
            password = "",
            confirmPassword = "",
            message = null
        )
    }

    fun showRegistration() = update {
        it.copy(
            authPage = AuthPage.Registration,
            password = "",
            confirmPassword = "",
            message = null
        )
    }

    fun selectRole(role: UserRole) = update {
        it.copy(selectedRole = role, message = null)
    }

    fun updateLoginIdentifier(value: String) = update { it.copy(loginIdentifier = value, message = null) }
    fun updateFullName(value: String) = update { it.copy(fullName = value, message = null) }
    fun updatePhoneNumber(value: String) = update { it.copy(phoneNumber = value, message = null) }
    fun updateEmail(value: String) = update { it.copy(email = value, message = null) }
    fun updateLocation(value: String) = update { it.copy(location = value, message = null) }
    fun updateStoreName(value: String) = update { it.copy(storeName = value, message = null) }
    fun updateStoreAddress(value: String) = update { it.copy(storeAddress = value, message = null) }
    fun updateStoreLatitude(value: String) = update { it.copy(storeLatitude = value, message = null) }
    fun updateStoreLongitude(value: String) = update { it.copy(storeLongitude = value, message = null) }
    fun updatePassword(value: String) = update { it.copy(password = value, message = null) }
    fun updateConfirmPassword(value: String) = update { it.copy(confirmPassword = value, message = null) }
    fun updateTermsAccepted(value: Boolean) = update { it.copy(termsAccepted = value, message = null) }

    fun submitLogin() {
        val state = mutableUiState.value
        val validationMessage = when {
            state.loginIdentifier.isBlank() -> "Masukkan alamat email."
            !isValidEmailAddress(state.loginIdentifier) -> "Masukkan alamat email yang valid."
            state.password.isBlank() -> "Masukkan kata sandi Anda."
            else -> null
        }
        if (validationMessage != null) {
            update { it.copy(message = validationMessage, messageIsError = true) }
            return
        }
        launchRequest {
            repository.signIn(state.loginIdentifier, state.password)
                .onSuccess {
                    update { it.copy(message = "Berhasil masuk. Memuat profil Anda.", messageIsError = false) }
                }
                .onFailure { showFailure(it) }
        }
    }

    fun submitRegistration() {
        val state = mutableUiState.value
        val validationMessage = when {
            state.fullName.isBlank() -> "Masukkan nama lengkap Anda."
            state.phoneNumber.isBlank() -> "Masukkan nomor telepon aktif."
            !isValidEmailAddress(state.email) -> "Masukkan alamat email yang valid."
            state.location.isBlank() -> "Pilih domisili kabupaten atau kota."
            state.selectedRole == UserRole.Seller && state.storeName.isBlank() -> "Masukkan nama toko."
            state.selectedRole == UserRole.Seller && state.storeAddress.isBlank() -> "Masukkan alamat lengkap toko."
            state.selectedRole == UserRole.Seller && !isCoordinateInRange(state.storeLatitude, -90.0, 90.0) ->
                "Latitude toko harus berupa angka antara -90 dan 90."
            state.selectedRole == UserRole.Seller && !isCoordinateInRange(state.storeLongitude, -180.0, 180.0) ->
                "Longitude toko harus berupa angka antara -180 dan 180."
            state.password.length < 8 -> "Kata sandi harus terdiri dari minimal 8 karakter."
            state.password != state.confirmPassword -> "Konfirmasi kata sandi belum cocok."
            !state.termsAccepted -> "Setujui syarat dan ketentuan untuk melanjutkan."
            else -> null
        }
        if (validationMessage != null) {
            update { it.copy(message = validationMessage, messageIsError = true) }
            return
        }

        val request = RegistrationRequest(
            fullName = state.fullName,
            phone = state.phoneNumber,
            email = state.email,
            location = state.location,
            role = state.selectedRole,
            password = state.password,
            storeName = state.storeName.takeIf { state.selectedRole == UserRole.Seller },
            storeAddress = state.storeAddress.takeIf { state.selectedRole == UserRole.Seller },
            storeLatitude = state.storeLatitude.toDoubleOrNull()
                .takeIf { state.selectedRole == UserRole.Seller },
            storeLongitude = state.storeLongitude.toDoubleOrNull()
                .takeIf { state.selectedRole == UserRole.Seller }
        )
        launchRequest {
            repository.signUp(request)
                .onSuccess { outcome ->
                    if (!outcome.verificationRequired) {
                        update { it.copy(message = "Akun berhasil dibuat. Memuat profil Anda.", messageIsError = false) }
                    } else {
                        update {
                            it.copy(
                                authPage = AuthPage.EmailVerification,
                                password = "",
                                confirmPassword = "",
                                message = "Akun dibuat. Periksa email Anda untuk menyelesaikan verifikasi, lalu masuk.",
                                messageIsError = false
                            )
                        }
                    }
                }
                .onFailure { showFailure(it) }
        }
    }


    fun enterPreview(role: UserRole = mutableUiState.value.selectedRole) = update {
        it.copy(
            destination = AppDestination.Main(role),
            selectedRole = role,
            sessionState = AppSessionState.ConfigurationMissing,
            password = "",
            confirmPassword = "",
            message = null,
            editingProfile = false,
            isPreviewMode = true
        )
    }

    fun exitPreview() = update {
        it.copy(
            destination = AppDestination.Authentication,
            authPage = AuthPage.Login,
            password = "",
            confirmPassword = "",
            message = null,
            editingProfile = false,
            isPreviewMode = false
        )
    }

    fun startEditingProfile() = update { it.copy(editingProfile = true, message = null) }

    fun saveProfile() {
        val state = mutableUiState.value
        if (state.isPreviewMode) {
            update {
                it.copy(
                    editingProfile = false,
                    message = "Perubahan hanya tersimpan sementara selama pratinjau ini.",
                    messageIsError = false
                )
            }
            return
        }
        val currentProfile = (state.sessionState as? AppSessionState.Authenticated)?.profile
            ?: return
        val editedProfile = currentProfile.copy(
            fullName = state.fullName,
            phone = state.phoneNumber,
            email = state.email
        )
        launchRequest {
            repository.updateProfile(editedProfile)
                .onSuccess { profile ->
                    update {
                        it.copy(
                            sessionState = AppSessionState.Authenticated(profile),
                            fullName = profile.fullName,
                            phoneNumber = profile.phone.orEmpty(),
                            email = profile.email.orEmpty(),
                            editingProfile = false,
                            message = "Profil berhasil diperbarui.",
                            messageIsError = false
                        )
                    }
                }
                .onFailure { showFailure(it) }
        }
    }

    fun signOut() {
        launchRequest {
            repository.signOut()
                .onSuccess {
                    update {
                        it.copy(
                            destination = AppDestination.Authentication,
                            authPage = AuthPage.Login,
                            sessionState = AppSessionState.Unauthenticated,
                            fullName = "",
                            phoneNumber = "",
                            email = "",
                            location = "",
                            storeName = "",
                            storeAddress = "",
                            storeLatitude = "",
                            storeLongitude = "",
                            password = "",
                            confirmPassword = "",
                            editingProfile = false,
                            isPreviewMode = false,
                            message = null
                        )
                    }
                }
                .onFailure { showFailure(it) }
        }
    }

    private fun applySessionState(sessionState: AppSessionState) {
        update { current ->
            when (sessionState) {
                AppSessionState.Loading -> current.copy(sessionState = sessionState)
                AppSessionState.Unauthenticated -> current.copy(
                    sessionState = sessionState,
                    destination = AppDestination.Authentication,
                    fullName = "",
                    phoneNumber = "",
                    email = "",
                    location = "",
                    storeName = "",
                    storeAddress = "",
                    storeLatitude = "",
                    storeLongitude = "",
                    isPreviewMode = false
                )
                is AppSessionState.Authenticated -> current.copy(
                    sessionState = sessionState,
                    destination = AppDestination.Main(sessionState.profile.role),
                    fullName = sessionState.profile.fullName,
                    phoneNumber = sessionState.profile.phone.orEmpty(),
                    email = sessionState.profile.email.orEmpty(),
                    location = sessionState.profile.location.orEmpty(),
                    storeName = sessionState.profile.storeName.orEmpty(),
                    storeAddress = sessionState.profile.storeAddress.orEmpty(),
                    isPreviewMode = false,
                    message = null
                )
                AppSessionState.ConfigurationMissing -> current.copy(
                    sessionState = sessionState,
                    message = "Supabase belum dikonfigurasi. Pratinjau tidak membuat akun atau menyimpan data.",
                    messageIsError = false
                )
                AppSessionState.ProfileMissing -> current.copy(
                    sessionState = sessionState,
                    destination = AppDestination.Authentication,
                    message = "Profil akun tidak ditemukan. Pastikan migrasi Supabase TANICO sudah diterapkan.",
                    messageIsError = true
                )
                AppSessionState.Unauthorized -> current.copy(
                    sessionState = sessionState,
                    destination = AppDestination.Authentication,
                    message = "Peran akun tidak valid. Hubungi administrator TANICO.",
                    messageIsError = true
                )
                is AppSessionState.Failure -> current.copy(
                    sessionState = sessionState,
                    destination = AppDestination.Authentication,
                    message = sessionState.message,
                    messageIsError = true
                )
            }
        }
    }

    private fun launchRequest(block: suspend () -> Unit) {
        if (mutableUiState.value.isLoading) return
        viewModelScope.launch {
            update { it.copy(isLoading = true, message = null) }
            try {
                block()
            } finally {
                update { it.copy(isLoading = false) }
            }
        }
    }

    private fun showFailure(exception: Throwable) = update {
        it.copy(
            message = exception.message?.takeIf(String::isNotBlank)
                ?: "Tidak dapat menyelesaikan permintaan. Coba lagi.",
            messageIsError = true
        )
    }

    private fun isCoordinateInRange(value: String, minimum: Double, maximum: Double): Boolean =
        value.trim().toDoubleOrNull()?.let { it.isFinite() && it in minimum..maximum } == true

    private inline fun update(transform: (AuthUiState) -> AuthUiState) {
        mutableUiState.value = transform(mutableUiState.value)
    }
}
