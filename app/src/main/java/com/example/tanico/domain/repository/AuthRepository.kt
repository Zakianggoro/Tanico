package com.example.tanico.domain.repository

import com.example.tanico.core.session.AppSessionState
import com.example.tanico.domain.model.RegistrationOutcome
import com.example.tanico.domain.model.RegistrationRequest
import com.example.tanico.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isConfigured: Boolean

    fun observeSession(): Flow<AppSessionState>
    suspend fun signIn(identifier: String, password: String): Result<Unit>
    suspend fun signUp(request: RegistrationRequest): Result<RegistrationOutcome>
    suspend fun updateProfile(profile: UserProfile): Result<UserProfile>
    suspend fun signOut(): Result<Unit>
}
