package com.oriooneee.jet.navigation.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.LocalPlatformContext
import com.oriooneee.jet.navigation.domain.entities.NavigationDirection
import com.oriooneee.jet.navigation.domain.entities.NavigationStep

@Composable
expect fun rememberVectorSvgPainter(bytes: ByteArray): Painter

@Composable
fun rememberZoomState(
    minScale: Float = 0.1f,
    maxScale: Float = 10f
) = remember { ZoomState(minScale, maxScale) }

class ZoomState(
    private val minScale: Float,
    private val maxScale: Float
) {
    var scale by mutableStateOf(1f)
        private set

    var offsetX by mutableStateOf(0f)
        private set

    var offsetY by mutableStateOf(0f)
        private set

    private var containerSize = Size.Zero

    fun updateContainerSize(size: Size) {
        containerSize = size
    }

    fun resetToFit(contentSize: Size) {
        if (containerSize == Size.Zero || contentSize == Size.Zero) return

        val scaleX = containerSize.width / contentSize.width
        val scaleY = containerSize.height / contentSize.height
        val fitScale = minOf(scaleX, scaleY)

        scale = fitScale
        centerContent(contentSize, fitScale)
    }

    fun zoomToPoint(focusPoint: Offset, contentSize: Size, zoomMultiplier: Float) {
        if (containerSize == Size.Zero || contentSize == Size.Zero) return

        val scaleX = containerSize.width / contentSize.width
        val scaleY = containerSize.height / contentSize.height
        val fitScale = minOf(scaleX, scaleY)

        val targetScale = (fitScale * zoomMultiplier).coerceIn(minScale, maxScale)
        scale = targetScale

        val screenCenter = Offset(containerSize.width / 2, containerSize.height / 2)
        val newOffset = screenCenter - (focusPoint * scale)

        offsetX = newOffset.x
        offsetY = newOffset.y
    }

    private fun centerContent(contentSize: Size, currentScale: Float) {
        offsetX = (containerSize.width - contentSize.width * currentScale) / 2
        offsetY = (containerSize.height - contentSize.height * currentScale) / 2
    }

    fun onGesture(centroid: Offset, pan: Offset, zoomChange: Float) {
        val oldScale = scale
        val newScale = (oldScale * zoomChange).coerceIn(minScale, maxScale)
        val actualZoomFactor = newScale / oldScale

        val currentOffset = Offset(offsetX, offsetY)
        val targetOffset = currentOffset + pan
        val offsetAdjustment = (centroid - targetOffset) * (1 - actualZoomFactor)
        val finalOffset = targetOffset + offsetAdjustment

        scale = newScale
        offsetX = finalOffset.x
        offsetY = finalOffset.y
    }

    fun zoomIn() {
        zoomBy(1.5f)
    }

    fun zoomOut() {
        zoomBy(0.66f)
    }

    private fun zoomBy(factor: Float) {
        if (containerSize == Size.Zero) return

        val center = Offset(containerSize.width / 2, containerSize.height / 2)
        val oldScale = scale
        val newScale = (oldScale * factor).coerceIn(minScale, maxScale)
        val actualZoomFactor = newScale / oldScale

        val currentOffset = Offset(offsetX, offsetY)
        val targetOffset = currentOffset
        val offsetAdjustment = (center - targetOffset) * (1 - actualZoomFactor)
        val finalOffset = targetOffset + offsetAdjustment

        scale = newScale
        offsetX = finalOffset.x
        offsetY = finalOffset.y
    }
}

