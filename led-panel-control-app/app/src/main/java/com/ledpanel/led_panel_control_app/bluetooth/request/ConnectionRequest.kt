package com.ledpanel.led_panel_control_app.bluetooth.request

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.ledpanel.led_panel_control_app.bluetooth.IBluetoothEventListener
import java.io.IOException
import java.util.*

private const val BASE_UUID = "00001101-0000-1000-8000-00805F9B34FB"

/**
 *  Connect to device class
 */
class ConnectionRequest(private val context : Context, private val eventListener: IBluetoothEventListener) : IBluetoothRequest {
    private var connectionThread : ConnectionThread? = null

    /**
     *  Connect to device
     *  @param device Bluetooth Device
     */
    fun connect(device: BluetoothDevice) {
        eventListener.onConnecting()
        connectionThread = ConnectionThread(device)
        { isSuccess -> eventListener.onConnected(isSuccess)}
        connectionThread?.start()
    }

    /**
     *  Send data via Bluetooth
     *  @param data
     */
    fun sendCommand(data: String) {
        connectionThread!!.sendCommand(data)
    }

    /**
     *  Returns if device is connected
     */
    fun isConnected(): Boolean {
        return connectionThread != null
    }

    /**
     *  Disconnect device
     */
    fun stopConnect() {
        if (isConnected())
            connectionThread?.cancel()
    }

    /**
     *  Close the connection
     */
    override fun cleanup() {
        stopConnect()
    }


    /**
     *  Thread for bluetooth connection
     */
    private class ConnectionThread(private val device : BluetoothDevice,
                                   private val onComplete: (isSuccess : Boolean) -> Unit) : Thread() {

        private var bluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        private var bluetoothSocket : BluetoothSocket? = createSocket();

        /**
         *  Connect to device and return socket
         */
        private fun createSocket() : BluetoothSocket? {
            var socket : BluetoothSocket? = null;

            try {
                val uuid = if (device.uuids.isNotEmpty())
                    device.uuids[0].uuid
                else UUID.fromString(BASE_UUID);

                socket = device.createRfcommSocketToServiceRecord(uuid)
            }
            catch (e : IOException) {}

            return socket;
        }

        /**
         *  Send data via Bluetooth
         */
        fun sendCommand(data: String) {
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket!!.outputStream.write(data.toByteArray())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        override fun run() {
            super.run()

            bluetoothAdapter.cancelDiscovery()
            var isSuccess = false

            try {
                if (bluetoothSocket != null) {
                    bluetoothSocket?.connect()
                    isSuccess = true
                }

            }
            catch (e: Exception) { }

            onComplete(isSuccess)
        }

        /**
         *  Close connection
         */
        fun cancel() {
            if (bluetoothSocket != null)
                bluetoothSocket?.close()
        }
    }


}