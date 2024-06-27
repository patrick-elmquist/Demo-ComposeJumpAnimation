package com.patrick.elmquist.demo.jumpanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                Screen()
            }
        }
    }
}

data class Item(
    val emoji: String,
    val withTopExpand: Boolean,
    val withEndSquish: Boolean,
)

@Composable
private fun Screen() {
    val emojis = remember {
        listOf(
            Item("🍞", withTopExpand = false, withEndSquish = false),
            Item("🍦", withTopExpand = false, withEndSquish = true),
            
            Item("🍿", withTopExpand = true, withEndSquish = true),
        )
    }
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .background(Color(0xFFFBEACC).copy(alpha = 0.48f))
            .padding(top = 104.dp, bottom = 24.dp)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            emojis.take(3).forEach { item ->
                JumpingEmoji(
                    item = item,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun JumpingEmoji(
    item: Item,
    modifier: Modifier = Modifier,
) {
    var counter by remember { mutableIntStateOf(0) }
    Column(
        modifier = modifier,
        verticalArrangement = spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = item.emoji,
            fontSize = 96.sp,
            modifier = Modifier
                .jumpOnClick(
                    rememberJumpAnimationState(
                        withTopExpand = item.withTopExpand,
                        withEndSquish = item.withEndSquish,
                        onClick = { counter++ },
                    )
                )
        )

        Text(
            text = "Jumps: $counter",
            style = Typography.titleMedium,
            color = Color(0x8A000000),
            modifier = Modifier.alpha(if (counter > 0) 1f else 0f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenPreview() {
    JumpAnimationTheme {
        Screen()
    }
}
