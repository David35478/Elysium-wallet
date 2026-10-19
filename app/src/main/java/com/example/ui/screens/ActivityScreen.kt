package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Screen
import com.example.model.TransactionItem
import com.example.model.TxType
import com.example.ui.theme.ElysiumCanvas
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary
import com.example.viewmodel.WalletUiState
import com.example.viewmodel.WalletViewModel

@Composable
fun ActivityScreen(
    uiState: WalletUiState,
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumCanvas)
            .statusBarsPadding()
            .testTag("activity_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ElysiumCardAlt)
                            .border(1.dp, ElysiumCardBorder, CircleShape)
                            .testTag("activity_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ElysiumTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Activity & History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumTextPrimary
                    )
                }
            }

            // Wallet Subtitle
            item {
                Text(
                    text = "Showing recent transactions for ${uiState.selectedWallet.name}",
                    fontSize = 13.sp,
                    color = ElysiumTextSecondary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Transaction Items
            items(uiState.transactions) { tx ->
                TransactionRowCard(tx = tx)
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}

@Composable
private fun TransactionRowCard(tx: TransactionItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ElysiumCardBg)
            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(22.dp))
            .padding(16.dp)
            .testTag("tx_item_${tx.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tx Type Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ElysiumCardAlt)
                    .border(1.dp, ElysiumCardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (tx.type) {
                    TxType.SWAP -> Icons.Default.SwapHoriz
                    TxType.SEND -> Icons.AutoMirrored.Filled.CallMade
                    TxType.RECEIVE -> Icons.AutoMirrored.Filled.CallReceived
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElysiumMint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElysiumTextPrimary
                )
                Text(
                    text = "${tx.subtitle} • ${tx.timestamp}",
                    fontSize = 12.sp,
                    color = ElysiumTextTertiary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = tx.amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (tx.amount.startsWith("+")) ElysiumGreenPositive else ElysiumTextPrimary
                )
                Text(
                    text = tx.fiatAmount,
                    fontSize = 12.sp,
                    color = ElysiumTextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
