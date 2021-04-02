package com.ledpanel.led_panel_control_app.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.ledpanel.led_panel_control_app.bluetooth.request.ConnectionRequest
import com.ledpanel.led_panel_control_app.bluetooth.request.DiscoverRequest
import com.ledpanel.led_panel_control_app.bluetooth.request.EnableRequest
import com.ledpanel.led_panel_control_app.bluetooth.request.PairRequest

/**
 *  Bluetooth Connection class
 */
class BluetoothConnection(val context: Context) {

    // Requests
    private var eventListener: IBluetoothEventListener = BluetoothEventListener()
    private val enableRequest = EnableRequest(context, eventListener)
    private val discoverRequest = DiscoverRequest(context, eventListener)
    private val pairRequest = PairRequest(context, eventListener)
    private val connectionRequest = ConnectionRequest(context, eventListener)

    fun setBluetoothEventListener(listener: IBluetoothEventListener) {
        eventListener = listener
    }

    /**
     *  Enable Bluetooth Adapter on Android device
     */
    fun enableBluetoothAdapter() {
        enableRequest.enableBluetooth()
    }

    /**
     *  Disable Bluetooth Adapter on Android device
     */
    fun disableBluetoothAdapter() {
        enableRequest.disableBluetooth()
    }

    /**
     *  Discover Bluetooth device nearby
     */
    fun discoverDevices() {
        discoverRequest.discover()
    }

    /**
     *  Pair to Bluetooth device
     *  @param deviceId Device ID in device list
     */
    fun pairDevice(deviceId : Int) {
        val device = discoverRequest.getDevice(deviceId)
        pairRequest.pair(device)
    }

    /**
     *  Connect to Bluetooth device
     *  @param deviceId Device ID in device list
     */
    fun connectDevice(deviceId: Int) {
        val device = discoverRequest.getDevice(deviceId)
        connectionRequest.connect(device)
    }

    /**
     *  Disconnect Bluetooth device
     */
    fun stopConnectDevice() {
        connectionRequest.stopConnect()
    }

    /**
     *  Get device names from paired or discovered device list
     *  @param isPaired 'true' -> paired
     *  @return ArrayList of device names + addresses
     */
    fun getDevicesNames(isPaired: Boolean): ArrayList<String> {
        return discoverRequest.getDevicesNames(isPaired)
    }

    /**
     *  Transfer data via Bluetooth
     *  @param data
     */
    fun sendCommand(data: String) {
        connectionRequest.sendCommand(data)
    }

    /**
     *  Returns 'true' if device is connected
     */
    fun isConnected(): Boolean {
        return connectionRequest.isConnected()
    }

    /**
     *  Clean up Bluetooth Connection
     */
    fun cleanUp() {
        enableRequest.cleanup()
        discoverRequest.cleanup()
        pairRequest.cleanup()
    }
}