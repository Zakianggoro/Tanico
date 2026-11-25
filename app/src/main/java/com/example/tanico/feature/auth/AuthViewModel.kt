package com.example.tanico.feature.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tanico.core.navigation.AppDestination
import com.example.tanico.domain.model.UserRole

enum class AuthPage {
    Login,
    Registration
}

data class AuthUiState(
    val destination: AppDestination = AppDestination.Authentication,
    val authPage: AuthPage = AuthPage.Login,
    val selectedRole: UserRole = UserRole.Buyer,
    val loginIdentifier: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val location: String = "",
    val storeName: String = "",
    val storeAddress: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false,
    val message: String? = null,
    val messageIsError: Boolean = true,
    val editingProfile: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val mutableUiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = mutableUiState

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
    fun updatePassword(value: String) = update { it.copy(password = value, message = null) }
    fun updateConfirmPassword(value: String) = update { it.copy(confirmPassword = value, message = null) }
    fun updateTermsAccepted(value: Boolean) = update { it.copy(termsAccepted = value, message = null) }

    fun submitLogin() = update { state ->
        val validationMessage = when {
            state.loginIdentifier.isBlank() ->
                "Masukkan nomor WhatsApp atau alamat email."
            state.password.isBlank() -> "Masukkan kata sandi Anda."
            else -> null
        }
        state.copy(
            message = validationMessage
                ?: "Login belum tersedia. Supabase belum terhubung dan data tidak dikirim.",
            messageIsError = validationMessage != null
        )
    }

    fun submitRegistration() = update { state ->
        val validationMessage = when {
            state.fullName.isBlank() -> "Masukkan nama lengkap Anda."
            state.phoneNumber.isBlank() -> "Masukkan nomor WhatsApp aktif."
            state.location.isBlank() -> "Pilih domisili kabupaten atau kota."
            state.password.length < 8 -> "Kata sandi harus terdiri dari minimal 8 karakter."
            state.password != state.confirmPassword -> "Konfirmasi kata sandi belum cocok."
            !state.termsAccepted -> "Setujui syarat dan ketentuan untuk melanjutkan."
            else -> null
        }
        state.copy(
            message = validationMessage
                ?: "Formulir valid, tetapi akun belum dibuat karena Supabase belum terhubung.",
            messageIsError = validationMessage != null
        )
    }

    fun enterPreview(role: UserRole = mutableUiState.value.selectedRole) = update {
        it.copy(
            destination = AppDestination.Main(role),
            selectedRole = role,
            password = "",
            confirmPassword = "",
            message = null,
            editingProfile = false
        )
    }

    fun exitPreview() = update {
        it.copy(
            destination = AppDestination.Authentication,
            authPage = AuthPage.Login,
            password = "",
            confirmPassword = "",
            message = null,
            editingProfile = false
        )
    }

    fun startEditingProfile() = update { it.copy(editingProfile = true, message = null) }

    fun saveProfileDraft() = update {
        it.copy(
            editingProfile = false,
            message = "Perubahan hanya tersimpan sementara selama pratinjau ini.",
            messageIsError = false
        )
    }

    private inline fun update(transform: (AuthUiState) -> AuthUiState) {
        mutableUiState.value = transform(mutableUiState.value)
    }
}
