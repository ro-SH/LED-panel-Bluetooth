package com.ledpanel.led_panel_control_app.bluetooth.request

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ledpanel.led_panel_control_app.bluetooth.IBluetoothEventListener

class DiscoverRequest(private val context : Context, private val eventListener: IBluetoothEventListener) : IBluetoothRequest  {

    private var discoveredDevices:MutableList<BluetoothDevice> = mutableListOf()
    private var pairedDevices: MutableList<BluetoothDevice> = mutableListOf()
    private var bluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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

    fun discover() {
        discoveredDevices = mutableListOf()

        if (bluetoothAdapter.isDiscovering)
            bluetoothAdapter.cancelDiscovery()

        bluetoothAdapter.startDiscovery()
        eventListener.onDiscovering()
    }

    private fun registerReceiver() {
        context.registerReceiver(discoverReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        context.registerReceiver(discoverReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

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

    fun getPairedDevicesNames(): ArrayList<String> {
        pairedDevices = mutableListOf()
        val pairedDevicesNames: ArrayList<String> = ArrayList()
        bluetoothAdapter.bondedDevices.forEach { device ->
            pairedDevices.add(device)
            pairedDevicesNames.add("${device.name}\nMAC: ${device.address}")
        }
        return pairedDevicesNames
    }

    override fun cleanup() {
        context.unregisterReceiver(discoverReceiver)
    }
}