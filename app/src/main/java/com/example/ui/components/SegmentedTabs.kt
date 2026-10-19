package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumPillActive
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary

enum class HomeTab {
    CRYPTO,
    NFTS
}

@Composable
fun SegmentedTabs(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ElysiumCardAlt)
            .border(
                width = 1.dp,
                color = ElysiumCardBorder,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(4.dp)
            .testTag("home_segmented_tabs")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Crypto Tab
            val isCryptoSelected = selectedTab == HomeTab.CRYPTO
            val cryptoBgColor by animateColorAsState(
                targetValue = if (isCryptoSelected) ElysiumPillActive else Color.Transparent,
                label = "crypto_tab_bg"
            )
            val cryptoTextColor by animateColorAsState(
                targetValue = if (isCryptoSelected) ElysiumTextPrimary else ElysiumTextSecondary,
                label = "crypto_tab_text"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(cryptoBgColor)
                    .clickable { onTabSelected(HomeTab.CRYPTO) }
                    .testTag("tab_crypto_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Crypto",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = cryptoTextColor
                )
            }

            // NFTs Tab
            val isNftsSelected = selectedTab == HomeTab.NFTS
            val nftsBgColor by animateColorAsState(
                targetValue = if (isNftsSelected) ElysiumPillActive else Color.Transparent,
                label = "nfts_tab_bg"
            )
            val nftsTextColor by animateColorAsState(
                targetValue = if (isNftsSelected) ElysiumTextPrimary else ElysiumTextSecondary,
                label = "nfts_tab_text"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(nftsBgColor)
                    .clickable { onTabSelected(HomeTab.NFTS) }
                    .testTag("tab_nfts_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NFTs",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = nftsTextColor
                )
            }
        }
    }
}
