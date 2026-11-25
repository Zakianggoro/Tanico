package com.example.tanico.core.session

import com.example.tanico.domain.model.UserRole

sealed interface AppSessionState {
    data object Loading : AppSessionState
    data object Unauthenticated : AppSessionState
    data class Authenticated(val role: UserRole) : AppSessionState
    data object ProfileMissing : AppSessionState
    data object Unauthorized : AppSessionState
}
