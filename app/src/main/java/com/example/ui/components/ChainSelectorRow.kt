package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chain
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumPillBg
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun ChainSelectorRow(
    sourceChain: Chain,
    destinationChain: Chain,
    onSourceChainClick: () -> Unit,
    onDestinationChainClick: () -> Unit,
    onToggleChains: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Source Chain Pill
        ChainPill(
            chain = sourceChain,
            onClick = onSourceChainClick,
            testTag = "source_chain_pill",
            modifier = Modifier.weight(1f)
        )

        // Horizontal Chain Swap Toggle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ElysiumPillBg)
                .border(1.dp, ElysiumCardBorder, CircleShape)
                .clickable(onClick = onToggleChains)
                .testTag("chain_swap_toggle_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Swap Chains",
                tint = ElysiumTextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }

        // Destination Chain Pill
        ChainPill(
            chain = destinationChain,
            onClick = onDestinationChainClick,
            testTag = "destination_chain_pill",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ChainPill(
    chain: Chain,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ElysiumPillBg)
            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
            .testTag(testTag),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChainLogo(
                    iconType = chain.iconType,
                    size = 20.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = chain.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElysiumTextPrimary.copy(alpha = 0.9f)
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ElysiumTextTertiary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
