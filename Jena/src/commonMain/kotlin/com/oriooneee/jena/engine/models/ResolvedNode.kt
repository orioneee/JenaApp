package com.oriooneee.jena.engine.models

import com.oriooneee.jena.domain.entities.graph.InDoorNode
import com.oriooneee.jena.domain.entities.graph.OutDoorNode

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
