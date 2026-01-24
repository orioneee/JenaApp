package com.oriooneee.jet.navigation.domain.entities.graph

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OutDoorNavGraph(
    @SerialName("edges")
    val edges: List<Edge>,
    @SerialName("nodes")
    val nodes: List<OutDoorNode>
)