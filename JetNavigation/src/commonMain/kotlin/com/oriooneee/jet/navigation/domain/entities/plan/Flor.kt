package com.oriooneee.jet.navigation.domain.entities.plan


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Flor(
    @SerialName("lines")
    val lines: List<Line>,
    @SerialName("polylines")
    val polylines: List<Polyline>,
    @SerialName("texts")
    val texts: List<Text>
)