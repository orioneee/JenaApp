package com.oriooneee.jet.navigation.data

import com.oriooneee.jet.navigation.domain.entities.graph.MasterNavigation

interface NavigationRemoteRepository{
   suspend fun getMainNavigation(): Result<MasterNavigation>
}