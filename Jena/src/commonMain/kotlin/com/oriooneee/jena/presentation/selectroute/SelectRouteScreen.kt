package com.oriooneee.jena.presentation.selectroute

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oriooneee.jena.domain.entities.NavigationDirection
import com.oriooneee.jena.domain.entities.NavigationStep
import com.oriooneee.jena.presentation.MapComponent
import com.oriooneee.jena.presentation.screen.getRoutePresentation
import com.oriooneee.jena.presentation.screen.map.ZoomableMapCanvas
import org.jetbrains.compose.resources.stringResource
import jena.generated.resources.Res
import jena.generated.resources.*

@Composable
private fun getStepLabel(step: NavigationStep): String = when (step) {
    is NavigationStep.ByFlor -> stringResource(Res.string.step_floor_bldg_format, step.flor, step.building)
    is NavigationStep.TransitionToFlor ->
        if (step.to > step.from) stringResource(Res.string.go_up_to_floor_format, step.to)
        else stringResource(Res.string.go_down_to_floor_format, step.to)

    is NavigationStep.TransitionToBuilding -> stringResource(Res.string.to_building_format, step.to)
    is NavigationStep.OutDoorMaps -> stringResource(Res.string.walk_outdoors)
    is NavigationStep.TransitionToOutDoor -> stringResource(Res.string.exit_building)
    is NavigationStep.TransitionToInDoor -> stringResource(Res.string.enter_building_format, step.toBuilding)
}

@Composable
private fun StepPreview(
    step: NavigationStep,
    stepNumber: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val planColor = MaterialTheme.colorScheme.onSurface
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val routeColor = MaterialTheme.colorScheme.primary
    val nodeColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .width(200.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (step) {
                is NavigationStep.ByFlor -> {
                    ZoomableMapCanvas(
                        isStatic = true,
                        renderData = step.image,
                        initFocusPoint = step.pointOfInterest,
                        routeBounds = step.routeBounds,
                        planColor = planColor,
                        labelColor = labelColor,
                        routeColor = routeColor,
                        startNodeColor = nodeColor,
                        endNodeColor = nodeColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is NavigationStep.OutDoorMaps -> {
                    MapComponent(
                        step = step,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.fillMaxSize(),
                        isStatic = true
                    )
                }

                else -> {}
            }

            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = getStepLabel(step),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun RouteCard(
    route: NavigationDirection,
    presentation: com.oriooneee.jena.presentation.screen.RoutePresentation,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val containerColor = animateColorAsState(
        if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "containerColor"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = containerColor.value,
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(presentation.color.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = presentation.icon,
                        contentDescription = null,
                        tint = presentation.color,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val badge = route.badge
                    if (badge != null) {
                        val badgeColor = if (badge == "Recommended")
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                        val badgeTextColor = if (badge == "Recommended")
                            MaterialTheme.colorScheme.onTertiaryContainer
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer

                        Surface(
                            color = badgeColor,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = presentation.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = presentation.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    val mins = route.estimatedTimeMinutes.toInt()
                    val secs = ((route.estimatedTimeMinutes - mins) * 60).toInt()
                    Text(
                        text = stringResource(Res.string.time_min_format, mins),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (secs > 0) {
                        Text(
                            text = stringResource(Res.string.time_sec_format, secs),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = stringResource(Res.string.steps_count_format, route.steps.size),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                route.steps.filter {
                    it !is NavigationStep.TransitionToFlor &&
                            it !is NavigationStep.TransitionToBuilding &&
                            it !is NavigationStep.TransitionToInDoor &&
                            it !is NavigationStep.TransitionToOutDoor
                }.forEachIndexed { index, step ->
                    StepPreview(
                        step = step,
                        stepNumber = index + 1,
                        isDarkTheme = isDarkMode
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRouteScreen(
    routes: List<NavigationDirection>,
    selectedRoute: NavigationDirection?,
    isDarkMode: Boolean,
    onRouteSelected: (NavigationDirection) -> Unit,
    onClose: () -> Unit,
) {
    val sortedForCheck = remember(routes) { routes.sortedBy { it.totalDistanceMeters } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.choose_route_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = stringResource(Res.string.cd_close)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            routes.forEach { route ->
                val isSelected = route == selectedRoute
                val isFastest = route == sortedForCheck.firstOrNull()
                val presentation = getRoutePresentation(route, isFastest, isDarkMode)

                RouteCard(
                    route = route,
                    presentation = presentation,
                    isSelected = isSelected,
                    isDarkMode = isDarkMode,
                    onClick = { onRouteSelected(route) }
                )
            }
        }
    }
}
