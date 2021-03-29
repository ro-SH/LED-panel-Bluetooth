package com.ledpanel.led_panel_control_app.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.NumberFormatException
import java.time.LocalTime

class QueueViewModel : ViewModel() {

    // Current Queue Type
    private val _type = MutableLiveData<Int>()
    val type: LiveData<Int>
        get() = _type

//    private val queue: MutableList<QueueItem> = mutableListOf() // FROM DATABASE
    private val _queue: MutableList<QueueItem> = generateList().toMutableList()
    val queue: MutableList<QueueItem>
        get() = _queue

    // Sets QueueType
    fun setQueueType(newType: Int) {
        _type.value = newType
//        setFieldsVisibility()
    }

    fun addQueueItem(newText: String, newTime: String): Boolean {
        return if (checkTime(newTime)) {
            queue.add(
                    QueueItem(newText, newTime)
            )
            true
        }
        else
            false
    }
}

private fun checkTime(time: String): Boolean {

    println(time.filter { it == ':' }.count())
    if (time.filter { it == ':' }.count() != 1)
        return false

    val hrs = time.substringBefore(":")
    val mins = time.substringAfter(":")

    try {
        if (hrs.toInt() < 0 || hrs.toInt() > 23) {
            return false
        }
        if (mins.toInt() < 0 || mins.toInt() > 59) {
            return false
        }
    }
    catch (e: NumberFormatException) { return false }

    return true
}

private fun generateList(): List<QueueItem> {
    return listOf(
            QueueItem("Погулять", "10:00"),
            QueueItem("Какое-то действие", "11:00"),
            QueueItem("Ещё одно", "12:22"),
            QueueItem("Рандом", "13:00"),
            QueueItem("Что-то", "14:00"),
            QueueItem("Надоело", "15:00"),
            QueueItem("Придумывать", "16:00"),
            QueueItem("Уже", "17:00"),
            QueueItem("Что-то очень очень длинное для проверки вместимости", "18:00"),
            QueueItem("Ну и ещё", "19:00"),
    )
}