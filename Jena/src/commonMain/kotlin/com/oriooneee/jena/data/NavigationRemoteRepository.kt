package com.oriooneee.jena.data

import com.oriooneee.jena.domain.entities.graph.MasterNavigation
import com.oriooneee.jena.domain.entities.weather.WeatherResponse

interface NavigationRemoteRepository{
   suspend fun getMainNavigation(): Result<MasterNavigation>
    suspend fun getWeather(): Result<WeatherResponse>
}