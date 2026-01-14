package com.oriooneee.jet.navigation.domain.entities

import androidx.compose.ui.geometry.Offset

sealed class NavigationStep() {
    data class ByFlor(
        val flor: Int,
        val image: ByteArray,
        val pointOfInterest: Offset,
    ): NavigationStep()
    data class TransitionToFlor(
        val from: Int,
        val to: Int
    ): NavigationStep()
}

data class NavigationDirection(
    val steps: List<NavigationStep>,
    val totalDistanceMeters: Double,
){
    val estimatedTimeMinutes: Double
        get() = totalDistanceMeters / 80.0
}