package com.ledpanel.led_panel_control_app.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.ledpanel.led_panel_control_app.bluetooth.request.ConnectionRequest
import com.ledpanel.led_panel_control_app.bluetooth.request.DiscoverRequest
import com.ledpanel.led_panel_control_app.bluetooth.request.EnableRequest
import com.ledpanel.led_panel_control_app.bluetooth.request.PairRequest

class BluetoothConnection(val context: Context) {

    private var eventListener: IBluetoothEventListener = BluetoothEventListener()
    private val enableRequest = EnableRequest(context, eventListener)
    private val discoverRequest = DiscoverRequest(context, eventListener)
    private val pairRequest = PairRequest(context, eventListener)
    private val connectionRequest = ConnectionRequest(context, eventListener)

    fun setBluetoothEventListener(listener: IBluetoothEventListener) {
        eventListener = listener
    }

    fun enableBluetoothAdapter() {
        enableRequest.enableBluetooth()
    }

    fun disableBluetoothAdapter() {
        enableRequest.disableBluetooth()
    }

    fun discoverDevices() {
        discoverRequest.discover()
    }

    fun pairDevice(deviceId : Int) {
        val device = discoverRequest.getDevice(deviceId)
        pairRequest.pair(device)
    }

    fun connectDevice(deviceId: Int) {
        val device = discoverRequest.getDevice(deviceId)
        connectionRequest.connect(device)
    }

    fun stopConnectDevice() {
        connectionRequest.stopConnect()
    }

    fun getDevicesNames(isPaired: Boolean): ArrayList<String> {
        return discoverRequest.getDevicesNames(isPaired)
    }

    fun sendCommand(data: String) {
        connectionRequest.sendCommand(data)
    }

    fun isConnected(): Boolean {
        return connectionRequest.isConnected()
    }

    fun cleanUp() {
        enableRequest.cleanup()
        discoverRequest.cleanup()
        pairRequest.cleanup()
    }

//    private var m_bluetoothAdapter: BluetoothAdapter? = null
//    lateinit var deviceList: ArrayList<BluetoothDevice>
//    //    private lateinit var m_pairedDevices: Set<BluetoothDevice>
//    val REQUEST_ENABLE_BLUETOOTH = 1
//
//    companion object {
//        val EXTRA_ADRESS: String = "device_address"
//    }
//
//    init {
//        initialize()
//    }
//
//    private fun initialize() {
//        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        if (m_bluetoothAdapter == null) {
//            // Device does NOT support Bluetooth
////            Toast.makeText(this.applicationContext, "No Bluetooth", Toast.LENGTH_LONG).show()
//            return
//        }
//        if (!m_bluetoothAdapter!!.isEnabled) {
//
//            // enable bluetooth
////            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
////            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
//        }
//    }
//
//    fun pairedDeviceList(): ArrayList<String> {
//        val namesList: ArrayList<String> = ArrayList()
//        val m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
//        deviceList = ArrayList()
//        if (m_pairedDevices.isNotEmpty()) {
//            for (device: BluetoothDevice in m_pairedDevices) {
//                deviceList.add(device)
//                namesList.add("${device.name} ${device.address}")
//            }
//        }
//
//        return namesList
//    }
//
//    fun connectTo(listId: Int) {
//        val deviceAddress = deviceList[listId].address
//    }
}