package com.example.ui.screens.browser

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.dapp.WalletProviderBridge
import com.example.model.DappBookmark
import com.example.model.DappHistoryItem
import com.example.model.EvmNetworkConfig
import com.example.model.FeaturedDapp
import com.example.model.WalletAccount
import com.example.ui.components.DappWebViewContainer
import com.example.ui.theme.ElysiumAmber
import com.example.ui.theme.ElysiumCanvas
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumPillActive
import com.example.ui.theme.ElysiumPillBg
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary
import java.net.URLEncoder

@Composable
fun DappBrowserScreen(
    selectedNetwork: EvmNetworkConfig,
    activeWallet: WalletAccount,
    currentUrl: String?,
    pageTitle: String?,
    isLoading: Boolean,
    progress: Int,
    errorMessage: String?,
    bookmarks: List<DappBookmark>,
    history: List<DappHistoryItem>,
    featuredDapps: List<FeaturedDapp>,
    bridge: WalletProviderBridge,
    onNavigateUrl: (String) -> Unit,
    onCloseBrowser: () -> Unit,
    onToggleBookmark: (String, String) -> Unit,
    onRemoveBookmark: (String) -> Unit,
    onClearHistory: () -> Unit,
    onNetworkClick: () -> Unit,
    onWalletClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var inputUrl by remember(currentUrl) { mutableStateOf(currentUrl ?: "") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showMenu by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Browser Back Navigation: First go back within WebView history; if at root, exit to Discovery landing
    BackHandler {
        val wv = webViewInstance
        if (currentUrl != null && wv != null && wv.canGoBack()) {
            wv.goBack()
        } else if (currentUrl != null) {
            onCloseBrowser()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumCanvas)
            .statusBarsPadding()
            .testTag("dapp_browser_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // A. Top App Bar
            DappBrowserTopBar(
                selectedNetwork = selectedNetwork,
                activeWallet = activeWallet,
                onNetworkClick = onNetworkClick,
                onWalletClick = onWalletClick
            )

            // B. Website Address Bar
            DappAddressBar(
                inputUrl = inputUrl,
                currentUrl = currentUrl,
                isLoading = isLoading,
                isBookmarked = bookmarks.any { it.url.equals(currentUrl ?: "", ignoreCase = true) },
                onInputChange = { inputUrl = it },
                onSubmit = {
                    focusManager.clearFocus()
                    val target = sanitizeOrSearchUrl(inputUrl)
                    onNavigateUrl(target)
                },
                onRefresh = {
                    webViewInstance?.reload()
                },
                onToggleBookmark = {
                    currentUrl?.let { url ->
                        onToggleBookmark(pageTitle ?: url, url)
                    }
                },
                onMenuClick = { showMenu = true },
                onClearInput = { inputUrl = "" }
            )

            // Menu Dropdown
            Box(modifier = Modifier.align(Alignment.End).padding(end = 16.dp)) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(ElysiumCardBg).border(1.dp, ElysiumCardBorder, RoundedCornerShape(12.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("Reload", color = ElysiumTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = ElysiumMint) },
                        onClick = {
                            showMenu = false
                            webViewInstance?.reload()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Open in External Browser", color = ElysiumTextPrimary) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = ElysiumMint) },
                        onClick = {
                            showMenu = false
                            currentUrl?.let { url ->
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Clear Browsing History", color = ElysiumAmber) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = ElysiumAmber) },
                        onClick = {
                            showMenu = false
                            onClearHistory()
                        }
                    )
                    if (currentUrl != null) {
                        DropdownMenuItem(
                            text = { Text("Back to Discovery", color = ElysiumTextSecondary) },
                            leadingIcon = { Icon(Icons.Outlined.Explore, contentDescription = null, tint = ElysiumTextSecondary) },
                            onClick = {
                                showMenu = false
                                onCloseBrowser()
                            }
                        )
                    }
                }
            }

            // Progress Bar
            if (isLoading && progress in 1..99) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp),
                    color = ElysiumMint,
                    trackColor = Color(0x338DF5DC)
                )
            }

            // Main Content Area: WebView if currentUrl is not null, otherwise Discovery Landing
            if (currentUrl != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    DappWebViewContainer(
                        urlToLoad = currentUrl,
                        selectedNetwork = selectedNetwork,
                        activeAddress = activeWallet.fullAddress,
                        bridge = bridge,
                        onWebViewCreated = { webViewInstance = it },
                        onPageStarted = { /* handled */ },
                        onPageFinished = { url, _ -> inputUrl = url },
                        onProgressChanged = { /* handled */ },
                        onErrorReceived = { /* handled in error state */ },
                        onSslError = { /* strict handler cancels navigation */ },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Error overlay
                    if (errorMessage != null) {
                        DappErrorOverlay(
                            errorMessage = errorMessage,
                            onRetry = { webViewInstance?.reload() },
                            onGoHome = onCloseBrowser
                        )
                    }
                }
            } else {
                // Discovery Landing: Featured DApps, Bookmarks & History
                DappDiscoveryLanding(
                    categories = listOf("All", "DeFi", "DEX", "Bridge", "NFT", "Gaming", "Explore"),
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it },
                    featuredDapps = featuredDapps.filter {
                        selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
                    },
                    bookmarks = bookmarks,
                    history = history,
                    onDappClick = { dapp ->
                        onNavigateUrl(dapp.url)
                    },
                    onBookmarkClick = { bookmark ->
                        onNavigateUrl(bookmark.url)
                    },
                    onRemoveBookmark = onRemoveBookmark,
                    onHistoryClick = { item ->
                        onNavigateUrl(item.url)
                    },
                    onClearHistory = onClearHistory
                )
            }
        }
    }
}

