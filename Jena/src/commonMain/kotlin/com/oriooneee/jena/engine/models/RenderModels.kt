package com.oriooneee.jena.engine.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TextLabel(
    val text: String,
    val x: Float,
    val y: Float,
    val color: String = "#000000",
    val bold: Boolean = false,
    val hasBackground: Boolean = false
)

data class IconLabel(
    val icon: ImageVector,
    val x: Float,
    val y: Float,
    val tint: Color,
)

data class FloorRenderData(
    val width: Float,
    val height: Float,
    val polygons: List<List<Offset>>,
    val polylines: List<List<Offset>>,
    val singleLines: List<Pair<Offset, Offset>>,
    val routePath: List<Offset>,
    val startNode: Offset?,
    val endNode: Offset?,
    val textLabels: List<TextLabel>,
    val icons: List<IconLabel>,
)
