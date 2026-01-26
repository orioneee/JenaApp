package com.oriooneee.jet.navigation.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.oriooneee.jet.navigation.domain.entities.NavigationDirection
import com.oriooneee.jet.navigation.domain.entities.NavigationStep

data class RoutePresentation(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

fun generatePathSummary(steps: List<NavigationStep>): String {
    val parts = mutableListOf<String>()

    steps.forEach { step ->
        val part = when (step) {
            is NavigationStep.OutDoorMaps -> "Street"
            is NavigationStep.ByFlor -> "${step.building}"
            else -> null
        }

        if (part != null && parts.lastOrNull() != part) {
            parts.add(part)
        }
    }

    if (parts.isEmpty()) return "Direct Route"
    if (parts.size == 1 && parts.first() != "Street") return "Building ${parts.first()} Only"

    return parts.joinToString(" → ")
}

@Composable
fun getRoutePresentation(route: NavigationDirection, isFastest: Boolean): RoutePresentation {
    val summary = generatePathSummary(route.steps)
    val isPureOutdoor =
        route.steps.all { it is NavigationStep.OutDoorMaps || it is NavigationStep.TransitionToOutDoor }
    val hasOutdoor = route.steps.any { it is NavigationStep.OutDoorMaps }

    return when {
        isPureOutdoor -> RoutePresentation(
            title = "Via Streets",
            subtitle = "Outdoor route • ${route.totalDistanceMeters.toInt()}m",
            icon = Icons.Outlined.WbSunny,
            color = Color(0xFFE65100)
        )

        else -> RoutePresentation(
            title = summary,
            subtitle = if (isFastest) "Fastest • ${route.totalDistanceMeters.toInt()}m" else "${route.totalDistanceMeters.toInt()}m",
            icon = if (hasOutdoor) Icons.Outlined.NaturePeople else Icons.Outlined.Apartment,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
