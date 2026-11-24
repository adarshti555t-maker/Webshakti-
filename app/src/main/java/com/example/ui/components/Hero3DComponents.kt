package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyAbyss
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private data class SphereNode3D(val x: Float, val y: Float, val z: Float)
private data class ProjectedNode(val x: Float, val y: Float, val z: Float, val scale: Float)

/**
 * Real-time 3D rotating neural globe & orbital rings projected in 3D space.
 */
@Composable
fun Rotating3DNeuralGlobe(
    modifier: Modifier = Modifier,
    extraYawRad: Float = 0f,
    extraPitchRad: Float = 0f
) {
    val transition = rememberInfiniteTransition(label = "globe3d")
    val angleY by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "yaw"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Fibonacci sphere points for uniform 3D distribution
    val nodes = remember {
        val count = 28
        val goldenAngle = PI * (3.0 - sqrt(5.0))
        List(count) { i ->
            val y = 1f - (i / (count - 1f)) * 2f
            val r = sqrt((1f - y * y).coerceAtLeast(0f))
            val theta = goldenAngle * i
            SphereNode3D(
                x = (cos(theta) * r).toFloat(),
                y = y,
                z = (sin(theta) * r).toFloat()
            )
        }
    }

    val edges = remember(nodes) {
        buildList {
            for (i in nodes.indices) {
                for (j in i + 1 until nodes.size) {
                    val dx = nodes[i].x - nodes[j].x
                    val dy = nodes[i].y - nodes[j].y
                    val dz = nodes[i].z - nodes[j].z
                    if (dx * dx + dy * dy + dz * dz < 0.72f) {
                        add(i to j)
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val radius = size.minDimension * 0.36f
        val perspective = 2.6f

        val yaw = angleY + extraYawRad
        val pitch = 0.32f + extraPitchRad
        val cosY = cos(yaw)
        val sinY = sin(yaw)
        val cosP = cos(pitch)
        val sinP = sin(pitch)

        // Outer ambient 3D aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ElectricBlue.copy(alpha = 0.28f * pulse),
                    CyanPulse.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = radius * 1.45f
            ),
            radius = radius * 1.45f,
            center = Offset(cx, cy)
        )

        // Tilted 3D orbital rings
        val ringSteps = 44
        for (ringIdx in 0..1) {
            val tiltAngle = if (ringIdx == 0) 0.45f else -0.55f
            val ringRadius = radius * (if (ringIdx == 0) 1.24f else 1.12f)
            val cosT = cos(tiltAngle)
            val sinT = sin(tiltAngle)
            var prev: Offset? = null
            for (s in 0..ringSteps) {
                val a = (s.toFloat() / ringSteps) * (2f * PI.toFloat()) + (if (ringIdx == 0) yaw else -yaw * 0.7f)
                val rx = cos(a) * ringRadius
                val rz = sin(a) * ringRadius
                val ry = rz * sinT
                val rzt = rz * cosT
                val factor = perspective / (perspective - (rzt / (radius * 2.4f)))
                val pt = Offset(cx + rx * factor, cy + ry * factor)
                prev?.let { p ->
                    val alpha = if (rzt > 0f) 0.48f else 0.14f
                    drawLine(
                        color = (if (ringIdx == 0) CyanPulse else ElectricBlue).copy(alpha = alpha),
                        start = p,
                        end = pt,
                        strokeWidth = if (rzt > 0f) 2.dp.toPx() else 1.dp.toPx()
                    )
                }
                prev = pt
            }
        }

        // Project sphere nodes
        val projected = nodes.map { n ->
            val x1 = n.x * cosY - n.z * sinY
            val z1 = n.x * sinY + n.z * cosY
            val y2 = n.y * cosP - z1 * sinP
            val z2 = n.y * sinP + z1 * cosP
            val factor = perspective / (perspective - z2 * 0.55f)
            ProjectedNode(
                x = cx + x1 * radius * factor,
                y = cy + y2 * radius * factor,
                z = z2,
                scale = factor
            )
        }

        // Draw 3D wireframe connections
        edges.forEach { (i, j) ->
            val a = projected[i]
            val b = projected[j]
            val avgZ = (a.z + b.z) * 0.5f
            val alpha = ((avgZ + 1f) * 0.5f).coerceIn(0.08f, 0.62f)
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ElectricBlue.copy(alpha = alpha),
                        CyanPulse.copy(alpha = alpha * 0.9f)
                    ),
                    start = Offset(a.x, a.y),
                    end = Offset(b.x, b.y)
                ),
                start = Offset(a.x, a.y),
                end = Offset(b.x, b.y),
                strokeWidth = (1.1f + alpha).dp.toPx()
            )
        }

        // Draw depth-sorted nodes so foreground glows over background
        projected.sortedBy { it.z }.forEach { p ->
            val depthNorm = ((p.z + 1f) * 0.5f).coerceIn(0.15f, 1f)
            val nodeRadius = (2.2f + 2.8f * depthNorm) * p.scale
            if (depthNorm > 0.65f) {
                drawCircle(
                    color = CyanPulse.copy(alpha = 0.25f * depthNorm),
                    radius = nodeRadius * 2.2f,
                    center = Offset(p.x, p.y)
                )
            }
            drawCircle(
                color = if (depthNorm > 0.55f) CyanPulse.copy(alpha = depthNorm) else ElectricBlue.copy(alpha = depthNorm),
                radius = nodeRadius,
                center = Offset(p.x, p.y)
            )
        }

        // Core specular rim
        drawCircle(
            color = CyanPulse.copy(alpha = 0.22f),
            radius = radius * 1.02f,
            center = Offset(cx, cy),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

/**
 * Interactive 3D perspective stage that tilts smoothly with drag gestures and springs back.
 */
@Composable
fun Interactive3DStageContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(tiltXDeg: Float, tiltYDeg: Float) -> Unit
) {
    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()
    val rotX = remember { Animatable(0f) }
    val rotY = remember { Animatable(0f) }

    val transition = rememberInfiniteTransition(label = "ambient3d")
    val ambientDrift by transition.animateFloat(
        initialValue = -2.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    val effectiveRotX = (rotX.value + ambientDrift * 0.45f).coerceIn(-14f, 14f)
    val effectiveRotY = (rotY.value + ambientDrift).coerceIn(-16f, 16f)

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            rotX.animateTo(0f, spring(stiffness = Spring.StiffnessLow, dampingRatio = 0.65f))
                        }
                        scope.launch {
                            rotY.animateTo(0f, spring(stiffness = Spring.StiffnessLow, dampingRatio = 0.65f))
                        }
                    },
                    onDragCancel = {
                        scope.launch { rotX.animateTo(0f, spring()) }
                        scope.launch { rotY.animateTo(0f, spring()) }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            rotY.snapTo((rotY.value + dragAmount.x * 0.08f).coerceIn(-14f, 14f))
                            rotX.snapTo((rotX.value - dragAmount.y * 0.08f).coerceIn(-12f, 12f))
                        }
                    }
                )
            }
            .graphicsLayer {
                rotationX = effectiveRotX
                rotationY = effectiveRotY
                cameraDistance = 15f * density
            }
    ) {
        content(effectiveRotX, effectiveRotY)
    }
}

