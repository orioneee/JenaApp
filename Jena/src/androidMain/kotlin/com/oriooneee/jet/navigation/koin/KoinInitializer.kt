package com.oriooneee.jena.koin

import android.content.Context
import androidx.startup.Initializer
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

internal class KoinInitializer : Initializer<KoinApplication> {
    override fun create(context: Context): KoinApplication {
        val koinApplication = koinApplication {
            androidContext(context.applicationContext)
            modules(AppModule.module)
        }
        IsolatedContext.initializeIfNeeded(koinApplication)
        return koinApplication
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }
}