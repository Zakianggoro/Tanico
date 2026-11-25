package com.example.tanico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tanico.core.navigation.AppDestination
import com.example.tanico.feature.auth.AuthEntryScreen
import com.example.tanico.feature.auth.AuthPage
import com.example.tanico.feature.auth.AuthUiState
import com.example.tanico.feature.auth.AuthViewModel
import com.example.tanico.feature.auth.RegistrationScreen
import com.example.tanico.feature.main.MainAppScreen
import com.example.tanico.ui.theme.TanicoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TanicoTheme {
                val authViewModel: AuthViewModel = viewModel()
                val state by authViewModel.uiState
                TanicoApp(state = state, viewModel = authViewModel)
            }
        }
    }
}

@Composable
private fun TanicoApp(state: AuthUiState, viewModel: AuthViewModel) {
    when (val destination = state.destination) {
        AppDestination.Authentication -> when (state.authPage) {
            AuthPage.Login -> AuthEntryScreen(
                state = state,
                onRoleSelected = viewModel::selectRole,
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
        }

        is AppDestination.Main -> MainAppScreen(
            role = destination.role,
            fullName = state.fullName,
            phoneNumber = state.phoneNumber,
            email = state.email,
            location = state.location,
            storeName = state.storeName,
            storeAddress = state.storeAddress,
            editingProfile = state.editingProfile,
            message = state.message,
            onFullNameChanged = viewModel::updateFullName,
            onPhoneNumberChanged = viewModel::updatePhoneNumber,
            onEmailChanged = viewModel::updateEmail,
            onLocationChanged = viewModel::updateLocation,
            onStoreNameChanged = viewModel::updateStoreName,
            onStoreAddressChanged = viewModel::updateStoreAddress,
            onEditProfile = viewModel::startEditingProfile,
            onSaveProfile = viewModel::saveProfileDraft,
            onExitPreview = viewModel::exitPreview
        )
    }
}
