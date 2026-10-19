package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NftItem
import com.example.model.Screen
import com.example.model.TokenAsset
import com.example.ui.components.HomeTab
import com.example.ui.components.SecurityBanner
import com.example.ui.components.SegmentedTabs
import com.example.ui.components.TokenRowItem
import com.example.ui.components.TopWalletHeaderCard
import com.example.ui.theme.ElysiumCanvas
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary
import com.example.viewmodel.WalletUiState
import com.example.viewmodel.WalletViewModel

import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun HomeScreen(
    uiState: WalletUiState,
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshTestnetBalance()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumCanvas)
            .statusBarsPadding()
            .testTag("home_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Top Header Card (Hyperliquid Theme with Elysium Testnet HYPE integration)
            item {
                TopWalletHeaderCard(
                    wallet = uiState.selectedWallet,
                    selectedNetwork = uiState.selectedNetwork,
                    networkStatus = uiState.networkStatus,
                    balanceFormatted = uiState.networkBalanceFormatted,
                    isNetworkLoading = uiState.isNetworkLoading,
                    networkErrorMessage = uiState.networkErrorMessage,
                    onRefreshClick = { viewModel.refreshNetworkBalance() },
                    onNetworkClick = { viewModel.setShowNetworkSelector(true) },
                    onSwitchWalletClick = { viewModel.setShowWalletSelector(true) },
                    onCopyAddressClick = { viewModel.copyAddressToClipboard() },
                    onHistoryClick = { viewModel.navigateTo(Screen.ACTIVITY) },
                    onSendClick = { viewModel.triggerActionNotImplemented("Send") },
                    onReceiveClick = { viewModel.triggerActionNotImplemented("Receive") },
                    onSwapClick = { viewModel.navigateTo(Screen.SWAP) },
                    onBuySellClick = { viewModel.triggerActionNotImplemented("Buy/Sell") }
                )
            }

            // Security Reminder Banner (if not dismissed)
            if (uiState.isSecurityBannerVisible) {
                item {
                    val activity = androidx.compose.ui.platform.LocalContext.current as? androidx.fragment.app.FragmentActivity
                    SecurityBanner(
                        onDismiss = { viewModel.dismissSecurityBanner() },
                        onClick = {
                            activity?.let { viewModel.requestViewSeedPhrase(it) }
                        }
                    )
                }
            }

            // Segmented Tabs: Crypto | NFTs
            item {
                SegmentedTabs(
                    selectedTab = uiState.selectedHomeTab,
                    onTabSelected = { viewModel.selectHomeTab(it) }
                )
            }

            // Tab Content: Token Asset List or NFTs
            if (uiState.selectedHomeTab == HomeTab.CRYPTO) {
                items(uiState.tokens) { token ->
                    TokenRowItem(
                        token = token,
                        onClick = { viewModel.setSelectedTokenDetails(token) }
                    )
                }
            } else {
                items(uiState.nfts) { nft ->
                    NftCardItem(
                        nft = nft,
                        onClick = { viewModel.triggerActionNotImplemented("NFT details for ${nft.name}") }
                    )
                }
            }

            // Bottom padding for bottom navigation bar
            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}

@Composable
private fun SampleDataIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1497FFE5))
            .border(1.dp, Color(0x2697FFE5), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("sample_data_indicator"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = ElysiumMint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Sample Data Mode • Offline Native Prototype",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ElysiumMint
            )
        }
    }
}

@Composable
private fun NftCardItem(
    nft: NftItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ElysiumCardAlt)
            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("nft_card_${nft.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gradient Artwork Placeholder
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(nft.gradientPrimaryHex),
                                Color(nft.gradientSecondaryHex)
                            )
                        )
                    )
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nft.tokenId,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nft.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElysiumTextPrimary
                )
                Text(
                    text = nft.collection,
                    fontSize = 12.sp,
                    color = ElysiumTextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Floor",
                    fontSize = 11.sp,
                    color = ElysiumTextTertiary
                )
                Text(
                    text = nft.floorPriceEth,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElysiumMint,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
