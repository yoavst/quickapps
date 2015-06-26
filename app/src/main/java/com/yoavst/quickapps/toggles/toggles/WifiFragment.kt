package com.yoavst.quickapps.toggles.toggles

import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.provider.Settings
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.ToggleFragment
import kotlinx.android.synthetic.toggles_toggle_fragment.image
import kotlinx.android.synthetic.toggles_toggle_fragment.imageAnimation
import kotlinx.android.synthetic.toggles_toggle_fragment.text

public class WifiFragment : ToggleFragment() {
    val WifiOff by stringResource(R.string.wifi_off)
    val WifiOn by stringResource(R.string.wifi_on)
    val WifiNetworkAvailable by stringResource(R.string.wifi_network_available)
    val WifiConnected by stringResource(R.string.wifi_connected)
    val WifiTurnOff by stringResource(R.string.wifi_turning_off)
    val WifiTurnOn by stringResource(R.string.wifi_turning_on)
    var lock = false
    val wifiManager: WifiManager by systemService()

    val receiver = broadcastReceiver { context, intent ->
        setToggleData()
    }

    override fun getIntentForLaunch(): Intent = Intent(Settings.ACTION_WIFI_SETTINGS)

    override fun init() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        getActivity().registerReceiver(receiver, intentFilter)
        image.setImageResource(R.drawable.ic_wifi)
        setToggleData()
    }

    override fun getTitle(): CharSequence = getString(R.string.wifi)

    override fun onToggleButtonClicked() {
        // No need for animation since wifi connecting is damn fast...
        if (!lock) {
            lock = true
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false)
                text.setText(WifiTurnOff)
            } else {
                wifiManager.setWifiEnabled(true)
                text.setText(WifiTurnOn)
            }
        }
    }


    fun setToggleData() {
        lock = false
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            setToggleBackgroundOn()
            val networkName = getWifiNetworkName()
            if (networkName == null || networkName == "<unknown ssid>") {
                val results = wifiManager.getScanResults()
                var bestSignalAndPublic: ScanResult? = null
                for (result in results) {
                    if (result.capabilities == "[WPS][ESS]" || result.capabilities == "[ESS]" &&
                            (bestSignalAndPublic == null || WifiManager.compareSignalLevel(bestSignalAndPublic.level, result.level) < 0))
                        bestSignalAndPublic = result
                }
                if (bestSignalAndPublic == null) {
                    text.setText(WifiOn)
                } else
                    text.setText(WifiNetworkAvailable)
            } else {
                text.setText(WifiConnected.format(networkName))
            }
        } else if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            setToggleBackgroundOff()
            image.show()
            imageAnimation?.hide()
            text.setText(WifiOff)
        }
    }

    public fun getWifiNetworkName(): String? {
        if (wifiManager.isWifiEnabled()) {
            val wifiInfo = wifiManager.getConnectionInfo()
            if (wifiInfo != null) {
                val state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState())
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID()
                }
            }
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            getActivity().unregisterReceiver(receiver)
        } catch (ignored: Exception) {
            // Do nothing - receiver not registered
        }

    }
}