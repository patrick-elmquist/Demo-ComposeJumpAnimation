package com.patrick.elmquist.demo.jumpanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrick.elmquist.demo.jumpanimation.ui.theme.JumpAnimationTheme

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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(top = 112.dp, bottom = 48.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
    ) {
        state.toSortedMap().forEach { (emoji, count) ->
            JumpingEmoji(
                emoji = emoji,
                onClick = { state[emoji] = count + 1 },
                modifier = Modifier.weight(1f),
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
