package com.oriooneee.jet.navigation.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MyNavKey {
    @Serializable
    data object NavigationScreen : MyNavKey

    @Serializable
    data class SelectDestination(val isStartNode: Boolean) : MyNavKey
}