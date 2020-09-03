package com.example.nearbyapi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val intentFilter = IntentFilter()
    private lateinit var manager: WifiP2pManager
    private lateinit var wifiManager: WifiManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var mreceiver: WifiReceiver
    private val peer = mutableListOf<WifiP2pDevice>()
    private lateinit var deviceName: Array<String>
    private lateinit var device: Array<WifiP2pDevice>
    lateinit var peerListListener: WifiP2pManager.PeerListListener

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_wifi_on_off.setOnClickListener {

            when {
                wifiManager.isWifiEnabled -> {
                    btn_wifi_on_off.text = "Off"
                    wifiManager.isWifiEnabled = false
                }
                !wifiManager.isWifiEnabled -> {
                    wifiManager.isWifiEnabled = true
                    btn_wifi_on_off.text = "On"
                }
            }

        }

        btn_discover.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                    @SuppressLint("MissingPermission")
                    override fun onSuccess() {
                        status.text = "Discover Started"
                    }

                    override fun onFailure(reasonCode: Int) {
                        status.text = "Starting Fail"
                    }
                })
                return@setOnClickListener
            }


        }


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        channel = manager.initialize(
            this, mainLooper, null
        )

        mreceiver = WifiReceiver(manager, channel, this)



        peerListListener = WifiP2pManager.PeerListListener { peerList ->
            val refreshedPeers = peerList.deviceList
            if (refreshedPeers != peer) {
                peer.clear()
                peer.addAll(refreshedPeers)
                deviceName= Array(refreshedPeers.size){""}
                device= Array(refreshedPeers.size){ WifiP2pDevice() }
                for ((index, devices) in refreshedPeers.withIndex()) {
                    deviceName[index] = devices.deviceName
                    device[index] = devices
                }
                val adapter = ArrayAdapter<String>(
                    applicationContext,
                    R.layout.list_item,
                    deviceName
                )
                wifi_view.adapter=adapter
            }
            if (peer.isEmpty()) {
                Log.e("error", "No Device Found")
                return@PeerListListener
            }


        }


    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mreceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mreceiver)
    }

}


