package com.oriooneee.jena.koin

import io.github.orioneee.Axer
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation

actual fun pluginsList(): List<ClientPlugin<out Any>> {
    return listOf(
            Axer.ktorPlugin,
            ContentNegotiation
    )
}