package com.oriooneee.jena.presentation.screen.map

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

actual fun Modifier.onMouseScroll(onScroll: (scrollDelta: Offset, position: Offset) -> Unit): Modifier = this
