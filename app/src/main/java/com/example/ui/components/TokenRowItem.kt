package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TokenAsset
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun TokenRowItem(
    token: TokenAsset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("asset_item_${token.symbol.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Token Logo
            TokenLogo(
                iconType = token.iconType,
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Token Name & Symbol
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = token.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElysiumTextPrimary,
                    lineHeight = 18.sp
                )
                Text(
                    text = token.symbol,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElysiumTextTertiary,
                    modifier = Modifier.padding(top = 2.dp),
                    lineHeight = 16.sp
                )
            }

            // Balance & Price
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = token.balanceDisplay,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumTextPrimary,
                    lineHeight = 18.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = token.fiatDisplay,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElysiumTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = token.priceChangeDisplay,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (token.isPositive) ElysiumGreenPositive else Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}
