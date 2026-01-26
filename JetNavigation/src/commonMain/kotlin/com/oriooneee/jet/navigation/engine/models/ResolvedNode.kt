package com.oriooneee.jet.navigation.engine.models

import com.oriooneee.jet.navigation.domain.entities.graph.InDoorNode
import com.oriooneee.jet.navigation.domain.entities.graph.OutDoorNode

sealed class ResolvedNode {
    data class InDoor(val node: InDoorNode) : ResolvedNode()
    data class OutDoor(val node: OutDoorNode) : ResolvedNode()

    val label: String?
        get() = when (this) {
            is InDoor -> node.label
            is OutDoor -> node.label
        }

    val id: String
        get() = when (this) {
            is InDoor -> node.id
            is OutDoor -> node.id
        }
}
