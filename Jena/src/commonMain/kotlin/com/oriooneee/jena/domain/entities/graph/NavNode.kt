package com.oriooneee.jena.domain.entities.graph

interface NavNode {
    val id: String
    val buildNum: Int?
    val label: String?
    val type: List<NodeType>
}