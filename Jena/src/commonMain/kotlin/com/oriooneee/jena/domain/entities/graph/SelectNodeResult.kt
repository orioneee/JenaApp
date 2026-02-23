package com.oriooneee.jena.domain.entities.graph

import kotlinx.serialization.Serializable

@Serializable
sealed class SelectNodeResult{
    @Serializable
    data object NearestManWC : SelectNodeResult()
    @Serializable
    data object NearestWomanWC : SelectNodeResult()
    @Serializable
    data object NearestMainEntrance : SelectNodeResult()
    @Serializable
    data class SelectedNode(val node: InDoorNode) : SelectNodeResult()
    @Serializable
    data class SelectedOutDoorNode(val node: OutDoorNode) : SelectNodeResult()
}