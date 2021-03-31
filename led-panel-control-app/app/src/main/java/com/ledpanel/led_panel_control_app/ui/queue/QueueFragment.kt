package com.ledpanel.led_panel_control_app.ui.queue

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.colorpicker.util.setVisibility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.databinding.FragmentQueueBinding
import com.ledpanel.led_panel_control_app.hideKeyboard

const val QUEUE = 0
const val TIMETABLE = 1

class QueueFragment : Fragment(), QueueAdapter.OnItemClickListener {

    // Data Binding
    private lateinit var binding: FragmentQueueBinding

    // ViewModel for QueueFragment
    private lateinit var queueViewModel: QueueViewModel

    // Adapter for Recycler View
    var adapter: QueueAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_queue, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Creating QueueViewModel object with QueueViewModelFactory
        queueViewModel = ViewModelProvider(this, QueueViewModelFactory())
                .get(QueueViewModel::class.java)

        // Data Binding
        binding.queueViewModel = queueViewModel

        // Live Data Binding
        binding.lifecycleOwner = this

        // Adapter for Recycler View
        adapter = QueueAdapter(queueViewModel.type.value!!, queueViewModel.queue, this)

        binding.fragmentQueueRvQueueList.adapter = adapter
        binding.fragmentQueueRvQueueList.layoutManager = LinearLayoutManager(context)

        binding.fragmentQueueClearButton.setOnClickListener {
            queueViewModel.clear()
            adapter?.notifyDataSetChanged()
        }

        binding.fragmentQueueSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                val newPosition = parent!!.selectedItemPosition
                if (newPosition != queueViewModel.type.value) {
                    setVisibility(newPosition)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        binding.fragmentQueueAddButton.setOnClickListener {
            hideKeyboard()
            val time = if (queueViewModel.type.value == QUEUE)
                binding.fragmentQueueTime.text.toString()
            else null
            val text = binding.fragmentQueueItem.text.toString()
            if (queueViewModel.addQueueItem(text, time)) {
                adapter?.notifyDataSetChanged()
                binding.fragmentQueueTime.setText("")
                binding.fragmentQueueItem.setText("")
            }
        }
    }

    /**
     *  Change Add Card and Recycler View layout based on type selected on Spinner
     *  @param position Selected type position
     */
    private fun setVisibility(position: Int) {
        queueViewModel.setQueueType(position)

        binding.fragmentQueueTime.setVisibility(position == QUEUE)
        binding.fragmentQueueTimeImage.setVisibility(position == QUEUE)

        adapter = QueueAdapter(queueViewModel.type.value!!, queueViewModel.queue, this@QueueFragment)
        binding.fragmentQueueRvQueueList.adapter = adapter
    }

    /**
     *  Show Edit dialog, change information of queue[position] and update layout
     *  @param position Recycler View position
     */
    override fun onItemClick(position: Int) {
        val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.edit_queue_dialog, null)

        dialogView.findViewById<EditText>(R.id.edit_queue_dialog__time)
                .setVisibility(queueViewModel.type.value == QUEUE)
        dialogView.findViewById<ImageView>(R.id.edit_queue_dialog__time_image)
                .setVisibility(queueViewModel.type.value == QUEUE)

        val builder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)

        val alertDialog = builder.show()

        val timeField = if (queueViewModel.type.value == QUEUE) dialogView.findViewById<EditText>(R.id.edit_queue_dialog__time) else null
        val itemField = dialogView.findViewById<EditText>(R.id.edit_queue_dialog__item)
        timeField?.setText(queueViewModel.queue[position].time)
        itemField.setText(queueViewModel.queue[position].text)

        dialogView.findViewById<Button>(R.id.edit_queue_dialog__edit_button).setOnClickListener {
            hideKeyboard()
            alertDialog.dismiss()
            if (queueViewModel.editQueueItem(position, itemField.text.toString(), if (queueViewModel.type.value == QUEUE) timeField?.text.toString() else null))
                adapter?.notifyItemChanged(position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        queueViewModel.save()
    }

    /**
     *  Delete item on selected position and update layout
     *  @param position Recycler View position
     */
    override fun onDeleteItemClick(position: Int) {
        queueViewModel.removeItemAt(position)
        adapter?.notifyItemRemoved(position)
    }
}