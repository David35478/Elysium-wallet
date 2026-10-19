package com.example.data

import com.example.model.Chain
import com.example.model.ChainIconType
import com.example.model.NftItem
import com.example.model.TokenAsset
import com.example.model.TokenIconType
import com.example.model.TransactionItem
import com.example.model.TxType
import com.example.model.WalletAccount

object SampleData {

    val sampleWallets = listOf(
        WalletAccount(
            id = "w1",
            name = "Wallet 1",
            shortAddress = "0x0cce...9895",
            fullAddress = "0x0cce81a547FdB8E56Bdf3E44B1578322699895",
            totalBalanceUsd = 12483.20,
            change24hUsd = 28.10,
            change24hPct = 0.41
        ),
        WalletAccount(
            id = "w2",
            name = "DeFi Vault",
            shortAddress = "0x7a81...32fe",
            fullAddress = "0x7a81F0839CeB6c3e9810Da15A63F8B3e32fe1290",
            totalBalanceUsd = 45120.75,
            change24hUsd = -142.30,
            change24hPct = -0.31
        ),
        WalletAccount(
            id = "w3",
            name = "Trading Subaccount",
            shortAddress = "0x3f12...a901",
            fullAddress = "0x3f12CcB891A965eD813426AbcE005481a901ee19",
            totalBalanceUsd = 8240.10,
            change24hUsd = 110.50,
            change24hPct = 1.35
        )
    )

    val sampleTokens = listOf(
        TokenAsset(
            id = "eth",
            symbol = "ETH",
            name = "Ethereum",
            balance = 0.019864,
            balanceDisplay = "0.019864 ETH",
            fiatPrice = 2376.15,
            fiatValue = 47.20,
            fiatDisplay = "$47.20",
            priceChange24hPct = 0.3,
            priceChangeDisplay = "▲ 0.3%",
            isPositive = true,
            chain = "Ethereum",
            iconType = TokenIconType.ETH
        ),
        TokenAsset(
            id = "bnb",
            symbol = "BNB",
            name = "BNB Chain",
            balance = 24.48,
            balanceDisplay = "24.48 BNB",
            fiatPrice = 574.80,
            fiatValue = 9.09,
            fiatDisplay = "$9.09",
            priceChange24hPct = 0.9,
            priceChangeDisplay = "▲ 0.9%",
            isPositive = true,
            chain = "BNB Chain",
            iconType = TokenIconType.BNB
        ),
        TokenAsset(
            id = "hype",
            symbol = "HYPE",
            name = "Hyperliquid",
            balance = 120.50,
            balanceDisplay = "120.50 HYPE",
            fiatPrice = 24.15,
            fiatValue = 2910.08,
            fiatDisplay = "$2,910.08",
            priceChange24hPct = 4.8,
            priceChangeDisplay = "▲ 4.8%",
            isPositive = true,
            chain = "Hyperliquid",
            iconType = TokenIconType.HYPE
        ),
        TokenAsset(
            id = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            balance = 0.1250,
            balanceDisplay = "0.1250 BTC",
            fiatPrice = 67450.00,
            fiatValue = 8431.25,
            fiatDisplay = "$8,431.25",
            priceChange24hPct = 1.2,
            priceChangeDisplay = "▲ 1.2%",
            isPositive = true,
            chain = "Bitcoin",
            iconType = TokenIconType.BTC
        ),
        TokenAsset(
            id = "usdt",
            symbol = "USDT",
            name = "Tether USD",
            balance = 850.00,
            balanceDisplay = "850.00 USDT",
            fiatPrice = 1.00,
            fiatValue = 850.00,
            fiatDisplay = "$850.00",
            priceChange24hPct = 0.01,
            priceChangeDisplay = "▲ 0.01%",
            isPositive = true,
            chain = "BNB Chain",
            iconType = TokenIconType.USDT
        ),
        TokenAsset(
            id = "sol",
            symbol = "SOL",
            name = "Solana",
            balance = 1.55,
            balanceDisplay = "1.55 SOL",
            fiatPrice = 152.00,
            fiatValue = 235.58,
            fiatDisplay = "$235.58",
            priceChange24hPct = -0.5,
            priceChangeDisplay = "▼ 0.5%",
            isPositive = false,
            chain = "Solana",
            iconType = TokenIconType.SOL
        )
    )

    val sampleChains = listOf(
        Chain(
            id = "bnb",
            name = "BNB Chain",
            symbol = "BNB",
            iconType = ChainIconType.BNB
        ),
        Chain(
            id = "eth",
            name = "Ethereum",
            symbol = "ETH",
            iconType = ChainIconType.ETH
        ),
        Chain(
            id = "hl",
            name = "Hyperliquid L1",
            symbol = "HYPE",
            iconType = ChainIconType.HYPERLIQUID
        ),
        Chain(
            id = "sol",
            name = "Solana",
            symbol = "SOL",
            iconType = ChainIconType.SOLANA
        ),
        Chain(
            id = "arb",
            name = "Arbitrum One",
            symbol = "ARB",
            iconType = ChainIconType.ARBITRUM
        )
    )

    val sampleNfts = listOf(
        NftItem(
            id = "nft-1",
            name = "HyperLiquid Genesis #1042",
            collection = "Hyperliquid Originals",
            tokenId = "#1042",
            floorPriceEth = "1.85 ETH ($4,395)",
            gradientPrimaryHex = 0xFF0B3431,
            gradientSecondaryHex = 0xFF97FFE5
        ),
        NftItem(
            id = "nft-2",
            name = "Elysium Pioneer Pass",
            collection = "Elysium Club",
            tokenId = "#089",
            floorPriceEth = "0.75 ETH ($1,782)",
            gradientPrimaryHex = 0xFF10443F,
            gradientSecondaryHex = 0xFFB0FFED
        ),
        NftItem(
            id = "nft-3",
            name = "Cyber Penguin #7831",
            collection = "Cyber World",
            tokenId = "#7831",
            floorPriceEth = "2.40 ETH ($5,702)",
            gradientPrimaryHex = 0xFF181B21,
            gradientSecondaryHex = 0xFF9945FF
        )
    )

    val sampleTransactions = listOf(
        TransactionItem(
            id = "tx-1",
            title = "Swapped BNB for ETH",
            subtitle = "HyperSwap Router",
            amount = "+0.0198 ETH",
            fiatAmount = "$47.20",
            status = "Completed",
            timestamp = "10 mins ago",
            type = TxType.SWAP
        ),
        TransactionItem(
            id = "tx-2",
            title = "Received USDT",
            subtitle = "From 0x92b1...38a2",
            amount = "+850.00 USDT",
            fiatAmount = "$850.00",
            status = "Confirmed",
            timestamp = "2 hours ago",
            type = TxType.RECEIVE
        ),
        TransactionItem(
            id = "tx-3",
            title = "Sent HYPE",
            subtitle = "To Elysium Staking Pool",
            amount = "-25.00 HYPE",
            fiatAmount = "$603.75",
            status = "Confirmed",
            timestamp = "Yesterday",
            type = TxType.SEND
        )
    )
}