/**
 * 3D extruded bevel card with recessed depth layer underneath and glowing specular edge.
 */
@Composable
fun Extruded3DCard(
    modifier: Modifier = Modifier,
    depthOffset: Dp = 5.dp,
    cornerRadius: Dp = 20.dp,
    accentGlow: Color = ElectricBlue,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .padding(bottom = depthOffset, end = 2.dp)
    ) {
        // Recessed 3D extrusion base plate
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 2.dp, y = depthOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.24f),
                            DeepSapphire.copy(alpha = 0.22f),
                            accentGlow.copy(alpha = 0.20f)
                        )
                    )
                )
        )

        // Elevated crisp white 3D top surface
        Surface(
            color = NavySurface,
            shape = RoundedCornerShape(cornerRadius),
            border = BorderStroke(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ElectricBlue.copy(alpha = 0.48f),
                        NavyBorder,
                        DeepSapphire.copy(alpha = 0.35f)
                    )
                )
            ),
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier
                            .clip(RoundedCornerShape(cornerRadius))
                            .clickable(onClick = onClick)
                            .minimumInteractiveComponentSize()
                    } else {
                        Modifier
                    }
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                NavySurface,
                                NavyDeep,
                                NavyElevated.copy(alpha = 0.45f)
                            )
                        )
                    )
            ) {
                content()
            }
        }
    }
}
