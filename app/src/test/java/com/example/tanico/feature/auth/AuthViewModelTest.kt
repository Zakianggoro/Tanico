package com.example.tanico.feature.auth

import com.example.tanico.core.navigation.AppDestination
import com.example.tanico.core.session.AppSessionState
import com.example.tanico.domain.model.RegistrationOutcome
import com.example.tanico.domain.model.RegistrationRequest
import com.example.tanico.domain.model.UserProfile
import com.example.tanico.domain.model.UserRole
import com.example.tanico.domain.model.isValidEmailAddress
import com.example.tanico.domain.model.normalizeIndonesianPhoneNumber
import com.example.tanico.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loginWithoutIdentifierShowsValidationMessage() {
        val viewModel = AuthViewModel(FakeAuthRepository())
        viewModel.updatePassword("secret123")

        viewModel.submitLogin()

        assertEquals("Masukkan alamat email.", viewModel.uiState.value.message)
        assertEquals(AppDestination.Authentication, viewModel.uiState.value.destination)
    }

    @Test
    fun loginRejectsPhoneWhileOnlyEmailAuthIsEnabled() {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)
        viewModel.updateLoginIdentifier("081234567890")
        viewModel.updatePassword("secret123")

        viewModel.submitLogin()

        assertEquals("Masukkan alamat email yang valid.", viewModel.uiState.value.message)
        assertFalse(repository.signInCalled)
    }

    @Test
    fun registrationRequiresTermsAndDoesNotCallRepository() {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)
        viewModel.updateFullName("Nama Pengguna")
        viewModel.updatePhoneNumber("081234567890")
        viewModel.updateEmail("buyer@example.test")
        viewModel.updateLocation("Kota Malang")
        viewModel.updatePassword("secret123")
        viewModel.updateConfirmPassword("secret123")

        viewModel.submitRegistration()

        assertEquals("Setujui syarat dan ketentuan untuk melanjutkan.", viewModel.uiState.value.message)
        assertFalse(repository.registrationCalled)
    }

    @Test
    fun registrationSendsSelectedRoleAndEmailSignupRequestsVerification() {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)
        viewModel.selectRole(UserRole.Seller)
        viewModel.updateFullName("Penjual TANICO")
        viewModel.updatePhoneNumber("081234567890")
        viewModel.updateEmail("buyer@example.test")
        viewModel.updateLocation("Kota Malang")
        viewModel.updatePassword("secret123")
        viewModel.updateConfirmPassword("secret123")
        viewModel.updateTermsAccepted(true)

        viewModel.submitRegistration()

        assertTrue(repository.registrationCalled)
        assertEquals(UserRole.Seller, repository.registrationRequest?.role)
        assertEquals("buyer@example.test", repository.registrationRequest?.email)
        assertEquals(AuthPage.EmailVerification, viewModel.uiState.value.authPage)
    }

    @Test
    fun registrationRequiresAnEmailForTheEnabledSupabaseProvider() {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)
        viewModel.updateFullName("Nama Pengguna")
        viewModel.updatePhoneNumber("081234567890")
        viewModel.updateLocation("Kota Malang")
        viewModel.updatePassword("secret123")
        viewModel.updateConfirmPassword("secret123")
        viewModel.updateTermsAccepted(true)

        viewModel.submitRegistration()

        assertEquals("Masukkan alamat email yang valid.", viewModel.uiState.value.message)
        assertFalse(repository.registrationCalled)
    }

    @Test
    fun previewOpensSelectedRoleWithoutMarkingItAsAuthenticated() {
        val viewModel = AuthViewModel(FakeAuthRepository())

        viewModel.enterPreview(UserRole.Seller)

        assertEquals(AppDestination.Main(UserRole.Seller), viewModel.uiState.value.destination)
        assertTrue(viewModel.uiState.value.isPreviewMode)
        assertFalse(viewModel.uiState.value.sessionState is AppSessionState.Authenticated)
    }

    @Test
    fun normalizesLocalIndonesianPhoneNumbersForSupabase() {
        assertEquals("+6281234567890", normalizeIndonesianPhoneNumber("0812 3456 7890"))
        assertEquals("+6281234567890", normalizeIndonesianPhoneNumber("+62 812-3456-7890"))
        assertEquals(null, normalizeIndonesianPhoneNumber("12345"))
        assertTrue(isValidEmailAddress("buyer@example.test"))
        assertFalse(isValidEmailAddress("not-an-email"))
    }

    private class FakeAuthRepository : AuthRepository {
        override val isConfigured: Boolean = false
        var signInCalled = false
        var registrationCalled = false
        var registrationRequest: RegistrationRequest? = null

        override fun observeSession(): Flow<AppSessionState> = flowOf(AppSessionState.ConfigurationMissing)
        override suspend fun signIn(identifier: String, password: String): Result<Unit> {
            signInCalled = true
            return Result.success(Unit)
        }
        override suspend fun signUp(request: RegistrationRequest): Result<RegistrationOutcome> {
            registrationCalled = true
            registrationRequest = request
            return Result.success(RegistrationOutcome(verificationRequired = true))
        }
        override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> = Result.success(profile)
        override suspend fun signOut(): Result<Unit> = Result.success(Unit)
    }
}
