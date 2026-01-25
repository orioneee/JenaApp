package com.oriooneee.jet.navigation.data

import com.oriooneee.jet.navigation.domain.entities.graph.MasterNavigation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NavigationRemoteRepositoryImpl(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
                contentType = ContentType.Any
            )
        }
    }

) : NavigationRemoteRepository {

    private var cachedNavigation: MasterNavigation? = null

    override suspend fun getMainNavigation(): Result<MasterNavigation> {
        cachedNavigation?.let {
            return Result.success(it)
        }

        return runCatching {
            client
                .get("https://raw.githubusercontent.com/orioneee/OrimapParcer/master/master_navigation.json")
                .body<MasterNavigation>()
        }.onSuccess {
            cachedNavigation = it
        }.onFailure {
            it.printStackTrace()
        }
    }
}
