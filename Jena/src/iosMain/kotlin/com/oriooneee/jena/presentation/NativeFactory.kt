package com.oriooneee.jena.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import com.oriooneee.jena.buildconfig.BuildConfig
import com.oriooneee.jena.domain.entities.NavigationStep
import platform.UIKit.UIView

val LocalNativeFactory = staticCompositionLocalOf<NativeFactory?> {
    null
}


interface NativeFactory {
    fun getMapBoxMap(
        step: NavigationStep.OutDoorMaps?,
        isDarkTheme: Boolean,
    ): UIView

    companion object {
        fun getMapBoxToken() = BuildConfig.MAPBOX_TOKEN
    }
}