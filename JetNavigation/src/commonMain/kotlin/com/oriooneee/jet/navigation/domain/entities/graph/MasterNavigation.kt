package com.oriooneee.jet.navigation.domain.entities.graph


import jetnavigation.jetnavigation.generated.resources.Res
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MasterNavigation(
    @SerialName("plan")
    val buildings: List<Building>,
    @SerialName("inDoor")
    val inDoorNavGraph: InDoorNavGraph,
    @SerialName("outDoor")
    val outDoorNavGraph: OutDoorNavGraph
) {
    companion object {
        private val mutex = Mutex()
        private var cachedInstance: MasterNavigation? = null

        suspend fun loadFromAssets(): MasterNavigation {
            cachedInstance?.let { return it }

            return mutex.withLock {
                cachedInstance ?: loadInternal().also {
                    cachedInstance = it
                }
            }
        }

        private suspend fun loadInternal(): MasterNavigation {
            val bytes = Res.readBytes("files/master_navigation.json")
            val jsonString = bytes.decodeToString()

            return Json.decodeFromString<MasterNavigation>(jsonString).also {
                val audsCount = it.inDoorNavGraph.nodes
                    .count { node -> node.type.contains(NodeType.AUDITORIUM) }
                val outDoorEdgesCount = it.outDoorNavGraph.edges.size
                val inDoorEdgesCount = it.inDoorNavGraph.edges.size
                val totalNodes = it.inDoorNavGraph.nodes.size + it.outDoorNavGraph.nodes.size
                val totalEdges = it.inDoorNavGraph.edges.size + it.outDoorNavGraph.edges.size

                println(
                    "Loaded MasterNavigation with " +
                            "${it.buildings.size} buildings and $audsCount auditoriums " +
                            "($outDoorEdgesCount outdoor edges), " +
                            "($inDoorEdgesCount indoor edges), " +
                            "total $totalNodes nodes and $totalEdges edges."
                )
            }
        }
    }
}
