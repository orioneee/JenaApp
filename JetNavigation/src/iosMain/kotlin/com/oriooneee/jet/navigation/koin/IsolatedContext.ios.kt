package com.oriooneee.jet.navigation.koin

import org.koin.dsl.koinApplication

internal actual fun initializeIfCan(){
    IsolatedContext.initializeIfNeeded(
        koinApplication {
            modules(
                AppModule.module
            )
        }
    )
}