package com.example.nearbyapi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.core.app.ActivityCompat

class WifiReceiver(
    _manager: WifiP2pManager,
    _channel: WifiP2pManager.Channel,
    _activity: MainActivity
) : BroadcastReceiver() {
    private var manager = _manager
    private val channel = _channel
    private val activity = _activity


    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                when (state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        Log.e("wifi","off")
                    }
                    WifiP2pManager.WIFI_P2P_STATE_DISABLED -> {
                        Log.e("wifi","on")
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                if (ActivityCompat.checkSelfPermission(
                        activity.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    manager.discoverPeers(channel,object :WifiP2pManager.ActionListener{
                        override fun onSuccess() {
                            Log.e("peer","success")
                        }

                        override fun onFailure(p0: Int) {
                            Log.e("peer","fail")
                        }

                    })
                    manager.requestPeers(channel,activity.peerListListener)

                    return
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {

            }
        }
    }
}
