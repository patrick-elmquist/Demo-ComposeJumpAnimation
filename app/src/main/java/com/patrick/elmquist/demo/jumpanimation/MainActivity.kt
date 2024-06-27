package com.patrick.elmquist.demo.jumpanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrick.elmquist.demo.jumpanimation.ui.theme.JumpAnimationTheme
import com.patrick.elmquist.demo.jumpanimation.ui.theme.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JumpAnimationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val state = remember { listOf("🍞️" to 0, "🍦" to 0, "🍿" to 0).toMutableStateMap() }
    val sortedState = state.toSortedMap().entries

    val textEnabled = false
    val text = sortedState
        .joinToString(separator = "  ") { (emoji, count) -> "$emoji:$count" }
        .takeIf { state.values.any { it > 0 } && textEnabled }

    val textAlpha by animateFloatAsState(
        targetValue = if (text.isNullOrBlank()) 0f else 1f,
        label = "alpha animation"
    )

    val paddingValues = if (textEnabled) {
        PaddingValues(top = 152.dp, bottom = 48.dp)
    } else {
        PaddingValues(top = 112.dp, bottom = 48.dp)
    }

    Box(
        Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalArrangement = spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        ) {
            sortedState.forEach { (emoji, count) ->
                JumpingEmoji(
                    emoji = emoji,
                    onClick = { state[emoji] = count + 1 },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Column(
            Modifier
                .align(Alignment.TopCenter)
                .alpha(textAlpha)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "CLICKS",
                style = Typography.labelSmall,
                color = Color.DarkGray,
            )
            Text(
                text = text.orEmpty(),
                style = Typography.labelLarge,
                color = Color.DarkGray,
            )
        }
    }
}

@Composable
fun JumpingEmoji(
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = emoji,
        fontSize = 96.sp,
        modifier = modifier
            .jumpOnClick(rememberJumpAnimationState(onClick = onClick))
    )
}

val backgroundColor = Color(0xFFFBEACC).copy(alpha = 0.48f)

@Preview(showBackground = true, device = "id:pixel_7_pro")
@Composable
fun ScreenPreview() {
    JumpAnimationTheme {
        MainScreen()
    }
}
