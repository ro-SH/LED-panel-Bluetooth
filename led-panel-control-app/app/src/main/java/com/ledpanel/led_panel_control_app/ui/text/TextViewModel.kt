package com.ledpanel.led_panel_control_app.ui.text

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TextViewModel() : ViewModel() {

    // Current Color Int
    private val _color = MutableLiveData<Int>()
    val color: LiveData<Int>
        get() = _color

    // Current String Type
    private val _type = MutableLiveData<Int>()
    val type: LiveData<Int>
        get() = _type

    // Current Text
    val text = MutableLiveData<String>()

    // Current Speed
    private val _speed = MutableLiveData<Double>()
    val speed: LiveData<Double>
        get() = _speed

    private val _textEditVisible = MutableLiveData<Boolean>()
    val textEditVisible: LiveData<Boolean>
        get() = _textEditVisible

    private val _speedSliderVisible = MutableLiveData<Boolean>()
    val speedSliderVisible: LiveData<Boolean>
        get() = _speedSliderVisible

    init {
        _color.value = Color.parseColor("white")
        _type.value = 0
        text.value = ""
        _speed.value = 1.0

        setFieldsVisibility()
    }

    // Sets Type for display
    fun setType(newType: Int) {
        _type.value = newType
        setFieldsVisibility()
    }

    private fun setFieldsVisibility() {
        _textEditVisible.value = when(_type.value) {
            2 -> false
            else -> true
        }

        _speedSliderVisible.value = when(_type.value) {
            1 -> true
            else -> false
        }
    }

    // Sets Color for display
    fun setColor(newColor: Int) {
        _color.value = newColor
    }

    // Sets Speed for display
    fun setSpeed(newSpeed: Float) {
        _speed.value = newSpeed.toDouble()
    }

    // Transfer Data to Panel
    fun onDisplayClick() {
        Log.i("TextViewModel", "type ${type.value}")

    }
}