package com.ledpanel.led_panel_control_app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _deviceName = MutableLiveData<String>()
    val deviceName: LiveData<String>
        get() = _deviceName

    private val _deviceAddress = MutableLiveData<String>()
    val deviceAddress: LiveData<String>
        get() = _deviceAddress

    init {
        deleteDeviceData()
    }

    fun setDeviceData(name: String, address: String) {
        _deviceName.value = "Device Name: $name"
        _deviceAddress.value = "MAC: $address"
    }

    fun deleteDeviceData() {
        _deviceName.value = "No Connected Device"
        _deviceAddress.value = ""
    }
}