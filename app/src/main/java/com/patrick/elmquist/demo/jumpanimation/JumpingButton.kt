package com.patrick.elmquist.demo.jumpanimation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrick.elmquist.demo.jumpanimation.ui.theme.JumpAnimationTheme
import com.patrick.elmquist.demo.jumpanimation.ui.theme.Typography

// TODO Could just get rid of this file and move to Activity
@Composable
internal fun JumpingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Text(
        text = "🍞🍦🍿",
        fontSize = 96.sp,
        modifier = modifier
            .jumpOnClick(rememberJumpAnimationState(onClick))
    )
}

@Preview
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
