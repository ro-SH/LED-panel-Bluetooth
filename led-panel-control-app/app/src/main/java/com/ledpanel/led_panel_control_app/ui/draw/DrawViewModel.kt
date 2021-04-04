package com.ledpanel.led_panel_control_app.ui.draw

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawViewModel : ViewModel() {

    // Current Color Int
    private val _color = MutableLiveData<Int>()
    val color: LiveData<Int>
        get() = _color

    init {
        _color.value = Color.WHITE
    }

    /**
     *  Change string color for display
     *  @param newColor New color for String
     */
    fun setColor(newColor: Int) {
        _color.value = newColor
    }
}