package com.oriooneee.jet.navigation.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oriooneee.jet.navigation.buildconfig.BuildConfig
import com.oriooneee.jet.navigation.domain.entities.NavigationStep

@Composable
actual fun MapComponent(
    modifier: Modifier,
    step: NavigationStep.OutDoorMaps?,
    isDarkTheme: Boolean,
    isStatic: Boolean
) {
    MapBoxMapComponent(
        modifier = modifier,
        step = step,
        isDarkTheme = isDarkTheme,
        isStatic = isStatic
    )
//    GoogleMapsMapComponent(
//        modifier = modifier,
//        step = step,
//        isDarkTheme = isDarkTheme
//    )
}

internal actual val BuildConfig.MAPBOX_TOKEN: String
    get() = BuildConfig.MAPBOX_API_KEY_ANDROID