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

    var width: Int = 0
    var height: Int = 0

    // Current draw mode
    private val _drawMode = MutableLiveData<Int>()
    val drawMode: LiveData<Int>
        get() = _drawMode

    init {
        setColor(Color.WHITE)
        setDrawMode(DRAW)
    }

    /**
     *  Change string color for display
     *  @param newColor New color for String
     */
    fun setColor(newColor: Int) {
        _color.value = newColor
    }

    /**
     *  Set new draw mode
     *  @param newMode
     */
    fun setDrawMode(newMode: Int) {
        _drawMode.value = newMode
    }
}
