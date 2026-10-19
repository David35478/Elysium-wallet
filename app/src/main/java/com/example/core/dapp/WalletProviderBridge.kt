package com.example.core.dapp

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.example.core.network.EvmRpcClient
import com.example.core.network.NetworkRegistry
import com.example.core.network.RpcResult
import com.example.data.repository.DappPermissionRepository
import com.example.model.DappChainSwitchRequest
import com.example.model.DappConnectionRequest
import com.example.model.DappSignMessageRequest
import com.example.model.DappTransactionRequest
import com.example.model.EvmNetworkConfig
import com.example.model.WalletAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

/**
 * Restricted JavaScript bridge interface for Web3 DApps.
 * Intercepts EIP-1193 calls from web content, enforces origin validation,
 * gates sensitive operations behind native Jetpack Compose approval sheets,
 * and executes safe read-only RPC calls over EvmRpcClient.
 */
class WalletProviderBridge(
    private val webViewProvider: () -> WebView?,
    private val getActiveWallet: () -> WalletAccount?,
    private val getSelectedNetwork: () -> EvmNetworkConfig,
    private val permissionRepository: DappPermissionRepository,
    private val networkRegistry: NetworkRegistry,
    private val scope: CoroutineScope,
    private val onConnectionRequested: (DappConnectionRequest) -> Unit,
    private val onSignMessageRequested: (DappSignMessageRequest) -> Unit,
    private val onTransactionRequested: (DappTransactionRequest) -> Unit,
    private val onChainSwitchRequested: (DappChainSwitchRequest) -> Unit
) {

    // Tracks pending requests currently awaiting native user approval
    private val pendingRequests = mutableMapOf<String, PendingRequestRecord>()

    data class PendingRequestRecord(
        val requestId: String,
        val origin: String,
        val method: String,
        val accountAddress: String,
        val networkChainId: Long
    )

    @JavascriptInterface
    fun postMessage(messageJson: String) {
        scope.launch(Dispatchers.Default) {
            handleIncomingMessage(messageJson)
        }
    }

    private suspend fun handleIncomingMessage(messageJson: String) {
        val json = try {
            JSONObject(messageJson)
        } catch (_: Exception) {
            return
        }

        val id = json.optString("id", "")
        val method = json.optString("method", "")
        val params = json.optJSONArray("params") ?: JSONArray()

        if (id.isBlank() || method.isBlank()) {
            return
        }

        val currentUrl = withContext(Dispatchers.Main) {
            webViewProvider()?.url ?: ""
        }

        val origin = DappPermissionRepository.extractOrigin(currentUrl)
        if (origin == null) {
            sendError(id, 4100, "Unauthorized: Requests are restricted to secure HTTPS origins or local development")
            return
        }

        val hostname = DappPermissionRepository.extractHostname(currentUrl)
        val activeWallet = getActiveWallet()
        if (activeWallet == null) {
            sendError(id, 4100, "Wallet is locked or uninitialized")
            return
        }

        val currentNetwork = getSelectedNetwork()

        when (method) {
            "eth_requestAccounts" -> {
                if (permissionRepository.isOriginApproved(origin, activeWallet.fullAddress)) {
                    val accountsArray = JSONArray().apply { put(activeWallet.fullAddress) }
                    sendSuccess(id, accountsArray)
                } else {
                    val request = DappConnectionRequest(
                        requestId = id,
                        origin = origin,
                        hostname = hostname,
                        targetChainId = currentNetwork.chainId
                    )
                    registerPending(id, origin, method, activeWallet.fullAddress, currentNetwork.chainId)
                    withContext(Dispatchers.Main) {
                        onConnectionRequested(request)
                    }
                }
            }

            "eth_accounts" -> {
                val accountsArray = JSONArray()
                if (permissionRepository.isOriginApproved(origin, activeWallet.fullAddress)) {
                    accountsArray.put(activeWallet.fullAddress)
                }
                sendSuccess(id, accountsArray)
            }

            "eth_chainId" -> {
                sendSuccess(id, currentNetwork.chainIdHex)
            }

            "net_version" -> {
                sendSuccess(id, currentNetwork.chainId.toString())
            }

            "wallet_switchEthereumChain" -> {
                val firstParam = params.optJSONObject(0)
                val targetChainIdHex = firstParam?.optString("chainId", "") ?: ""
                val targetChainId = EvmRpcClient.parseHexQuantityToLong(targetChainIdHex)

                if (targetChainId == null) {
                    sendError(id, -32602, "Invalid params: chainId hex is required")
                    return
                }

                if (targetChainId == currentNetwork.chainId) {
                    sendSuccess(id, JSONObject.NULL)
                    return
                }

                val targetNetwork = networkRegistry.getNetworkByChainId(targetChainId)
                if (targetNetwork == null) {
                    // Standard EIP-1193 error code for unrecognized chain
                    sendError(id, 4902, "Unrecognized chain ID: $targetChainId")
                    return
                }

                val request = DappChainSwitchRequest(
                    requestId = id,
                    origin = origin,
                    hostname = hostname,
                    targetChainId = targetChainId,
                    targetNetwork = targetNetwork
                )
                registerPending(id, origin, method, activeWallet.fullAddress, currentNetwork.chainId)
                withContext(Dispatchers.Main) {
                    onChainSwitchRequested(request)
                }
            }

            "personal_sign" -> {
                if (!permissionRepository.isOriginApproved(origin, activeWallet.fullAddress)) {
                    sendError(id, 4100, "Unauthorized: DApp is not connected")
                    return
                }

                // In standard web3 personal_sign params are [message, address] or [address, message]
                val param0 = params.optString(0, "")
                val param1 = params.optString(1, "")
                val (rawMessage, targetAddress) = if (param0.startsWith("0x") && param0.length == 42) {
                    Pair(param1, param0)
                } else {
                    Pair(param0, param1)
                }

                val (displayMessage, isOpaque) = decodePersonalSignMessage(rawMessage)

                val request = DappSignMessageRequest(
                    requestId = id,
                    origin = origin,
                    hostname = hostname,
                    accountAddress = activeWallet.fullAddress,
                    rawMessage = rawMessage,
                    displayMessage = displayMessage,
                    isOpaque = isOpaque
                )
                registerPending(id, origin, method, activeWallet.fullAddress, currentNetwork.chainId)
                withContext(Dispatchers.Main) {
                    onSignMessageRequested(request)
                }
            }

            "eth_sendTransaction" -> {
                if (!permissionRepository.isOriginApproved(origin, activeWallet.fullAddress)) {
                    sendError(id, 4100, "Unauthorized: DApp is not connected")
                    return
                }

                val txObj = params.optJSONObject(0)
                if (txObj == null) {
                    sendError(id, -32602, "Invalid transaction parameters")
                    return
                }

                val from = txObj.optString("from", activeWallet.fullAddress)
                val to = txObj.optString("to", "")
                val valueHex = txObj.optString("value", "0x0")
                val data = txObj.optString("data", "0x")
                val gasLimitHex = txObj.optString("gas", txObj.optString("gasLimit", "0x5208"))
                val gasPriceHex = txObj.optString("gasPrice", "0x0")

                val valueWei = EvmRpcClient.parseHexQuantityToBigInteger(valueHex) ?: BigInteger.ZERO
                val formattedValue = "${EvmRpcClient.weiToFormattedAmount(valueHex, currentNetwork.nativeCurrencyDecimals)} ${currentNetwork.nativeCurrencySymbol}"
                val gasLimit = EvmRpcClient.parseHexQuantityToBigInteger(gasLimitHex) ?: BigInteger.valueOf(21000)
                val gasPrice = EvmRpcClient.parseHexQuantityToBigInteger(gasPriceHex) ?: BigInteger.ZERO

                val estimatedFeeWei = gasLimit.multiply(if (gasPrice > BigInteger.ZERO) gasPrice else BigInteger.valueOf(20_000_000_000L))
                val estimatedFeeFormatted = "${EvmRpcClient.weiToFormattedAmount("0x" + estimatedFeeWei.toString(16), currentNetwork.nativeCurrencyDecimals)} ${currentNetwork.nativeCurrencySymbol}"

                val isContract = data.length > 2 && data != "0x"

                val request = DappTransactionRequest(
                    requestId = id,
                    origin = origin,
                    hostname = hostname,
                    fromAddress = from,
                    toAddress = to,
                    valueWei = valueWei,
                    formattedValue = formattedValue,
                    data = data,
                    gasLimit = gasLimit,
                    gasPrice = gasPrice,
                    estimatedFeeFormatted = estimatedFeeFormatted,
                    isContractInteraction = isContract
                )
                registerPending(id, origin, method, activeWallet.fullAddress, currentNetwork.chainId)
                withContext(Dispatchers.Main) {
                    onTransactionRequested(request)
                }
            }

            // Transparent read-only JSON-RPC delegates
            "eth_blockNumber" -> {
                val rpc = EvmRpcClient(currentNetwork)
                when (val result = rpc.getLatestBlockNumber()) {
                    is RpcResult.Success -> sendSuccess(id, "0x" + result.data.toString(16))
                    is RpcResult.Error -> sendError(id, -32603, result.message)
                    is RpcResult.WrongChain -> sendError(id, -32603, "Wrong chain ID detected on RPC")
                }
            }

            "eth_getBalance" -> {
                val targetAddr = params.optString(0, activeWallet.fullAddress)
                val rpc = EvmRpcClient(currentNetwork)
                when (val result = rpc.getBalance(targetAddr)) {
                    is RpcResult.Success -> sendSuccess(id, result.data.rawWeiHex)
                    is RpcResult.Error -> sendError(id, -32603, result.message)
                    is RpcResult.WrongChain -> sendError(id, -32603, "Wrong chain ID detected on RPC")
                }
            }

            "eth_call" -> {
                val callObj = params.optJSONObject(0)
                val to = callObj?.optString("to", "") ?: ""
                val data = callObj?.optString("data", "0x") ?: "0x"
                val rpc = EvmRpcClient(currentNetwork)
                when (val result = rpc.call(to, data)) {
                    is RpcResult.Success -> sendSuccess(id, result.data)
                    is RpcResult.Error -> sendError(id, -32603, result.message)
                    is RpcResult.WrongChain -> sendError(id, -32603, "Wrong chain ID detected on RPC")
                }
            }

            "eth_estimateGas" -> {
                val callObj = params.optJSONObject(0)
                val to = callObj?.optString("to", "") ?: ""
                val data = callObj?.optString("data", "0x") ?: "0x"
                val from = callObj?.optString("from", activeWallet.fullAddress)
                val rpc = EvmRpcClient(currentNetwork)
                when (val result = rpc.estimateGas(to, data, from)) {
                    is RpcResult.Success -> sendSuccess(id, "0x" + result.data.toString(16))
                    is RpcResult.Error -> sendError(id, -32603, result.message)
                    is RpcResult.WrongChain -> sendError(id, -32603, "Wrong chain ID detected on RPC")
                }
            }

            "eth_getTransactionReceipt" -> {
                val txHash = params.optString(0, "")
                val rpc = EvmRpcClient(currentNetwork)
                when (val result = rpc.getTransactionReceipt(txHash)) {
                    is RpcResult.Success -> {
                        if (result.data == null) {
                            sendSuccess(id, JSONObject.NULL)
                        } else {
                            sendSuccess(id, JSONObject(result.data))
                        }
                    }
                    is RpcResult.Error -> sendError(id, -32603, result.message)
                    is RpcResult.WrongChain -> sendError(id, -32603, "Wrong chain ID detected on RPC")
                }
            }

            else -> {
                sendError(id, -32601, "The method '$method' does not exist or is not available")
            }
        }
    }

    private fun registerPending(requestId: String, origin: String, method: String, accountAddress: String, chainId: Long) {
        synchronized(pendingRequests) {
            pendingRequests[requestId] = PendingRequestRecord(requestId, origin, method, accountAddress, chainId)
        }
    }

    /**
     * Responds to an approved connection request.
     */
    fun approveConnection(requestId: String, origin: String, accountAddress: String) {
        permissionRepository.grantPermission(origin, accountAddress)
        val accounts = JSONArray().apply { put(accountAddress) }
        sendSuccess(requestId, accounts)
        emitAccountsChanged(listOf(accountAddress))
        synchronized(pendingRequests) { pendingRequests.remove(requestId) }
    }

    /**
     * Rejects a pending request with EIP-1193 user-rejected code 4001.
     */
    fun rejectRequest(requestId: String, reason: String = "User rejected the request") {
        sendError(requestId, 4001, reason)
        synchronized(pendingRequests) { pendingRequests.remove(requestId) }
    }

    /**
     * Fulfills a signed personal message response.
     */
    fun approveSignMessage(requestId: String, signatureHex: String) {
        sendSuccess(requestId, signatureHex)
        synchronized(pendingRequests) { pendingRequests.remove(requestId) }
    }

    /**
     * Fulfills an approved broadcasted transaction hash response.
     */
    fun approveTransaction(requestId: String, txHash: String) {
        sendSuccess(requestId, txHash)
        synchronized(pendingRequests) { pendingRequests.remove(requestId) }
    }

    /**
     * Fulfills a network switch approval.
     */
    fun approveChainSwitch(requestId: String, targetNetwork: EvmNetworkConfig) {
        sendSuccess(requestId, JSONObject.NULL)
        emitChainChanged(targetNetwork.chainIdHex)
        synchronized(pendingRequests) { pendingRequests.remove(requestId) }
    }

    /**
     * Cancels any pending requests when the WebView navigates to a new origin
     * or when the user changes active account/network.
     */
    fun cancelPendingRequestsForOriginOrContext(newOrigin: String?, newAccount: String?, newChainId: Long?) {
        val toCancel = mutableListOf<String>()
        synchronized(pendingRequests) {
            for ((id, record) in pendingRequests) {
                val originChanged = newOrigin != null && !record.origin.equals(newOrigin, ignoreCase = true)
                val accountChanged = newAccount != null && record.accountAddress != newAccount
                val networkChanged = newChainId != null && record.networkChainId != newChainId
                if (originChanged || accountChanged || networkChanged) {
                    toCancel.add(id)
                }
            }
            toCancel.forEach { pendingRequests.remove(it) }
        }

        toCancel.forEach { id ->
            sendError(id, 4001, "Request was cancelled because the origin, account, or network changed")
        }
    }

    fun emitAccountsChanged(accounts: List<String>) {
        val array = JSONArray()
        accounts.forEach { array.put(it) }
        executeJavaScript("window._koroEmitAccountsChanged && window._koroEmitAccountsChanged($array);")
    }

    fun emitChainChanged(chainIdHex: String) {
        executeJavaScript("window._koroEmitChainChanged && window._koroEmitChainChanged('$chainIdHex');")
    }

    private fun sendSuccess(id: String, result: Any) {
        val resultStr = when (result) {
            is String -> JSONObject.quote(result)
            is JSONObject, is JSONArray -> result.toString()
            JSONObject.NULL -> "null"
            else -> result.toString()
        }
        executeJavaScript("window._koroDispatchResponse && window._koroDispatchResponse('$id', $resultStr, null);")
    }

    private fun sendError(id: String, code: Int, message: String) {
        val errorObj = JSONObject().apply {
            put("code", code)
            put("message", message)
        }
        executeJavaScript("window._koroDispatchResponse && window._koroDispatchResponse('$id', null, $errorObj);")
    }

    private fun executeJavaScript(script: String) {
        scope.launch(Dispatchers.Main) {
            try {
                webViewProvider()?.evaluateJavascript(script, null)
            } catch (_: Exception) {
            }
        }
    }

    private fun decodePersonalSignMessage(raw: String): Pair<String, Boolean> {
        if (!raw.startsWith("0x")) {
            return Pair(raw, false)
        }
        return try {
            val hex = raw.substring(2)
            if (hex.length % 2 != 0) return Pair(raw, true)
            val bytes = ByteArray(hex.length / 2)
            for (i in bytes.indices) {
                val index = i * 2
                bytes[i] = hex.substring(index, index + 2).toInt(16).toByte()
            }
            val text = String(bytes, Charsets.UTF_8)
            // Check if string contains mostly printable ASCII or UTF-8 characters
            val isPrintable = text.all { it.code in 32..126 || it == '\n' || it == '\r' || it == '\t' }
            if (isPrintable) Pair(text, false) else Pair(raw, true)
        } catch (_: Exception) {
            Pair(raw, true)
        }
    }
}
