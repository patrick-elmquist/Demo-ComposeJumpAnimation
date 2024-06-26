package com.patrick.elmquist.demo.jumpanimation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun rememberJumpAnimationState(
    onClick: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
): JumpAnimationState {
    val state = remember(scope, interactionSource) {
        JumpAnimationState(
            scale = Animatable(initialValue = 1f),
            translation = Animatable(initialValue = 0f),
            scope = scope,
            interactionSource = interactionSource,
        )
    }

    val onClickLambda by rememberUpdatedState(onClick)
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> state.onPress()
                is PressInteraction.Release -> state.onRelease(onClickLambda)
                is PressInteraction.Cancel -> state.onCancel()
            }
        }
    }

    return state
}

internal fun Modifier.jumpOnClick(state: JumpAnimationState): Modifier {
    return this then Modifier
        .clickable(
            interactionSource = state.interactionSource,
            indication = null,
            onClick = { /* don't set click here as it will interrupt the animation*/ },
        )
        .graphicsLayer {
            transformOrigin = TransformOrigin(
                pivotFractionX = 0.5f,
                pivotFractionY = 1.0f,
            )
            scaleY = state.scale.value
            translationY = state.translation.value * size.height
        }
}

// TODO for simplicity, maybe split this into its own file
@Stable
internal class JumpAnimationState(
    val scale: Animatable<Float, AnimationVector1D>,
    val translation: Animatable<Float, AnimationVector1D>,
    val interactionSource: MutableInteractionSource,
    private val scope: CoroutineScope,
) {
    private var animation: Job? = null

    fun onPress() = launchAnimation {
        scale.snapTo(1f)
        translation.snapTo(0f)
        scale(PressedScale)
    }

    fun onRelease(invokeOnCompletion: () -> Unit) = launchAnimation {
        coroutineScope {
            // ensure it's fully compressed if the user just quickly tapped
            scale(PressedScale)

            launch {
                // overshoot the scale a bit as part of the launch
                scale(LaunchScale)
                scale(DefaultScale)
            }

            launch {
                // up like a sun...
                translation(target = LaunchTranslation, spec = LaunchSpringSpec)
                // ... and down as a pancake
                translation(target = DefaultTranslation, spec = ReturnSpringSpec)
            }

            launch {
                delay(SquishDelay)
                invokeOnCompletion()
                // Squish the bag a bit when it lands
                scale(SquishScale)
                scale(DefaultScale)
            }
        }
    }

    fun onCancel() = launchAnimation {
        scale.snapTo(DefaultScale)
        translation.snapTo(DefaultTranslation)
    }

    private fun launchAnimation(block: suspend CoroutineScope.() -> Unit) {
        animation?.cancel()
        animation = scope.launch(block = block)
    }

    private suspend fun scale(
        target: Float,
        spec: SpringSpec<Float> = spring(),
    ) = scale.animateTo(targetValue = target, animationSpec = spec)

    private suspend fun translation(
        target: Float,
        spec: SpringSpec<Float> = spring(),
    ) = translation.animateTo(targetValue = target, animationSpec = spec)

    @Suppress("ConstPropertyName")
    companion object {
        const val DefaultScale = 1f
        const val DefaultTranslation = 0f

        const val PressedScale = 0.6f

        const val LaunchScale = 1.02f
        const val LaunchTranslation = -0.8f
        val LaunchSpringSpec = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        )
        val ReturnSpringSpec = spring<Float>(
            dampingRatio = 0.65f,
            stiffness = 140f,
        )

        val SquishDelay = 550.milliseconds
        const val SquishScale = 0.85f
    }
}
