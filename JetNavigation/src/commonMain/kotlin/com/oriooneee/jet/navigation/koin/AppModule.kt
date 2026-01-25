package com.oriooneee.jet.navigation.koin

import com.oriooneee.jet.navigation.data.NavigationRemoteRepository
import com.oriooneee.jet.navigation.data.NavigationRemoteRepositoryImpl
import com.oriooneee.jet.navigation.presentation.NavigationViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object AppModule {
    val module = module {
        singleOf<NavigationRemoteRepository>(::NavigationRemoteRepositoryImpl)
        viewModelOf(::NavigationViewModel)
    }
}