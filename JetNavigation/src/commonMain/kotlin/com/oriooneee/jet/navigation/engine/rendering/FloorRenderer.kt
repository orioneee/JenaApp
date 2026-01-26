package com.oriooneee.jet.navigation.engine.rendering

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Man
import androidx.compose.material.icons.outlined.Wc
import androidx.compose.material.icons.outlined.Woman
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.oriooneee.jet.navigation.domain.entities.graph.Flor
import com.oriooneee.jet.navigation.domain.entities.graph.InDoorNode
import com.oriooneee.jet.navigation.domain.entities.graph.MasterNavigation
import com.oriooneee.jet.navigation.domain.entities.graph.NodeType
import com.oriooneee.jet.navigation.engine.models.FloorRenderData
import com.oriooneee.jet.navigation.engine.models.IconLabel
import com.oriooneee.jet.navigation.engine.models.TextLabel

class FloorRenderer(
    private val masterNav: MasterNavigation,
    private val outputWidth: Double = 2000.0,
    private val paddingPct: Double = 0.05
) {
    fun generateFloorData(
        flor: Flor,
        buildingId: Int,
        stepPath: List<InDoorNode>,
        localStart: InDoorNode,
        localEnd: InDoorNode
    ): FloorRenderData {
        val allX = mutableListOf<Double>()
        val allY = mutableListOf<Double>()

        flor.plan.polylines.forEach { p ->
            p.points.forEach { pt ->
                allX.add(pt[0])
                allY.add(pt[1])
            }
        }
        flor.plan.lines.forEach { l ->
            allX.add(l.x1)
            allX.add(l.x2)
            allY.add(l.y1)
            allY.add(l.y2)
        }
        flor.plan.texts.forEach { t ->
            allX.add(t.x)
            allY.add(t.y)
        }

        if (allX.isEmpty()) {
            return FloorRenderData(
                1f, 1f, emptyList(), emptyList(), emptyList(),
                emptyList(), null, null, emptyList(), emptyList()
            )
        }

        val minX = allX.minOrNull() ?: 0.0
        val maxX = allX.maxOrNull() ?: 1.0
        val minY = allY.minOrNull() ?: 0.0
        val maxY = allY.maxOrNull() ?: 1.0

        val dataW = maxX - minX
        val dataH = maxY - minY
        if (dataW == 0.0 || dataH == 0.0) {
            return FloorRenderData(
                1f, 1f, emptyList(), emptyList(), emptyList(),
                emptyList(), null, null, emptyList(), emptyList()
            )
        }

        val drawWidth = outputWidth * (1 - paddingPct * 2)
        val scale = drawWidth / dataW
        val outputHeight = (dataH * scale + (outputWidth * paddingPct * 2)).toInt()
        val padding = outputWidth * paddingPct

        fun tx(x: Double): Float = ((x - minX) * scale + padding).toFloat()
        fun ty(y: Double): Float = (outputHeight - ((y - minY) * scale + padding)).toFloat()

        val polygons = mutableListOf<List<Offset>>()
        val polylines = mutableListOf<List<Offset>>()

        flor.plan.polylines.forEach { poly ->
            val points = poly.points.map { Offset(tx(it[0]), ty(it[1])) }
            if (poly.closed) polygons.add(points) else polylines.add(points)
        }

        val singleLines = flor.plan.lines.map { l ->
            Pair(Offset(tx(l.x1), ty(l.y1)), Offset(tx(l.x2), ty(l.y2)))
        }

        val routePoints = mutableListOf<Offset>()
        stepPath.forEach { node ->
            if (!node.type.contains(NodeType.STAIRS)) {
                routePoints.add(Offset(tx(node.x), ty(node.y)))
            }
        }

        var startNodeOffset: Offset? = null
        if (!localStart.type.contains(NodeType.STAIRS)) {
            startNodeOffset = Offset(tx(localStart.x), ty(localStart.y))
        }

        var endNodeOffset: Offset? = null
        if (!localEnd.type.contains(NodeType.STAIRS)) {
            endNodeOffset = Offset(tx(localEnd.x), ty(localEnd.y))
        }

        val icons = mutableListOf<IconLabel>()
        val floorNodes = masterNav.inDoorNavGraph.nodes.filter {
            it.buildNum == buildingId && it.floorNum == flor.num
        }

        floorNodes.forEach { node ->
            var icon: androidx.compose.ui.graphics.vector.ImageVector? = null
            var tint: Color = Color.Black

            when {
                node.type.containsAll(listOf(NodeType.WC_WOMAN, NodeType.WC_MAN)) -> {
                    icon = Icons.Outlined.Wc
                    tint = Color(0xFF9B27AF)
                }
                node.type.contains(NodeType.WC_MAN) -> {
                    icon = Icons.Outlined.Man
                    tint = Color(0xFF4A90E2)
                }
                node.type.contains(NodeType.WC_WOMAN) -> {
                    icon = Icons.Outlined.Woman
                    tint = Color(0xFFE91E63)
                }
                node.type.contains(NodeType.MAIN_ENTRANCE) -> {
                    icon = Icons.Outlined.ExitToApp
                    tint = Color(0xFF4CAF50)
                }
            }

            if (icon != null) {
                icons.add(IconLabel(icon, tx(node.x), ty(node.y), tint))
            }
        }

        val textLabels = mutableListOf<TextLabel>()
        flor.plan.texts.forEach { txt ->
            var clean = txt.text.filter { !it.isISOControl() }
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .trim()

            val isWcText = clean.contains("wc", ignoreCase = true) ||
                    clean.equals("м", ignoreCase = true) ||
                    clean.equals("ж", ignoreCase = true)

            if (clean.isNotEmpty() && !isWcText) {
                textLabels.add(TextLabel(clean, tx(txt.x), ty(txt.y), "#666666"))
            }
        }

        return FloorRenderData(
            width = outputWidth.toFloat(),
            height = outputHeight.toFloat(),
            polygons = polygons,
            polylines = polylines,
            singleLines = singleLines,
            routePath = routePoints,
            startNode = startNodeOffset,
            endNode = endNodeOffset,
            textLabels = textLabels,
            icons = icons
        )
    }
}
