package com.ledpanel.led_panel_control_app.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class QueueViewModelFactory :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QueueViewModel::class.java)) {
            return QueueViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}