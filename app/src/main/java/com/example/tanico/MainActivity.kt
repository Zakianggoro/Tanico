package com.example.tanico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tanico.core.navigation.AppDestination
import com.example.tanico.core.session.AppSessionState
import com.example.tanico.core.ui.TanicoLoadingIndicator
import com.example.tanico.domain.model.UserRole
import com.example.tanico.feature.auth.AuthEntryScreen
import com.example.tanico.feature.auth.AuthPage
import com.example.tanico.feature.auth.AuthUiState
import com.example.tanico.feature.auth.AuthViewModel
import com.example.tanico.feature.auth.AuthViewModelFactory
import com.example.tanico.feature.auth.EmailVerificationScreen
import com.example.tanico.feature.auth.RegistrationScreen
import com.example.tanico.feature.main.MainAppScreen
import com.example.tanico.feature.seller.SellerAnalyticsViewModel
import com.example.tanico.feature.seller.SellerAnalyticsViewModelFactory
import com.example.tanico.ui.theme.TanicoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TanicoTheme {
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())
                val analyticsViewModel: SellerAnalyticsViewModel =
                    viewModel(factory = SellerAnalyticsViewModelFactory())
                val state by authViewModel.uiState
                val analyticsState by analyticsViewModel.uiState
                TanicoApp(
                    state = state,
                    viewModel = authViewModel,
                    analyticsState = analyticsState,
                    analyticsViewModel = analyticsViewModel
                )
            }
        }
    }
}

@Composable
private fun TanicoApp(
    state: AuthUiState,
    viewModel: AuthViewModel,
    analyticsState: com.example.tanico.feature.seller.SellerAnalyticsUiState,
    analyticsViewModel: SellerAnalyticsViewModel
) {
    val sellerUserId = (state.sessionState as? AppSessionState.Authenticated)
        ?.profile
        ?.takeIf { it.role == UserRole.Seller && !state.isPreviewMode }
        ?.id
    LaunchedEffect(sellerUserId) {
        sellerUserId?.let(analyticsViewModel::load)
    }

    if (state.sessionState == AppSessionState.Loading && !state.isPreviewMode) {
        TanicoLoadingIndicator(Modifier.fillMaxSize())
        return
    }

    when (val destination = state.destination) {
        AppDestination.Authentication -> when (state.authPage) {
            AuthPage.Login -> AuthEntryScreen(
                state = state,
                onLoginIdentifierChanged = viewModel::updateLoginIdentifier,
                onPasswordChanged = viewModel::updatePassword,
                onLogin = viewModel::submitLogin,
                onRegister = viewModel::showRegistration,
                onPreview = viewModel::enterPreview
            )

            AuthPage.Registration -> RegistrationScreen(
                state = state,
                onRoleSelected = viewModel::selectRole,
                onFullNameChanged = viewModel::updateFullName,
                onPhoneNumberChanged = viewModel::updatePhoneNumber,
                onEmailChanged = viewModel::updateEmail,
                onLocationChanged = viewModel::updateLocation,
                onPasswordChanged = viewModel::updatePassword,
                onConfirmPasswordChanged = viewModel::updateConfirmPassword,
                onTermsAcceptedChanged = viewModel::updateTermsAccepted,
                onSubmit = viewModel::submitRegistration,
                onShowLogin = viewModel::showLogin,
                onPreview = viewModel::enterPreview
            )

            AuthPage.EmailVerification -> EmailVerificationScreen(
                message = state.message.orEmpty(),
                onBackToLogin = viewModel::showLogin
            )

        }

        is AppDestination.Main -> MainAppScreen(
            role = destination.role,
            isPreviewMode = state.isPreviewMode,
            fullName = state.fullName,
            phoneNumber = state.phoneNumber,
            email = state.email,
            location = state.location,
            storeName = state.storeName,
            storeAddress = state.storeAddress,
            editingProfile = state.editingProfile,
            isLoading = state.isLoading,
            message = state.message,
            analyticsState = analyticsState,
            onAnalyticsRetry = { sellerUserId?.let(analyticsViewModel::load) },
            onFullNameChanged = viewModel::updateFullName,
            onPhoneNumberChanged = viewModel::updatePhoneNumber,
            onEmailChanged = viewModel::updateEmail,
            onLocationChanged = viewModel::updateLocation,
            onStoreNameChanged = viewModel::updateStoreName,
            onStoreAddressChanged = viewModel::updateStoreAddress,
            onEditProfile = viewModel::startEditingProfile,
            onSaveProfile = viewModel::saveProfile,
            onSignOut = viewModel::signOut,
            onExitPreview = viewModel::exitPreview
        )
    }
}
