package com.oriooneee.jet.navigation.engine.routing

import com.oriooneee.jet.navigation.engine.models.ResolvedNode
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DistanceCalculator(
    private val globalAdjacency: Map<String, List<Pair<String, Double>>>
) {
    fun calculateTotalDistanceGlobal(path: List<ResolvedNode>): Double {
        var distance = 0.0
        for (i in 0 until path.size - 1) {
            val u = path[i]
            val v = path[i + 1]

            val edgeWeight = globalAdjacency[u.id]?.find { it.first == v.id }?.second
            if (edgeWeight != null) {
                distance += edgeWeight
            } else if (u is ResolvedNode.OutDoor && v is ResolvedNode.OutDoor) {
                distance += haversineDistance(u.node.lat, u.node.lon, v.node.lat, v.node.lon)
            }
        }
        return distance
    }

    fun calculateOutdoorDistance(path: List<ResolvedNode>): Double {
        var distance = 0.0
        for (i in 0 until path.size - 1) {
            val u = path[i]
            val v = path[i + 1]

            if (u is ResolvedNode.OutDoor || v is ResolvedNode.OutDoor) {
                val edgeWeight = globalAdjacency[u.id]?.find { it.first == v.id }?.second
                if (edgeWeight != null) {
                    distance += edgeWeight
                } else if (u is ResolvedNode.OutDoor && v is ResolvedNode.OutDoor) {
                    distance += haversineDistance(u.node.lat, u.node.lon, v.node.lat, v.node.lon)
                }
            }
        }
        return distance
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = (lat2 - lat1) * kotlin.math.PI / 180.0
        val dLon = (lon2 - lon1) * kotlin.math.PI / 180.0
        val lat1Rad = lat1 * kotlin.math.PI / 180.0
        val lat2Rad = lat2 * kotlin.math.PI / 180.0
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
