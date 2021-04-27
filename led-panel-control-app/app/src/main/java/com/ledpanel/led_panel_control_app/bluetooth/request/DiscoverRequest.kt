package com.ledpanel.led_panel_control_app.bluetooth.request

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ledpanel.led_panel_control_app.bluetooth.IBluetoothEventListener

/**
 *  Class for discovering new devices
 */
class DiscoverRequest(private val context: Context, private val eventListener: IBluetoothEventListener) : IBluetoothRequest {

    // List of discovered devices
    private var discoveredDevices: MutableList<BluetoothDevice> = mutableListOf()

    // List of paired devices
    private var pairedDevices: MutableList<BluetoothDevice> = mutableListOf()

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val discoverReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (BluetoothDevice.ACTION_FOUND == intent.action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                addDiscoveredDevice(device!!)
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == intent.action) {
                eventListener.onDiscovered()
            }
        }
    }

    init {
        registerReceiver()
    }

    /**
     *  Start discovering devices
     */
    fun discover() {
        discoveredDevices = mutableListOf()

        if (bluetoothAdapter.isDiscovering)
            bluetoothAdapter.cancelDiscovery()

        bluetoothAdapter.startDiscovery()
        eventListener.onDiscovering()
    }

    private fun registerReceiver() {
        context.registerReceiver(discoverReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        context.registerReceiver(discoverReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
    }

    /**
     *  Add device to discovered devices
     *  @param new_device
     */
    private fun addDiscoveredDevice(new_device: BluetoothDevice) {
        if (new_device.bondState != BluetoothDevice.BOND_BONDED)
            return

        for (device in discoveredDevices) {
            if (device.address.equals(new_device.address))
                return
        }

        discoveredDevices.add(new_device)
    }

    fun getDevice(deviceId: Int): BluetoothDevice {
        return bluetoothAdapter.getRemoteDevice(pairedDevices[deviceId].address)
    }

    /**
     *  Get list of devices data
     *  @param 'true' if list of paired devices
     */
    fun getDevicesNames(isPaired: Boolean): ArrayList<String> {
        val devicesNames: ArrayList<String> = ArrayList()

        when (isPaired) {
            true -> {
                pairedDevices = mutableListOf()
                bluetoothAdapter.bondedDevices.forEach { device ->
                    pairedDevices.add(device)
                    devicesNames.add("${device.name}\nMAC: ${device.address}")
                }
//                devicesNames.add("DISCOVER")
            }

            else -> {
                discoveredDevices.forEach { device ->
                    devicesNames.add("${device.name}\nMAC: ${device.address}")
                }
            }
        }

        return devicesNames
    }

    override fun cleanup() {
        context.unregisterReceiver(discoverReceiver)
    }
}
