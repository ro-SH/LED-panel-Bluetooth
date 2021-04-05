package com.ledpanel.led_panel_control_app.bluetooth

interface IBluetoothEventListener {
    fun onEnable()
    fun onDiscovering()
    fun onDiscovered()
    fun onConnecting()
    fun onConnected(isSuccess: Boolean)
    fun onPairing()
    fun onPaired()
    fun onDisconnecting()
    fun onDisconnected()
}