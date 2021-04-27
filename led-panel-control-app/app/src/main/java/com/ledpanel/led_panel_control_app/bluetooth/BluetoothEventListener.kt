package com.ledpanel.led_panel_control_app.bluetooth

class BluetoothEventListener : IBluetoothEventListener {

    override fun onDisconnecting() {}
    override fun onDisconnected() {}
    override fun onConnected(isSuccess: Boolean) {}
    override fun onPairing() {}
    override fun onConnecting() {}
    override fun onDiscovering() {}
    override fun onDiscovered() {}
    override fun onPaired() {}
    override fun onEnable() {}
}
