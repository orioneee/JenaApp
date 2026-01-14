package com.oriooneee.jet.navigation.domain.entities.graph


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Edge(
    @SerialName("from")
    val from: String,
    @SerialName("to")
    val to: String,
    @SerialName("weight")
    val weight: Double
)