@Composable
private fun DappBrowserTopBar(
    selectedNetwork: EvmNetworkConfig,
    activeWallet: WalletAccount,
    onNetworkClick: () -> Unit,
    onWalletClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ElysiumCardAlt)
                    .border(1.dp, Color(0x338DF5DC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumMint
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "DApp Browser",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ElysiumTextPrimary
            )
        }

        // Action shortcuts: Network Chip & Wallet Account
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Network Selector Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElysiumCardAlt)
                    .border(1.dp, Color(0x228DF5DC), RoundedCornerShape(12.dp))
                    .clickable(onClick = onNetworkClick)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("browser_network_selector_chip")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (selectedNetwork.isTestnet) ElysiumAmber else ElysiumGreenPositive)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedNetwork.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElysiumMint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Wallet Account Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElysiumCardAlt)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onWalletClick)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("browser_wallet_account_chip")
            ) {
                Text(
                    text = activeWallet.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElysiumTextSecondary
                )
            }
        }
    }
}

@Composable
private fun DappAddressBar(
    inputUrl: String,
    currentUrl: String?,
    isLoading: Boolean,
    isBookmarked: Boolean,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onRefresh: () -> Unit,
    onToggleBookmark: () -> Unit,
    onMenuClick: () -> Unit,
    onClearInput: () -> Unit
) {
    val isHttps = currentUrl?.startsWith("https://", ignoreCase = true) == true

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ElysiumCardAlt)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Security Lock Icon
            Icon(
                imageVector = if (isHttps) Icons.Default.Lock else Icons.Default.Language,
                contentDescription = if (isHttps) "Secure Connection" else "Web",
                tint = if (isHttps) ElysiumGreenPositive else ElysiumTextTertiary,
                modifier = Modifier
                    .size(18.dp)
                    .padding(start = 2.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // URL input field
            OutlinedTextField(
                value = inputUrl,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("browser_url_input"),
                placeholder = {
                    Text(
                        "Search or enter website address",
                        fontSize = 13.sp,
                        color = ElysiumTextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = { onSubmit() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = ElysiumTextPrimary,
                    unfocusedTextColor = ElysiumTextPrimary,
                    cursorColor = ElysiumMint
                )
            )

            // Clear button
            if (inputUrl.isNotBlank()) {
                IconButton(onClick = onClearInput, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = ElysiumTextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Refresh / Go button
            if (currentUrl != null) {
                IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = ElysiumMint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Bookmark toggle
                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) ElysiumMint else ElysiumTextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                IconButton(onClick = onSubmit, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search or Go",
                        tint = ElysiumMint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // More Menu
            IconButton(onClick = onMenuClick, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = ElysiumTextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DappDiscoveryLanding(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    featuredDapps: List<FeaturedDapp>,
    bookmarks: List<DappBookmark>,
    history: List<DappHistoryItem>,
    onDappClick: (FeaturedDapp) -> Unit,
    onBookmarkClick: (DappBookmark) -> Unit,
    onRemoveBookmark: (String) -> Unit,
    onHistoryClick: (DappHistoryItem) -> Unit,
    onClearHistory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = category.equals(selectedCategory, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ElysiumMint else ElysiumCardAlt)
                            .border(1.dp, if (isSelected) ElysiumMint else ElysiumCardBorder, RoundedCornerShape(20.dp))
                            .clickable { onSelectCategory(category) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("browser_category_$category")
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF111317) else ElysiumTextSecondary
                        )
                    }
                }
            }
        }

        // Featured DApps Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curated DApps",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumTextPrimary
                    )
                    Text(
                        text = "Sample Previews",
                        fontSize = 11.sp,
                        color = ElysiumTextTertiary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    featuredDapps.forEach { dapp ->
                        FeaturedDappCard(
                            dapp = dapp,
                            onClick = { onDappClick(dapp) }
                        )
                    }
                }
            }
        }

        // Quick Access / Bookmarks Section
        if (bookmarks.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Saved Bookmarks",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        bookmarks.forEach { bookmark ->
                            BookmarkRowItem(
                                bookmark = bookmark,
                                onClick = { onBookmarkClick(bookmark) },
                                onRemove = { onRemoveBookmark(bookmark.url) }
                            )
                        }
                    }
                }
            }
        }

        // Recent History Section
        if (history.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recently Visited",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElysiumTextPrimary
                        )

                        Text(
                            text = "Clear All",
                            fontSize = 12.sp,
                            color = ElysiumMint,
                            modifier = Modifier
                                .clickable(onClick = onClearHistory)
                                .testTag("browser_clear_history_btn")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        history.take(8).forEach { item ->
                            HistoryRowItem(
                                item = item,
                                onClick = { onHistoryClick(item) }
                            )
                        }
                    }
                }
            }
        }

        // Bottom padding for navigation bar
        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun FeaturedDappCard(
    dapp: FeaturedDapp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ElysiumCardAlt)
            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("featured_dapp_${dapp.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Emblem
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0B0D0F))
                    .border(1.dp, Color(0x338DF5DC), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dapp.name.take(2).uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumMint
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dapp.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElysiumTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElysiumPillBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = dapp.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElysiumMint
                        )
                    }
                }
                Text(
                    text = dapp.description,
                    fontSize = 12.sp,
                    color = ElysiumTextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open",
                tint = ElysiumTextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun BookmarkRowItem(
    bookmark: DappBookmark,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ElysiumCardAlt)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = null,
                tint = ElysiumMint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = bookmark.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElysiumTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = bookmark.url,
                    fontSize = 11.sp,
                    color = ElysiumTextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = ElysiumTextTertiary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun HistoryRowItem(
    item: DappHistoryItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ElysiumCardAlt)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = ElysiumTextTertiary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ElysiumTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.url,
                fontSize = 11.sp,
                color = ElysiumTextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DappErrorOverlay(
    errorMessage: String,
    onRetry: () -> Unit,
    onGoHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE111317))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFA800)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ElysiumAmber,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Failed to Load Page",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ElysiumTextPrimary
            )

            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = ElysiumTextTertiary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onGoHome,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElysiumTextSecondary)
                ) {
                    Text("Go to Discovery")
                }

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElysiumMint,
                        contentColor = Color(0xFF111317)
                    )
                ) {
                    Text("Retry", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun sanitizeOrSearchUrl(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isBlank()) return "https://duckduckgo.com"

    // If starts with http:// or https://, check hostname
    if (trimmed.startsWith("https://", ignoreCase = true)) {
        return trimmed
    }
    if (trimmed.startsWith("http://localhost", ignoreCase = true) || trimmed.startsWith("http://127.0.0.1", ignoreCase = true)) {
        return trimmed
    }
    if (trimmed.startsWith("http://", ignoreCase = true)) {
        // Upgrade to https if possible
        return "https://" + trimmed.substring(7)
    }

    // Check if looks like a domain name (e.g. app.uniswap.org or elysium.kinetiq.xyz)
    val hasDot = trimmed.contains(".") && !trimmed.contains(" ")
    if (hasDot) {
        return "https://$trimmed"
    }

    // Otherwise, perform safe web search via DuckDuckGo
    return try {
        "https://duckduckgo.com/?q=" + URLEncoder.encode(trimmed, "UTF-8")
    } catch (_: Exception) {
        "https://duckduckgo.com/?q=$trimmed"
    }
}
