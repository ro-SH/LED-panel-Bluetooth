package com.ledpanel.led_panel_control_app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *  ViewModel for SettingsFragment
 */
class SettingsViewModel : ViewModel() {

    // Bluetooth device name
    private val _deviceName = MutableLiveData<String>()
    val deviceName: LiveData<String>
        get() = _deviceName

    // Bluetooth device address
    private val _deviceAddress = MutableLiveData<String>()
    val deviceAddress: LiveData<String>
        get() = _deviceAddress

    init {
        deleteDeviceData()
    }

    /**
     *  Change Device Data
     *  @param name New device name
     *  @param address New device address
     */
    fun setDeviceData(name: String, address: String) {
        _deviceName.value = "Device Name: $name"
        _deviceAddress.value = "MAC: $address"
    }

    /**
     *  Delete Device Data
     */
    fun deleteDeviceData() {
        _deviceName.value = "No Connected Device"
        _deviceAddress.value = ""
    }
}