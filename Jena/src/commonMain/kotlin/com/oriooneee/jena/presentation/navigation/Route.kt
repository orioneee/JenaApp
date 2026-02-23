package com.oriooneee.jena.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object NavigationScreen : Route

    @Serializable
    data class SelectDestination(
        val isStartNode: Boolean,
        val isSelectedStartNode: Boolean
    ) : Route

    @Serializable
    data object SelectRoute : Route
}