@Composable
fun ZoomableSvgCanvas(svgBytes: ByteArray, initFocusPoint: Offset) {
    val painter = rememberVectorSvgPainter(svgBytes)
    val zoomState = rememberZoomState()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val containerWidth = with(density) { maxWidth.toPx() }
        val containerHeight = with(density) { maxHeight.toPx() }
        val containerSize = Size(containerWidth, containerHeight)

        LaunchedEffect(painter.intrinsicSize, containerSize, initFocusPoint) {
            zoomState.updateContainerSize(containerSize)

            if (initFocusPoint != Offset.Zero) {
                zoomState.zoomToPoint(
                    focusPoint = initFocusPoint,
                    contentSize = painter.intrinsicSize,
                    zoomMultiplier = 3f
                )
            } else {
                zoomState.resetToFit(painter.intrinsicSize)
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        zoomState.onGesture(centroid, pan, zoom)
                    }
                }
        ) {
            withTransform({
                translate(left = zoomState.offsetX, top = zoomState.offsetY)
                scale(
                    pivot = Offset.Zero,
                    scaleX = zoomState.scale,
                    scaleY = zoomState.scale
                )
            }) {
                with(painter) {
                    draw(size = intrinsicSize)
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { zoomState.zoomIn() },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            SmallFloatingActionButton(
                onClick = { zoomState.zoomOut() },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class)
@Composable
fun NavigationScreen(
    viewModel: NavigationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val planStrokeColor = MaterialTheme.colorScheme.onSurface
    val routeColor = MaterialTheme.colorScheme.primary
    val startNodeColor = MaterialTheme.colorScheme.tertiary
    val endNodeColor = MaterialTheme.colorScheme.error

    LaunchedEffect(uiState.startNode, uiState.endNode) {
        if (uiState.startNode != null &&
            uiState.endNode != null &&
            uiState.startNode != uiState.endNode
        ) {
            viewModel.calculateRoute(
                planStrokeColor,
                routeColor,
                startNodeColor,
                endNodeColor
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        val itemModifier = Modifier.weight(1f, fill = false).widthIn(min = 140.dp)
                            .fillMaxWidth(0.95f)
                        LocationSelector(
                            "Start", uiState.startNode, uiState.allNodes, uiState.endNode,
                            Icons.Default.LocationOn, itemModifier,
                            onNodeSelected = { viewModel.onStartNodeSelected(it) }
                        )
                        LocationSelector(
                            "End", uiState.endNode, uiState.allNodes, uiState.startNode,
                            Icons.Default.ArrowForward, itemModifier,
                            onNodeSelected = { viewModel.onEndNodeSelected(it) }
                        )
                    }

                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else if (uiState.navigationSteps.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            uiState.routeStats?.let { stats ->
                                RouteStatsCard(stats)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { viewModel.previousStep() },
                                    enabled = uiState.currentStepIndex > 0,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                }

                                Text(
                                    text = "Step ${uiState.currentStepIndex + 1} / ${uiState.navigationSteps.size}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Button(
                                    onClick = { viewModel.nextStep() },
                                    enabled = uiState.currentStepIndex < uiState.navigationSteps.lastIndex
                                ) {
                                    Text("Next")
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(bottom = 32.dp)
                .fillMaxSize()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(32.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.currentStep != null) {
                        AnimatedContent(
                            targetState = uiState.currentStep!!,
                            transitionSpec = {
                                fadeIn() + scaleIn(initialScale = 0.9f) togetherWith fadeOut() + scaleOut(
                                    targetScale = 1.1f
                                )
                            },
                            label = "MapNavigation"
                        ) { step ->
                            LocalPlatformContext.current
                            when (step) {
                                is NavigationStep.ByFlor -> {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        ZoomableSvgCanvas(
                                            svgBytes = step.image,
                                            initFocusPoint = step.pointOfInterest
                                        )

                                        FloorBadge(
                                            floorNumber = step.flor,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(24.dp)
                                        )
                                    }
                                }

                                is NavigationStep.TransitionToFlor -> {
                                    TransitionScreen(
                                        targetFloor = step.to,
                                        currentFlor = step.from
                                    )
                                }
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Select Start and End points",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RouteStatsCard(stats: NavigationDirection) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Flag, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${stats.totalDistanceMeters.toInt()}m",
                fontWeight = FontWeight.Bold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Timer, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            val mins = stats.estimatedTimeMinutes.toInt()
            val secs = ((stats.estimatedTimeMinutes - mins) * 60).toInt()
            Text(
                text = "$mins min $secs sec",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransitionScreen(
    currentFlor: Int,
    targetFloor: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .rotate(
                    if (targetFloor > currentFlor) 0f else 180f
                ),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Go to Floor",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$targetFloor",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}