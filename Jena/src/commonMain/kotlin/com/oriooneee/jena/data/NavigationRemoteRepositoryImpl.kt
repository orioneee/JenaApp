package com.oriooneee.jena.data

import com.oriooneee.jena.buildconfig.BuildConfig
import com.oriooneee.jena.domain.entities.Coordinates
import com.oriooneee.jena.domain.entities.graph.MasterNavigation
import com.oriooneee.jena.domain.entities.weather.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal expect val BuildConfig.API_KEY: String

class NavigationRemoteRepositoryImpl(
    private val client: HttpClient,
) : NavigationRemoteRepository {
    private val _masterNavigation = MutableStateFlow<Result<MasterNavigation>?>(null)

    override val masterNavigationFlow: Flow<MasterNavigation>
        get() = _masterNavigation.map { it?.getOrNull() }.filterNotNull()

    companion object {
        val VNTU_COORDINATES = Coordinates(
            latitude = 49.2338836,
            longitude = 28.4375
        )
    }


    override suspend fun updateMasterNavigation(){
        val result = runCatching {
            val res = client.get {
                url {
                    takeFrom(BuildConfig.BASE_URL)
                    appendPathSegments("api", "navigation", "")
                }
            }.body<MasterNavigation>()
            res
        }.onFailure {
            it.printStackTrace()
        }
        _masterNavigation.value = result
    }

    override suspend fun getWeather(): Result<WeatherResponse> {
        return runCatching {
            client.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.open-meteo.com"
                    encodedPath = "/v1/forecast"

                    parameters.append("latitude", VNTU_COORDINATES.latitude.toString())
                    parameters.append("longitude", VNTU_COORDINATES.longitude.toString())
                    parameters.append("current_weather", "true")
                    parameters.append("hourly", "temperature_2m,precipitation")
                }
            }.body<WeatherResponse>()
        }.onFailure {
            it.printStackTrace()
        }
    }

}
