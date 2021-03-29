package com.ledpanel.led_panel_control_app.ui.queue

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.databinding.FragmentQueueBinding
import com.ledpanel.led_panel_control_app.ui.text.TextViewModel
import com.ledpanel.led_panel_control_app.ui.text.TextViewModelFactory

class QueueFragment : Fragment(), QueueAdapter.OnItemClickListener {

    private lateinit var queueViewModel: QueueViewModel

    var adapter: QueueAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentQueueBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_queue, container, false)

        // Creating QueueViewModel object with QueueViewModelFactory
        queueViewModel = ViewModelProvider(this, QueueViewModelFactory())
                .get(QueueViewModel::class.java)

        // Data Binding
        binding.queueViewModel = queueViewModel

        // Live Data Binding
        binding.lifecycleOwner = this

        adapter = QueueAdapter(queueViewModel.queue, this)

        binding.fragmentQueueRvQueueList.adapter = adapter
        binding.fragmentQueueRvQueueList.layoutManager = LinearLayoutManager(context)

        binding.fragmentQueueSaveButton.setOnClickListener {
            queueViewModel.save()
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
        }

        binding.fragmentQueueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newPosition = parent!!.selectedItemPosition
                if (newPosition != queueViewModel.type.value) {
                    queueViewModel.setQueueType(newPosition)
                    // TODO: Change Layout
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        binding.fragmentQueueAddButton.setOnClickListener {
            val time = binding.fragmentQueueTime.text.toString()
            val text = binding.fragmentQueueItem.text.toString()
            if (queueViewModel.addQueueItem(text, time))
                adapter?.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onItemClick(position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_queue_dialog, null)

        val builder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)

        val alertDialog = builder.show()

        val timeField = dialogView.findViewById<EditText>(R.id.edit_queue_dialog__time)
        val itemField = dialogView.findViewById<EditText>(R.id.edit_queue_dialog__item)
        timeField.setText(queueViewModel.queue[position].time)
        itemField.setText(queueViewModel.queue[position].text)

        dialogView.findViewById<Button>(R.id.edit_queue_dialog__edit_button).setOnClickListener {
            alertDialog.dismiss()
            if (queueViewModel.editQueueItem(position, itemField.text.toString(), timeField.text.toString()))
                adapter?.notifyItemChanged(position)
        }
    }

    override fun onDeleteItemClick(position: Int) {
        queueViewModel.removeItemAt(position)
        adapter?.notifyItemRemoved(position)
        adapter?.notifyItemRangeRemoved(position, queueViewModel.queue.size)
    }
}