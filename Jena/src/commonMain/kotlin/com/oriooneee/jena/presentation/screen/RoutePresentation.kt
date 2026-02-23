package com.oriooneee.jena.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.oriooneee.jena.domain.entities.NavigationDirection
import com.oriooneee.jena.domain.entities.NavigationStep
import org.jetbrains.compose.resources.stringResource
import jena.generated.resources.Res
import jena.generated.resources.*

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
fun getRoutePresentation(route: NavigationDirection, isFastest: Boolean, isDarkMode: Boolean): RoutePresentation {
    val isPureOutdoor =
        route.steps.all { it is NavigationStep.OutDoorMaps || it is NavigationStep.TransitionToOutDoor }
    val hasOutdoor = route.steps.any { it is NavigationStep.OutDoorMaps }

    // Compute localized title (mirrors generatePathSummary logic)
    val parts = mutableListOf<String>()
    route.steps.forEach { step ->
        val part = when (step) {
            is NavigationStep.OutDoorMaps -> "Street"
            is NavigationStep.ByFlor -> "${step.building}"
            else -> null
        }
        if (part != null && parts.lastOrNull() != part) parts.add(part)
    }
    val localizedTitle = when {
        parts.isEmpty() -> stringResource(Res.string.direct_route)
        parts.size == 1 && parts.first() != "Street" -> stringResource(Res.string.building_only_format, parts.first())
        else -> parts.joinToString(" → ")
    }

    return when {
        isPureOutdoor -> RoutePresentation(
            title = stringResource(Res.string.via_streets),
            subtitle = stringResource(Res.string.outdoor_route_format, route.totalDistanceMeters.toInt()),
            icon = Icons.Outlined.WbSunny,
            color = if (isDarkMode) Color(0xFFFFB74D) else Color(0xFFE65100)
        )

        else -> RoutePresentation(
            title = localizedTitle,
            subtitle = if (isFastest) stringResource(Res.string.fastest_format, route.totalDistanceMeters.toInt()) else "${route.totalDistanceMeters.toInt()}m",
            icon = if (hasOutdoor) Icons.Outlined.NaturePeople else Icons.Outlined.Apartment,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
