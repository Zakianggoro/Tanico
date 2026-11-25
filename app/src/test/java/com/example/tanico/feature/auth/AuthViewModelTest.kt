package com.example.tanico.feature.auth

import com.example.tanico.core.navigation.AppDestination
import com.example.tanico.domain.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthViewModelTest {
    @Test
    fun loginWithoutIdentifierShowsValidationMessageAndStaysUnauthenticated() {
        val viewModel = AuthViewModel()
        viewModel.updatePassword("secret123")

        viewModel.submitLogin()

        assertEquals("Masukkan nomor WhatsApp atau alamat email.", viewModel.uiState.value.message)
        assertEquals(AppDestination.Authentication, viewModel.uiState.value.destination)
    }

    @Test
    fun validLoginFormDoesNotPretendToAuthenticateWithoutSupabase() {
        val viewModel = AuthViewModel()
        viewModel.updateLoginIdentifier("buyer@example.test")
        viewModel.updatePassword("secret123")

        viewModel.submitLogin()

        assertEquals(AppDestination.Authentication, viewModel.uiState.value.destination)
        assertTrue(viewModel.uiState.value.message.orEmpty().contains("Supabase belum terhubung"))
    }

    @Test
    fun registrationRequiresTermsAndDoesNotCreateAnAccount() {
        val viewModel = AuthViewModel()
        viewModel.updateFullName("Nama Pengguna")
        viewModel.updatePhoneNumber("081234567890")
        viewModel.updateLocation("Kota Malang")
        viewModel.updatePassword("secret123")
        viewModel.updateConfirmPassword("secret123")

        viewModel.submitRegistration()

        assertEquals("Setujui syarat dan ketentuan untuk melanjutkan.", viewModel.uiState.value.message)
        assertEquals(AppDestination.Authentication, viewModel.uiState.value.destination)

        viewModel.updateTermsAccepted(true)
        viewModel.submitRegistration()

        assertEquals(AppDestination.Authentication, viewModel.uiState.value.destination)
        assertTrue(viewModel.uiState.value.message.orEmpty().contains("akun belum dibuat"))
    }

    @Test
    fun previewOpensTheSelectedRoleWithoutCreatingAnAccount() {
        val viewModel = AuthViewModel()

        viewModel.enterPreview(UserRole.Seller)

        assertEquals(AppDestination.Main(UserRole.Seller), viewModel.uiState.value.destination)
    }
}
