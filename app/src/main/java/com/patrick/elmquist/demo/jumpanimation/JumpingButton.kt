package com.patrick.elmquist.demo.jumpanimation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrick.elmquist.demo.jumpanimation.ui.theme.JumpAnimationTheme
import com.patrick.elmquist.demo.jumpanimation.ui.theme.Typography

@Composable
internal fun JumpingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val animationState = rememberJumpAnimationState()
    val interactionSource = remember { MutableInteractionSource() }

    val onClickLambda by rememberUpdatedState(onClick)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> animationState.onPress()
                is PressInteraction.Release -> animationState.onRelease(onClickLambda)
                is PressInteraction.Cancel -> animationState.onCancel()
            }
        }
    }

    Box(modifier) {
        Text(
            text = "🍞🍦🍿",
            fontSize = 96.sp,
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { /* don't set click here as it will interrupt the animation*/ },
                )
                .graphicsLayer {
                    transformOrigin = TransformOrigin(
                        pivotFractionX = 0.5f,
                        pivotFractionY = 1.0f,
                    )
                    scaleY = animationState.scale.value
                    translationY = animationState.translation.value * size.height
                },
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewCouponPromotion() {
    JumpAnimationTheme {
        var counter by remember { mutableIntStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .background(Color(0x8AFBEACC))
                .padding(top = 128.dp, bottom = 32.dp),
            verticalArrangement = spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            JumpingButton { counter++ }
            Text(
                text = "Clicks: $counter",
                style = Typography.titleMedium,
                color = Color(0x8A000000)
            )
        }
    }
}
