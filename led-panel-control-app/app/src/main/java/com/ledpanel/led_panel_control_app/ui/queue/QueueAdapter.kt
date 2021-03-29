package com.ledpanel.led_panel_control_app.ui.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ledpanel.led_panel_control_app.R

class QueueAdapter(private val queue: MutableList<QueueItem>) :
        RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.queue_item, parent, false)
        return QueueViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val queueItem = queue[position]
        holder.itemView.findViewById<ImageView>(R.id.queue_item__iv_delete_button).setOnClickListener {
            queue.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
        holder.bind(queueItem)
    }

    override fun getItemCount(): Int {
        return queue.size
    }

    class QueueViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val tvTime: TextView = itemView.findViewById(R.id.queue_item__tv_time)
        private val tvText: TextView = itemView.findViewById(R.id.queue_item__tv_text)

        fun bind(queueItem: QueueItem) {
            tvTime.text = queueItem.time
            tvText.text = queueItem.text
        }
    }
}