package com.oriooneee.jet.navigation.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM

@Composable
actual fun rememberVectorSvgPainter(bytes: ByteArray): Painter {
    return remember(bytes) {
        val dom = SVGDOM(Data.makeFromBytes(bytes))
        SkiaSvgPainter(dom)
    }
}

class SkiaSvgPainter(private val dom: SVGDOM) : Painter() {
    override val intrinsicSize: Size
        get() {
            val root = dom.root ?: return Size.Unspecified
            return Size(root.width.value, root.height.value)
        }

    override fun DrawScope.onDraw() {
        dom.render(drawContext.canvas.nativeCanvas)
    }
}