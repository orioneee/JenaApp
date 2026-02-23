package com.oriooneee.jena.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapComponent(
    modifier: Modifier,
    step: NavigationStep.OutDoorMaps?,
    isDarkTheme: Boolean,
    isStatic: Boolean
) {
    if(isStatic){
        StaticImageMap(
            modifier = modifier,
            step = step,
            isDarkTheme = isDarkTheme
        )
    } else{
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
}