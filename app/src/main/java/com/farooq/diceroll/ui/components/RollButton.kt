package com.farooq.diceroll.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farooq.diceroll.R
import com.farooq.diceroll.ui.theme.AppGradients
import com.farooq.diceroll.ui.theme.Orange500

/**
 * The big orange ROLL DICE button.
 *
 * While a roll is running the button is disabled and shows "ROLLING…", which together with the
 * check inside the ViewModel makes accidental double rolls impossible.
 */
@Composable
fun RollButton(
    isRolling: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed && !isRolling) 0.975f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "rollButtonPress",
    )
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = if (isRolling) 4.dp else 18.dp,
                shape = shape,
                clip = false,
                ambientColor = Orange500.copy(alpha = 0.7f),
                spotColor = Orange500.copy(alpha = 0.7f),
            )
            .clip(shape)
            .background(AppGradients.rollButton(enabled = !isRolling))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isRolling,
                onClickLabel = stringResource(R.string.cd_roll_dice),
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_dice_filled),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(
                    if (isRolling) R.string.roll_button_rolling else R.string.roll_button,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                maxLines = 1,
            )
        }
    }
}
