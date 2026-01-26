package com.oriooneee.jet.navigation.engine

import com.oriooneee.jet.navigation.domain.entities.NavigationDirection
import com.oriooneee.jet.navigation.domain.entities.graph.InDoorNode
import com.oriooneee.jet.navigation.domain.entities.graph.MasterNavigation
import com.oriooneee.jet.navigation.domain.entities.graph.NodeType
import com.oriooneee.jet.navigation.domain.entities.graph.OutDoorNode
import com.oriooneee.jet.navigation.domain.entities.graph.SelectNodeResult
import com.oriooneee.jet.navigation.engine.models.ResolvedNode
import com.oriooneee.jet.navigation.engine.rendering.FloorRenderer
import com.oriooneee.jet.navigation.engine.routing.DistanceCalculator
import com.oriooneee.jet.navigation.engine.routing.PathFinder
import com.oriooneee.jet.navigation.engine.routing.RouteBuilder
import com.oriooneee.jet.navigation.engine.utils.Log
import kotlin.math.abs

private const val TAG = "NavEngine"

class NavigationEngine(
    private val masterNav: MasterNavigation,
    private val isRecommendedIndoor: Boolean
) {
    private val inDoorNodesMap: Map<String, InDoorNode> = masterNav.inDoorNavGraph.nodes.associateBy { it.id }
    private val outDoorNodesMap: Map<String, OutDoorNode> = masterNav.outDoorNavGraph.nodes.associateBy { it.id }
    private val globalAdjacency: Map<String, List<Pair<String, Double>>> = buildGlobalAdjacencyMap()

    private val pathFinder = PathFinder(inDoorNodesMap, outDoorNodesMap, globalAdjacency)
    private val distanceCalculator = DistanceCalculator(globalAdjacency)
    private val floorRenderer = FloorRenderer(masterNav)
    private val routeBuilder = RouteBuilder(masterNav, floorRenderer)

    init {
        Log.d(TAG, "NavigationEngine INIT")
    }

    private fun buildGlobalAdjacencyMap(): Map<String, List<Pair<String, Double>>> {
        val adj = mutableMapOf<String, MutableList<Pair<String, Double>>>()

        masterNav.inDoorNavGraph.edges.forEach { edge ->
            adj.getOrPut(edge.from) { mutableListOf() }.add(edge.to to edge.weight)
            adj.getOrPut(edge.to) { mutableListOf() }.add(edge.from to edge.weight)
        }

        masterNav.outDoorNavGraph.edges.forEach { edge ->
            adj.getOrPut(edge.from) { mutableListOf() }.add(edge.to to edge.weight)
            adj.getOrPut(edge.to) { mutableListOf() }.add(edge.from to edge.weight)
        }

        val indoorEntrances = inDoorNodesMap.values.filter { it.type.contains(NodeType.MAIN_ENTRANCE) }

        indoorEntrances.forEach { indoorNode ->
            val outdoorNode = findMatchingOutdoorNode(indoorNode)
            if (outdoorNode != null) {
                val connectionWeight = 5.0
                adj.getOrPut(indoorNode.id) { mutableListOf() }.add(outdoorNode.id to connectionWeight)
                adj.getOrPut(outdoorNode.id) { mutableListOf() }.add(indoorNode.id to connectionWeight)
            }
        }

        return adj
    }

    private fun findMatchingOutdoorNode(entrance: InDoorNode): OutDoorNode? {
        val exactMatch = outDoorNodesMap.values
            .filter { it.type.contains(NodeType.MAIN_ENTRANCE) }
            .find { it.label?.contains("${entrance.buildNum}") == true }

        if (exactMatch != null) return exactMatch

        val candidates = outDoorNodesMap.values.filter { it.type.contains(NodeType.MAIN_ENTRANCE) }
        if (candidates.isEmpty()) return null

        return candidates.firstOrNull()
    }

    fun resolveSelection(
        result: SelectNodeResult,
        referenceNode: ResolvedNode?
    ): ResolvedNode? {
        return when (result) {
            is SelectNodeResult.SelectedNode -> ResolvedNode.InDoor(result.node)
            is SelectNodeResult.SelectedOutDoorNode -> ResolvedNode.OutDoor(result.node)
            is SelectNodeResult.NearestManWC -> {
                if (referenceNode == null) return null
                pathFinder.findNearestNodeGlobal(referenceNode.id) { resolved ->
                    resolved is ResolvedNode.InDoor && resolved.node.type.contains(NodeType.WC_MAN)
                }
            }
            is SelectNodeResult.NearestWomanWC -> {
                if (referenceNode == null) return null
                pathFinder.findNearestNodeGlobal(referenceNode.id) { resolved ->
                    resolved is ResolvedNode.InDoor && resolved.node.type.contains(NodeType.WC_WOMAN)
                }
            }
            is SelectNodeResult.NearestMainEntrance -> {
                if (referenceNode == null) return null
                pathFinder.findNearestNodeGlobal(referenceNode.id) { resolved ->
                    resolved is ResolvedNode.InDoor && resolved.node.type.contains(NodeType.MAIN_ENTRANCE)
                }
            }
        }
    }

    fun getRoute(from: InDoorNode, to: InDoorNode): List<NavigationDirection> {
        return getRoute(ResolvedNode.InDoor(from), ResolvedNode.InDoor(to))
    }

    fun getRoute(from: ResolvedNode, to: ResolvedNode): List<NavigationDirection> {
        val allPaths = mutableListOf<List<String>>()

        val standardPath = pathFinder.findPathVariant(from.id, to.id) { _, _ -> 1.0 }
        if (standardPath != null) allPaths.add(standardPath)

        val indoorPreferredPath = pathFinder.findPathVariant(from.id, to.id) { u, v ->
            if (outDoorNodesMap.containsKey(u) || outDoorNodesMap.containsKey(v)) 50.0 else 1.0
        }
        if (indoorPreferredPath != null) allPaths.add(indoorPreferredPath)

        val outdoorPreferredPath = pathFinder.findPathVariant(from.id, to.id) { u, v ->
            if (inDoorNodesMap.containsKey(u) && inDoorNodesMap.containsKey(v)) 50.0 else 1.0
        }
        if (outdoorPreferredPath != null) allPaths.add(outdoorPreferredPath)

        if (allPaths.size < 4 && standardPath != null) {
            val standardEdges = standardPath.zipWithNext().toSet()
            val alternativePath = pathFinder.findPathVariant(from.id, to.id) { u, v ->
                if (standardEdges.contains(u to v) || standardEdges.contains(v to u)) 3.0 else 1.0
            }
            if (alternativePath != null) allPaths.add(alternativePath)
        }

        val uniquePaths = allPaths.distinct()

        data class PathEvaluation(
            val steps: List<com.oriooneee.jet.navigation.domain.entities.NavigationStep>,
            val totalDist: Double,
            val outdoorDist: Double
        )

        val evaluatedPaths = uniquePaths.map { pathIds ->
            val resolvedPath = pathIds.mapNotNull { pathFinder.resolveNodeById(it) }
            val totalDistance = distanceCalculator.calculateTotalDistanceGlobal(resolvedPath)
            val outdoorDistance = distanceCalculator.calculateOutdoorDistance(resolvedPath)
            val steps = routeBuilder.buildStepsFromUnifiedPath(resolvedPath)

            PathEvaluation(steps, totalDistance, outdoorDistance)
        }

        if (evaluatedPaths.isEmpty()) return emptyList()

        val fastestDist = evaluatedPaths.minOf { it.totalDist }
        val maxAcceptableDist = fastestDist * 1.3

        val sortedPaths = evaluatedPaths.sortedWith { a, b ->
            if (isRecommendedIndoor) {
                val aAcceptable = a.totalDist <= maxAcceptableDist
                val bAcceptable = b.totalDist <= maxAcceptableDist

                when {
                    aAcceptable && !bAcceptable -> -1
                    !aAcceptable && bAcceptable -> 1
                    else -> {
                        val outdoorDiff = a.outdoorDist - b.outdoorDist
                        if (abs(outdoorDiff) > 10.0) {
                            a.outdoorDist.compareTo(b.outdoorDist)
                        } else {
                            a.totalDist.compareTo(b.totalDist)
                        }
                    }
                }
            } else {
                a.totalDist.compareTo(b.totalDist)
            }
        }

        val minOutdoorDist = evaluatedPaths.minOf { it.outdoorDist }

        return sortedPaths.take(4).map { path ->
            val badge = when {
                path.totalDist == fastestDist -> "Fastest"
                path.outdoorDist == minOutdoorDist && path.outdoorDist < path.totalDist * 0.5 -> "Mostly Indoor"
                isRecommendedIndoor && path.outdoorDist == minOutdoorDist -> "Recommended"
                path.outdoorDist > 0 && path.outdoorDist < path.totalDist * 0.3 -> "Balanced"
                else -> null
            }
            NavigationDirection(path.steps, path.totalDist, badge)
        }
    }
}
