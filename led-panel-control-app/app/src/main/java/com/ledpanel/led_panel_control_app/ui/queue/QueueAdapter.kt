package com.ledpanel.led_panel_control_app.ui.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ledpanel.led_panel_control_app.R

class QueueAdapter(
    private val type: Int,
    private val queue: List<QueueItem>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = if (type == TIMETABLE)
            LayoutInflater.from(parent.context).inflate(R.layout.item_timetable, parent, false)
        else
            LayoutInflater.from(parent.context).inflate(R.layout.item_queue, parent, false)
        return QueueViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val queueItem = queue[position]
        holder.bind(queueItem)
    }

    override fun getItemCount(): Int {
        return queue.size
    }

    inner class QueueViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val tvTime: TextView? = if (type == TIMETABLE) itemView.findViewById(R.id.item_queue__tv_time) else null
        private val tvText: TextView = itemView.findViewById(R.id.item_queue__tv_text)
        private val ibDeleteButton: ImageView = itemView.findViewById(R.id.item_queue__ib_delete)

        init {
            itemView.setOnClickListener(this)
            ibDeleteButton.setOnClickListener(this)
        }

        fun bind(queueItem: QueueItem) {
            tvTime?.text = queueItem.time
            tvText.text = queueItem.text
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                when (v?.id) {
                    ibDeleteButton.id -> listener.onDeleteItemClick(position)
                    else -> listener.onItemClick(position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteItemClick(position: Int)
    }
}
