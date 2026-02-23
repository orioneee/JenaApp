package com.oriooneee.jena.data

import com.oriooneee.jena.buildconfig.BuildConfig

internal actual val BuildConfig.API_KEY: String
    get() = BuildConfig.API_KEY_WEB