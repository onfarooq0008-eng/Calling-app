package com.farooq.diceroll.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.ui.theme.AppGradients
import com.farooq.diceroll.ui.theme.LocalIsDarkTheme

/**
 * A rounded card with a soft shadow and a hairline border - the building block for the result
 * area, the settings groups and the roll history.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = LocalIsDarkTheme.current
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.40f else 0.55f)

    Column(
        modifier = modifier
            .shadow(
                elevation = if (isDark) 0.dp else 6.dp,
                shape = shape,
                clip = false,
            )
            .clip(shape)
            .background(AppGradients.card(isDark))
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(contentPadding),
        content = content,
    )
}
