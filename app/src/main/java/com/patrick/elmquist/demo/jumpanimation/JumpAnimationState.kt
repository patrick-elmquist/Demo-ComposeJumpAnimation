package com.patrick.elmquist.demo.jumpanimation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
class JumpAnimationState(
    val scale: Animatable<Float, AnimationVector1D>,
    val translation: Animatable<Float, AnimationVector1D>,
    val interactionSource: MutableInteractionSource,
    private val scope: CoroutineScope,
) {
    private var animation: Job? = null

    fun onPress() = launchAnimation {
        scale.snapTo(defaultScale)
        translation.snapTo(defaultTranslation)
        scale.animateTo(pressedScale, defaultSpring)
    }

    fun onRelease(invokeOnCompletion: () -> Unit) = launchAnimation {
        // ensure it's fully compressed if the user just quickly tapped
        scale.animateTo(pressedScale, defaultSpring)

        launch {
            // overshoot the scale a bit as part of the launch
            scale.animateTo(defaultScale, releaseScaleSpring)
        }

        launch {
            // up like the sun...
            translation.animateTo(launchTranslation, launchTranslationSpring)

            // ... and down as a pancake
            var isSquishing = false
            translation.animateTo(defaultTranslation, returnTranslationSpring) {
                val hitTheGround = value >= defaultTranslation
                if (hitTheGround && !isSquishing) {
                    isSquishing = true
                    invokeOnCompletion()
                    launch {
                        // Squish the bag a bit when it lands
                        scale.animateTo(squishScale, defaultSpring)
                        scale.animateTo(defaultScale, defaultSpring)
                    }
                }
            }
        }
    }

    fun onCancel() = launchAnimation {
        scale.snapTo(defaultScale)
        translation.snapTo(defaultTranslation)
    }

    private fun launchAnimation(block: suspend CoroutineScope.() -> Unit) {
        animation?.cancel()
        animation = scope.launch(block = block)
    }
}

private const val defaultScale = 1f
private const val pressedScale = 0.6f
private const val squishScale = 0.88f

private const val defaultTranslation = 0f
private const val launchTranslation = -0.8f

private val defaultSpring = spring<Float>()

private val releaseScaleSpring = spring<Float>(
    stiffness = Spring.StiffnessMedium,
    dampingRatio = 0.55f
)

private val launchTranslationSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

private val returnTranslationSpring = spring<Float>(
    dampingRatio = 0.65f,
    stiffness = 140f,
)
