package com.ledpanel.led_panel_control_app

import android.graphics.Color
import android.widget.Button
import com.github.dhaval2404.colorpicker.util.ColorUtil
import com.ledpanel.led_panel_control_app.ui.queue.QueueItem
import java.lang.NumberFormatException

/**
 *  @param time
 *  @return 'true' if string formats time
 */
fun checkTime(time: String): Boolean {

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

/**
 *  Generate Test List of QueueItems
 *  @return List of QueueItems
 */
fun generateList(): List<QueueItem> {
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

/**
 *  Change background color
 *  @param button Button to change bg color
 *  @param newColor New color for the button
 */
fun setBackgroundColor(button: Button, newColor: Int) {
    if (ColorUtil.isDarkColor(newColor)) {
        button.setTextColor(Color.WHITE)
    } else {
        button.setTextColor(Color.BLACK)
    }
    button.setBackgroundColor(newColor)
}