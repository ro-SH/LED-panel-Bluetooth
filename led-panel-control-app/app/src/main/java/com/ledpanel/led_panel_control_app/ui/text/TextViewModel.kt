package com.ledpanel.led_panel_control_app.ui.text

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *  View Model for TextFragment
 */
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

    // Text Visibility
    private val _textEditVisible = MutableLiveData<Boolean>()
    val textEditVisible: LiveData<Boolean>
        get() = _textEditVisible

    // Speed Slider Visibility
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

    /**
     *  Change String type
     *  @param newType Type ID
     */
    fun setType(newType: Int) {
        _type.value = newType
        setFieldsVisibility()
    }

    /**
     *  Change fields visibility based on current type
     */
    private fun setFieldsVisibility() {
        _textEditVisible.value = when (_type.value) {
            2 -> false
            else -> true
        }

        _speedSliderVisible.value = when (_type.value) {
            1 -> true
            else -> false
        }
    }

    /**
     *  Change string color for display
     *  @param newColor New color for String
     */
    fun setColor(newColor: Int) {
        _color.value = newColor
    }

    /**
     *  Change display speed
     *  @param newSpeed New String speed
     */
    fun setSpeed(newSpeed: Float) {
        _speed.value = newSpeed.toDouble()
    }

    // Transfer Data to Panel
    fun onDisplayClick() {
        Log.i("TextViewModel", "type ${type.value}")
    }
}
