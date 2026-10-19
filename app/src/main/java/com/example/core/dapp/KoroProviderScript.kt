package com.example.core.dapp

import com.example.model.EvmNetworkConfig

/**
 * Generates EIP-1193 compatible JavaScript provider script to inject into DApps.
 * Sets up window.ethereum with secure promise-based communication over the Koro native bridge.
 */
object KoroProviderScript {

    fun generateInjectionScript(
        network: EvmNetworkConfig,
        approvedAddress: String?
    ): String {
        val selectedAddressJs = if (approvedAddress != null) "'$approvedAddress'" else "null"
        val chainIdHex = network.chainIdHex
        val networkVersion = network.chainId.toString()

        return """
        (function() {
            if (window._koroInjected) return;
            window._koroInjected = true;

            window._koroCallbacks = {};
            window._koroEventListeners = {};

            function emitEvent(eventName, payload) {
                const listeners = window._koroEventListeners[eventName] || [];
                listeners.forEach(fn => {
                    try { fn(payload); } catch (e) { console.error(e); }
                });
            }

            const ethereum = {
                isKoro: true,
                isMetaMask: true,
                chainId: '$chainIdHex',
                networkVersion: '$networkVersion',
                selectedAddress: $selectedAddressJs,

                isConnected: function() {
                    return true;
                },

                enable: function() {
                    return this.request({ method: 'eth_requestAccounts' });
                },

                on: function(event, callback) {
                    if (!window._koroEventListeners[event]) {
                        window._koroEventListeners[event] = [];
                    }
                    window._koroEventListeners[event].push(callback);
                    return this;
                },

                removeListener: function(event, callback) {
                    if (!window._koroEventListeners[event]) return this;
                    window._koroEventListeners[event] = window._koroEventListeners[event].filter(fn => fn !== callback);
                    return this;
                },

                request: function(args) {
                    return new Promise((resolve, reject) => {
                        if (!args || !args.method) {
                            reject(new Error('Invalid request: method is required'));
                            return;
                        }

                        const reqId = Date.now().toString(36) + Math.random().toString(36).substr(2, 5);
                        window._koroCallbacks[reqId] = { resolve, reject };

                        const payload = JSON.stringify({
                            id: reqId,
                            method: args.method,
                            params: args.params || []
                        });

                        if (window.KoroWalletBridge && typeof window.KoroWalletBridge.postMessage === 'function') {
                            window.KoroWalletBridge.postMessage(payload);
                        } else {
                            delete window._koroCallbacks[reqId];
                            reject(new Error('Koro Wallet native bridge is not available'));
                        }
                    });
                },

                sendAsync: function(payload, callback) {
                    this.request({ method: payload.method, params: payload.params })
                        .then(result => callback(null, { id: payload.id, jsonrpc: '2.0', result: result }))
                        .catch(err => callback(err, { id: payload.id, jsonrpc: '2.0', error: { message: err.message || 'Error', code: -32603 } }));
                },

                send: function(methodOrPayload, params) {
                    if (typeof methodOrPayload === 'string') {
                        return this.request({ method: methodOrPayload, params: params });
                    }
                    return this.request(methodOrPayload);
                }
            };

            window.ethereum = ethereum;

            // Global callbacks invoked by native Android Kotlin
            window._koroDispatchResponse = function(id, result, error) {
                const cb = window._koroCallbacks[id];
                if (!cb) return;
                delete window._koroCallbacks[id];

                if (error) {
                    const err = new Error(error.message || 'Request failed');
                    err.code = error.code || -32603;
                    cb.reject(err);
                } else {
                    cb.resolve(result);
                }
            };

            window._koroEmitAccountsChanged = function(accounts) {
                window.ethereum.selectedAddress = (accounts && accounts.length > 0) ? accounts[0] : null;
                emitEvent('accountsChanged', accounts);
            };

            window._koroEmitChainChanged = function(chainIdHex) {
                window.ethereum.chainId = chainIdHex;
                emitEvent('chainChanged', chainIdHex);
            };

            // Dispatch standard EIP-6963 ethereum#initialized event
            window.dispatchEvent(new Event('ethereum#initialized'));
        })();
        """.trimIndent()
    }
}
