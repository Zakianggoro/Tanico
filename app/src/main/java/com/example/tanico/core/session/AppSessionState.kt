package com.example.tanico.core.session

import com.example.tanico.domain.model.UserProfile

sealed interface AppSessionState {
    data object Loading : AppSessionState
    data object Unauthenticated : AppSessionState
    data class Authenticated(val profile: UserProfile) : AppSessionState
    data object ProfileMissing : AppSessionState
    data object Unauthorized : AppSessionState
    data object ConfigurationMissing : AppSessionState
    data class Failure(val message: String) : AppSessionState
}
