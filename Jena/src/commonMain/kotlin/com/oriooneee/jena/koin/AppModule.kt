package com.oriooneee.jena.koin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.oriooneee.jena.buildconfig.BuildConfig
import com.oriooneee.jena.data.API_KEY
import com.oriooneee.jena.data.NavigationRemoteRepository
import com.oriooneee.jena.data.NavigationRemoteRepositoryImpl
import com.oriooneee.jena.presentation.NavigationViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
expect fun pluginsList(): List<ClientPlugin<out Any>>
object AppModule {
    val module = module {
        single<NavigationRemoteRepository> {
            NavigationRemoteRepositoryImpl(get())
        }
        viewModelOf(::NavigationViewModel)
        single {
            HttpClient {
                pluginsList().forEach { plugin ->
                    install(plugin)
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                        },
                        contentType = ContentType.Any
                    )
                }
                defaultRequest {
                    header("X-Api-Key", BuildConfig.API_KEY)
                }
            }
        }
    }
}

@Composable
fun rememberCoilImageLoader(
    context: PlatformContext = LocalPlatformContext.current,
    client: HttpClient = koinInject()
): ImageLoader {
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = client))
            }
            .build()
    }
    return imageLoader
}