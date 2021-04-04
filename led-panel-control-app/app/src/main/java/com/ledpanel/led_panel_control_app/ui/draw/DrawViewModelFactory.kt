package com.ledpanel.led_panel_control_app.ui.draw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DrawViewModelFactory :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrawViewModel::class.java)) {
            return DrawViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}