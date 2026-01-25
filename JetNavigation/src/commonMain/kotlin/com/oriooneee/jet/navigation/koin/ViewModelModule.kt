package com.oriooneee.jet.navigation.koin

import com.oriooneee.jet.navigation.presentation.NavigationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object ViewModelModule {
    val module = module {
        viewModelOf(::NavigationViewModel)
    }
}