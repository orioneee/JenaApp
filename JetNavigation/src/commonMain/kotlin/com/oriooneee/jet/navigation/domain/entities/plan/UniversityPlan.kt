package com.oriooneee.jet.navigation.domain.entities.plan


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UniversityPlan(
    @SerialName("FLOR_1")
    val flor1: Flor,
    @SerialName("FLOR_2")
    val flor2: Flor,
    @SerialName("FLOR_3")
    val flor3: Flor,
    @SerialName("FLOR_4")
    val flor4: Flor
)