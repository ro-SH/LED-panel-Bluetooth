package com.ledpanel.led_panel_control_app.bluetooth.request

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ledpanel.led_panel_control_app.bluetooth.IBluetoothEventListener

/**
 *  Class for enabling Bluetooth
 */
class EnableRequest(private val context : Context, private val eventListener: IBluetoothEventListener): IBluetoothRequest {

    private var requestEnable = false
    private lateinit var bluetoothAdapter : BluetoothAdapter

    init {
        registerReceiver()
    }

    private val enableReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (!requestEnable && BluetoothAdapter.ACTION_STATE_CHANGED != action)
                return

            requestEnable = false
            eventListener.onEnable()
        }
    }

    private fun registerReceiver() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        context.registerReceiver(enableReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    /**
     *  Enable Bluetooth
     */
    fun enableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            requestEnable = true
            bluetoothAdapter.enable()
        }
        else
            eventListener.onEnable()
    }

    /**
     *  Disable Bluetooth
     */
    fun disableBluetooth() {
        bluetoothAdapter.disable()
    }

    override fun cleanup() {
        context.unregisterReceiver(enableReceiver)
    }
}