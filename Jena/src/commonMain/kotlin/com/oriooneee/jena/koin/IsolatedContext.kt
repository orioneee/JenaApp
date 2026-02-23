package com.oriooneee.jena.koin

import org.koin.core.KoinApplication

internal object IsolatedContext {
    lateinit var koinApp: KoinApplication
        private set
    val koin by lazy {
        koinApp.koin
    }

    fun initializeIfNeeded(
        application: KoinApplication
    ) {
        if (!::koinApp.isInitialized) {
            koinApp = application
        }
    }
}

internal expect fun initializeIfCan()
