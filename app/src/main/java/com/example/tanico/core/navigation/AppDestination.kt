package com.example.tanico.core.navigation

import com.example.tanico.domain.model.UserRole

sealed interface AppDestination {
    data object Authentication : AppDestination

    data class Main(val role: UserRole) : AppDestination
}
