package com.oriooneee.jet.navigation.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.oriooneee.jet.navigation.domain.entities.NavigationStep
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapComponent(
    modifier: Modifier,
    step: NavigationStep.OutDoorMaps?,
    isDarkTheme: Boolean
) {
    val nativeFactory = LocalNativeFactory.current
    if (nativeFactory == null) {
        MapPlaceholderContent(step)
    } else {
        UIKitView(
            factory = {
                nativeFactory.getMapBoxMap(step, isDarkTheme)
            },
            modifier = modifier
        )
    }
}