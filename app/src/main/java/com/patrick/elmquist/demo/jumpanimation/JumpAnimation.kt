package com.patrick.elmquist.demo.jumpanimation

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberJumpAnimationState(
    onClick: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
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

fun Modifier.jumpOnClick(state: JumpAnimationState): Modifier =
    this then Modifier
        .clickable(
            interactionSource = state.interactionSource,
            indication = null,
            onClick = {/* clicks are handled by the state when the animation finishes */ },
        )
        .graphicsLayer {
            // set the origin to the bottom center to have the
            // scale press down on the composable
            transformOrigin = TransformOrigin(
                pivotFractionX = 0.5f,
                pivotFractionY = 1.0f,
            )
            scaleY = state.scale.value
            translationY = state.translation.value * size.height
        }
