package com.oriooneee.jena.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oriooneee.jena.domain.entities.NavigationStep
import com.oriooneee.jena.presentation.FloorAndBuildingBadge
import com.oriooneee.jena.presentation.MapComponent
import com.oriooneee.jena.presentation.NavigationViewModel
import com.oriooneee.jena.presentation.navigation.LocalNavController
import com.oriooneee.jena.presentation.navigation.Route
import com.oriooneee.jena.presentation.screen.components.NavigationControls
import com.oriooneee.jena.presentation.screen.map.ZoomableMapCanvas
import com.oriooneee.jena.presentation.screen.transitions.TransitionScreen
import com.oriooneee.jena.presentation.screen.transitions.TransitionToBuildingScreen
import com.oriooneee.jena.presentation.screen.transitions.TransitionToInDoorScreen
import com.oriooneee.jena.presentation.screen.transitions.TransitionToOutDoorScreen
import jena.generated.resources.Res
import jena.generated.resources.app_name
import jena.generated.resources.cd_toggle_panel
import jena.generated.resources.powered_by_mapbox
import jena.generated.resources.ready_to_navigate
import jena.generated.resources.ready_to_navigate_desc
import jena.generated.resources.updated_format
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

const val KEY_SELECTED_START_NODE = "selected_start_node"
const val KEY_SELECTED_END_NODE = "selected_end_node"
expect val isWebOrDesktop: Boolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    isDarkTheme: Boolean,
    viewModel: NavigationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentStep = uiState.currentStep
    var isHiddenMapCompoent by remember { mutableStateOf(false) }
    var shouldHideMap by remember { mutableStateOf(false) }
    LaunchedEffect(isHiddenMapCompoent) {
        if (isHiddenMapCompoent && currentStep is NavigationStep.OutDoorMaps && isWebOrDesktop) {
            shouldHideMap = true
            delay(1.seconds)
            isHiddenMapCompoent = false
            shouldHideMap = false
        } else {
            shouldHideMap = false
            isHiddenMapCompoent = false
        }
    }
    LaunchedEffect(Unit) {
        println("NavigationScreen LaunchedEffect: isWebOrDesktop = $isWebOrDesktop")
        shouldHideMap = isWebOrDesktop
        delay(600)
        shouldHideMap = false
    }

    val planColor = MaterialTheme.colorScheme.onSurface
    val planLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val routeColor = MaterialTheme.colorScheme.primary
    val startNodeColor = MaterialTheme.colorScheme.primary
    val endNodeColor = MaterialTheme.colorScheme.primary
    val navController = LocalNavController.current
    var mapHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect {
            val savedStateHandle = it.savedStateHandle
            launch {
                savedStateHandle.getStateFlow<String?>(
                    KEY_SELECTED_START_NODE,
                    null
                ).collect { node ->
                    if (node != null) {
                        viewModel.onStartNodeSelected(Json.decodeFromString(node))
                        it.savedStateHandle.remove<String>(KEY_SELECTED_START_NODE)
                    }
                }
            }
            launch {
                savedStateHandle.getStateFlow<String?>(
                    KEY_SELECTED_END_NODE,
                    null
                ).collect { node ->
                    if (node != null) {
                        viewModel.onEndNodeSelected(Json.decodeFromString(node))
                        it.savedStateHandle.remove<String>(KEY_SELECTED_END_NODE)
                    }
                }
            }
        }
    }

    BoxWithConstraints {
        val isLargeScreen = maxWidth >= 650.dp
        var isPanelExpanded by remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val createdAt by viewModel.navigationDataUpdatedAt.collectAsState()
                                AnimatedVisibility(
                                    createdAt != null,
                                ) {
                                    val timeAgo = remember(createdAt) {
                                        createdAt?.let {
                                            val now = Clock.System.now()
                                            val diff = now - it
                                            when {
                                                diff < 1.minutes -> "Just now"
                                                diff < 1.hours -> "${diff.inWholeMinutes} min ago"
                                                diff < 24.hours -> "${diff.inWholeHours} hour ago"
                                                else -> "${diff.inWholeDays} day ago"
                                            }
                                        }
                                    }
                                   timeAgo?.let {
                                       Text(
                                           text = stringResource(Res.string.updated_format, it),
                                           style = MaterialTheme.typography.labelSmall,
                                           color = MaterialTheme.colorScheme.onSurfaceVariant,
                                           lineHeight = 10.sp,
                                       )
                                   }
                                }
                                AnimatedVisibility(
                                    currentStep is NavigationStep.OutDoorMaps,
                                ) {
                                    Text(
                                        text = stringResource(Res.string.powered_by_mapbox),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 10.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.clip(
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(top = paddingValues.calculateTopPadding())
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 16.dp
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val activeMapData = currentStep as? NavigationStep.ByFlor

                        ZoomableMapCanvas(
                            renderData = activeMapData?.image,
                            initFocusPoint = activeMapData?.pointOfInterest ?: Offset.Zero,
                            routeBounds = activeMapData?.routeBounds,
                            planColor = planColor,
                            labelColor = planLabelColor,
                            routeColor = routeColor,
                            startNodeColor = startNodeColor,
                            endNodeColor = endNodeColor,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(if (activeMapData != null) 1f else 0f)
                        )

                        if (currentStep != null) {
                            AnimatedContent(
                                targetState = currentStep,
                                transitionSpec = {
                                    fadeIn(tween(400)) + scaleIn(initialScale = 0.95f) togetherWith
                                            fadeOut(tween(400))
                                },
                                label = "MapAnim"
                            ) { step ->
                                when (step) {
                                    is NavigationStep.ByFlor -> {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            FloorAndBuildingBadge(
                                                floorNumber = step.flor,
                                                buildingNumber = step.building,
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(16.dp)
                                            )
                                        }
                                    }

                                    is NavigationStep.TransitionToFlor -> {
                                        TransitionScreen(
                                            targetFloor = step.to,
                                            currentFlor = step.from
                                        )
                                    }

                                    is NavigationStep.TransitionToBuilding -> {
                                        TransitionToBuildingScreen(
                                            fromBuilding = step.form,
                                            toBuilding = step.to
                                        )
                                    }

                                    is NavigationStep.OutDoorMaps -> {
                                        val density = LocalDensity.current
                                        if (!shouldHideMap) {
                                            MapComponent(
                                                step = step,
                                                isDarkTheme = isDarkTheme,
                                                modifier = Modifier.onGloballyPositioned {
                                                    mapHeight = with(density) {
                                                        it.size.height.toDp()
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    is NavigationStep.TransitionToInDoor -> {
                                        TransitionToInDoorScreen(toBuilding = step.toBuilding)
                                    }

                                    is NavigationStep.TransitionToOutDoor -> {
                                        TransitionToOutDoorScreen(fromBuilding = step.fromBuilding)
                                    }
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    text = stringResource(Res.string.ready_to_navigate),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(Res.string.ready_to_navigate_desc),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 16.dp,
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .animateContentSize()
                            .padding(bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isPanelExpanded = !isPanelExpanded },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPanelExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(Res.string.cd_toggle_panel),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        NavigationControls(
                            currentStepIndex = uiState.currentStepIndex,
                            totalSteps = uiState.navigationSteps.size,
                            routeStats = uiState.routeStats,
                            isIndoorRecommended = false,
                            onPrevious = viewModel::previousStep,
                            onNext = viewModel::nextStep,
                            startNode = uiState.startNode,
                            endNode = uiState.endNode,
                            isLoading = uiState.isLoading,
                            onSelectStart = {
                                isHiddenMapCompoent = true
                                navController.navigate(
                                    Route.SelectDestination(
                                        isStartNode = true,
                                        false
                                    )
                                )
                            },
                            onSelectEnd = {
                                isHiddenMapCompoent = true
                                navController.navigate(
                                    Route.SelectDestination(
                                        isStartNode = false,
                                        uiState.startNode != null
                                    )
                                )
                            },
                            onSwapNodes = viewModel::swapNodes,
                            isExpanded = isPanelExpanded,
                            isVertical = !isLargeScreen,
                            availableRoutesCount = uiState.availableRoutes.size,
                            onOpenRouteSelection = {
                                isHiddenMapCompoent = true
                                navController.navigate(Route.SelectRoute)
                            },
                            isDarkMode = isDarkTheme
                        )
                        Spacer(Modifier.height(paddingValues.calculateBottomPadding()))
                    }
                }
            }
        }

    }
}
