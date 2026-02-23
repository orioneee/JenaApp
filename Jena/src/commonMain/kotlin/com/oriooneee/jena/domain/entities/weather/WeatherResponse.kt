package com.oriooneee.jena.domain.entities.weather


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("current_weather")
    val currentWeather: CurrentWeather,
) {
//    fun isRecomendedInDoor(): Boolean {
//        val byCode = WeatherCode.isIndoorRouteRecommendedСode(currentWeather.weatherCode)
//        val byTemp = currentWeather.temperature !in -10.0..35.0
//        return byCode || byTemp
//    }
}

