package com.oriooneee.jena.data

import com.oriooneee.jena.domain.entities.graph.MasterNavigation
import com.oriooneee.jena.domain.entities.weather.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface NavigationRemoteRepository {
    val masterNavigationFlow: Flow<MasterNavigation>
    suspend fun updateMasterNavigation()
    suspend fun getWeather(): Result<WeatherResponse>
}