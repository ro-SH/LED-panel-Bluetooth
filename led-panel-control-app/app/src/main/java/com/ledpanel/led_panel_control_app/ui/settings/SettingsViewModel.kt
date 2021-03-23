package com.ledpanel.led_panel_control_app.ui.settings

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    lateinit var deviceList: ArrayList<BluetoothDevice>
//    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADRESS: String = "device_address"
    }

    init {
        initialize()
    }

    private fun initialize() {
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_bluetoothAdapter == null) {
            // Device does NOT support Bluetooth
//            Toast.makeText(this.applicationContext, "No Bluetooth", Toast.LENGTH_LONG).show()
            return
        }
        if (!m_bluetoothAdapter!!.isEnabled) {

            // enable bluetooth
//            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }
    }

    fun pairedDeviceList(): ArrayList<String> {
        val namesList: ArrayList<String> = ArrayList()
        val m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        deviceList = ArrayList()
        if (m_pairedDevices.isNotEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                deviceList.add(device)
                namesList.add("${device.name} ${device.address}")
            }
        }

        return namesList
    }

    fun connectTo(listId: Int) {
        val deviceAddress = deviceList[listId].address
    }
}