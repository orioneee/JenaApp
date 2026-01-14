package com.oriooneee.jet.navigation.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import com.caverock.androidsvg.SVG

@Composable
actual fun rememberVectorSvgPainter(bytes: ByteArray): Painter {
    return remember(bytes) {
        try {
            val svg = SVG.getFromInputStream(bytes.inputStream())
            AndroidSvgPainter(svg)
        } catch (e: Exception) {
            EmptyPainter
        }
    }
}

class AndroidSvgPainter(private val svg: SVG) : Painter() {
    override val intrinsicSize: Size
        get() = Size(svg.documentWidth.toFloat(), svg.documentHeight.toFloat())

    override fun DrawScope.onDraw() {
        svg.documentWidth = size.width
        svg.documentHeight = size.height

        svg.renderToCanvas(drawContext.canvas.nativeCanvas)
    }
}

object EmptyPainter : Painter() {
    override val intrinsicSize: Size = Size.Unspecified
    override fun DrawScope.onDraw() {}
}