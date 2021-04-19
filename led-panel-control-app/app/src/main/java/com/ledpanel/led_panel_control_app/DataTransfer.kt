package com.ledpanel.led_panel_control_app

import com.ledpanel.led_panel_control_app.ui.queue.QueueItem

/**
 *  Interface for transferring data Via Bluetooth
 */
interface DataTransfer {

    /**
     *  Transfer disconnect device signal
     */
    fun disconnectDevice()

    /**
     *  Fill the panel with color
     */
    fun fill(red: Int = 0, green: Int = 0, blue: Int = 0)

    /**
     *  Transfer Device Id to MainActivity
     *  @param deviceID
     *  @param isPaired 'true' if device is paired
     */
    fun sendDeviceId(deviceID: Int, isPaired: Boolean)

    /**
     *  Set new device data.
     *  @param name
     *  @param address
     */
    fun setDeviceData(name: String, address: String)

    /**
     *  Set new LED panel configuration
     *  @param width
     *  @param height
     *  @param brightness
     */
    fun setConfiguration(width: Int, height: Int, brightness: Int)

    /**
     *  Send Data bia Bluetooth
     *  @param data as String
     */
    fun sendData(data: String)

    /**
     *  Start showing queue on panel
     */
    fun showQueue(queue: List<QueueItem>, showTime: Boolean, red: Int, green: Int, blue: Int)

    /**
     *  Start showing current time on panel
     */
    fun showTime(red: Int, green: Int, blue: Int)

    /**
     *  Returns 'true' if connected to Bluetooth device
     */
    fun isConnected(): Boolean
}
