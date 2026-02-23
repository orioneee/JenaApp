package com.oriooneee.jena.presentation.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oriooneee.jena.domain.entities.NavigationDirection
import com.oriooneee.jena.engine.models.ResolvedNode
import com.oriooneee.jena.presentation.screen.getRoutePresentation
import jena.generated.resources.Res
import jena.generated.resources.btn_next
import jena.generated.resources.btn_previous
import jena.generated.resources.cd_change_route
import jena.generated.resources.distance_format
import jena.generated.resources.indoor_route_recommended
import jena.generated.resources.time_format
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationControls(
    currentStepIndex: Int,
    totalSteps: Int,
    routeStats: NavigationDirection?,
    isIndoorRecommended: Boolean,
    isExpanded: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    startNode: ResolvedNode?,
    endNode: ResolvedNode?,
    isLoading: Boolean,
    onSelectStart: () -> Unit,
    onSelectEnd: () -> Unit,
    onSwapNodes: () -> Unit,
    isVertical: Boolean,
    availableRoutesCount: Int,
    onOpenRouteSelection: () -> Unit,
    isDarkMode: Boolean
) {
    @Composable
    fun RouteStats() {
        routeStats?.let { stats ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                onClick = { if (availableRoutesCount > 1) onOpenRouteSelection() }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (availableRoutesCount > 1) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = stringResource(Res.string.cd_change_route),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(0.5f),
                                            CircleShape
                                        )
                                        .padding(4.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                            }

                            Column {
                                val mins = stats.estimatedTimeMinutes.toInt()
                                val secs = ((stats.estimatedTimeMinutes - mins) * 60).toInt()

                                val typeTitle = getRoutePresentation(stats, false, isDarkMode).title

                                Text(
                                    text = stringResource(Res.string.time_format, mins, secs),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (availableRoutesCount > 1) {
                                        Text(
                                            text = "$typeTitle • ",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = stringResource(Res.string.distance_format, stats.totalDistanceMeters.toInt()),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Icon(
                            Icons.Default.Timer,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (isIndoorRecommended) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.indoor_route_recommended),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun StepNavigationControls() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onPrevious,
                enabled = currentStepIndex > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    null,
                    modifier = Modifier.rotate(180f)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.btn_previous))
            }

            Text(
                text = "${currentStepIndex + 1} / $totalSteps",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Button(
                onClick = onNext,
                enabled = currentStepIndex < totalSteps - 1,
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text(stringResource(Res.string.btn_next))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
    }

    if (isVertical) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isExpanded) {
                DestinationInputPanel(
                    startNode = startNode,
                    endNode = endNode,
                    isLoading = isLoading,
                    onSelectStart = onSelectStart,
                    onSelectEnd = onSelectEnd,
                    onSwap = onSwapNodes
                )
                RouteStats()
            }
            if (totalSteps > 0) {
                StepNavigationControls()
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (isExpanded) {
                    DestinationInputPanel(
                        startNode = startNode,
                        endNode = endNode,
                        isLoading = isLoading,
                        onSelectStart = onSelectStart,
                        onSelectEnd = onSelectEnd,
                        onSwap = onSwapNodes
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isExpanded) {
                    RouteStats()
                }
                if (totalSteps > 0) {
                    StepNavigationControls()
                }
            }
        }
    }
}
