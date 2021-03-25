package com.ledpanel.led_panel_control_app.bluetooth.request

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ledpanel.led_panel_control_app.bluetooth.IBluetoothEventListener

class PairRequest(private val context : Context, private val eventListener: IBluetoothEventListener) : IBluetoothRequest {

    private var isPairing = false
    private lateinit var currentBluetoothDevice : BluetoothDevice

    private val pairReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val state = intent?.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
            val prevState = intent?.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)

            if (prevState == BluetoothDevice.BOND_BONDING && state == BluetoothDevice.BOND_BONDED) {
                isPairing = false
                eventListener.onPaired()
            }

        }
    }

    init {
        registerReceiver()
    }

    fun pair(device : BluetoothDevice) {
        if (isPairing)
            return

        isPairing = true
        currentBluetoothDevice = device
        val method = device.javaClass.getMethod("createBond", *(null as Array<Class<Any>>))
        method.invoke(device, *(null as Array<Any>))
        eventListener.onPairing()
    }

    private fun registerReceiver() {
        context.registerReceiver(pairReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
    }

    override fun cleanup() {
        context.unregisterReceiver(pairReceiver)
    }

}