package com.ledpanel.led_panel_control_app.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ledpanel.led_panel_control_app.checkTime
import com.ledpanel.led_panel_control_app.generateList

/**
 *  ViewModel for the QueueFragment
 */
class QueueViewModel : ViewModel() {

    // Current Queue Type
    private val _type = MutableLiveData<Int>()
    val type: LiveData<Int>
        get() = _type

    // Current Queue to display
//    private val queue: MutableList<QueueItem> = mutableListOf() // FROM DATABASE
    private val _queue: MutableList<QueueItem> = generateList().toMutableList()
    // TODO ADD DATABASE
    val queue: List<QueueItem>
        get() = _queue

    init {
        setQueueType(QUEUE)
    }

    /**
     *  Set new Queue type
     *  @param newType New type
     */
    fun setQueueType(newType: Int) {
        _type.value = newType
    }

    /**
     *  Add new QueueItem to the back of Queue List
     *  @param newText Text for the new QueueItem
     *  @param newTime Time for the new QueueItem or null
     */
    fun addQueueItem(newText: String, newTime: String?): Boolean {
        return when {
            newTime == null -> {
                _queue.add(
                    QueueItem(newText, "")
                )
                true
            }
            checkTime(newTime) -> {
                _queue.add(
                    QueueItem(newText, newTime)
                )
                true
            }
            else -> false
        }
    }

    /**
     *  Edit QueueItem on position
     *  @param position QueueItem ID
     *  @param newText Text for the QueueItem
     *  @param newTime Time for the QueueItem or null
     */
    fun editQueueItem(position: Int, newText: String, newTime: String?): Boolean {
        return when {
            newTime == null -> {
                _queue[position].text = newText
                true
            }
            checkTime(newTime) -> {
                _queue[position].text = newText
                _queue[position].time = newTime
                true
            }
            else -> false
        }
    }

    /**
     *  Remove QueueItem on position
     *  @param position QueueItem ID
     */
    fun removeItemAt(position: Int) {
        _queue.removeAt(position)
    }

    /**
     *  Delete all the QueueItems in Queue
     */
    fun clear() {
        _queue.clear()
    }

    /**
     *  Save Queue to the Database
     */
    fun save() {

        // TODO: SAVE TO DATABASE
    }
}
