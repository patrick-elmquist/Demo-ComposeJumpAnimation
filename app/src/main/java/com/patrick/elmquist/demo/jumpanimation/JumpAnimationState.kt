package com.patrick.elmquist.demo.jumpanimation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Stable
internal class JumpAnimationState(
    val scale: Animatable<Float, AnimationVector1D>,
    val translation: Animatable<Float, AnimationVector1D>,
    val interactionSource: MutableInteractionSource,
    private val scope: CoroutineScope,
    private val withTopExpand: Boolean,
    private val withEndSquish: Boolean,
) {
    private var animation: Job? = null

    fun onPress() = launchAnimation {
        scale.snapTo(defaultScale)
        translation.snapTo(defaultTranslation)
        scale.animateTo(pressedScale, defaultSpring)
    }

    fun onRelease(invokeOnCompletion: () -> Unit) = launchAnimation {
        coroutineScope {
            // ensure it's fully compressed if the user just quickly tapped
            scale.animateTo(pressedScale, defaultSpring)

            launch {
                // overshoot the scale a bit as part of the launch
                scale.animateTo(defaultScale, spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.55f))
            }

            launch {
                // up like a sun...
                translation.animateTo(launchTranslation, launchSpring)
                var isSquishing = false
                // ... and down as a pancake
                translation.animateTo(defaultTranslation, returnSpring) {
                    val hitTheGround = value >= defaultTranslation
                    if (true && hitTheGround && !isSquishing) {
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
    }

    fun onCancel() = launchAnimation {
        scale.snapTo(defaultScale)
        translation.snapTo(defaultTranslation)
    }

    private fun launchAnimation(block: suspend CoroutineScope.() -> Unit) {
        animation?.cancel()
        animation = scope.launch(block = block)
    }

    @Suppress("ConstPropertyName")
    companion object {
        private const val defaultScale = 1f
        private const val defaultTranslation = 0f

        private const val pressedScale = 0.6f

        private const val launchScale = 1.02f
        private const val launchTranslation = -0.8f

        private const val squishScale = 0.88f

        private val defaultSpring = spring<Float>()

        private val launchSpring = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        )

        private val returnSpring = spring<Float>(
            dampingRatio = 0.65f,
            stiffness = 140f,
        )
    }
}
