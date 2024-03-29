package com.ledpanel.led_panel_control_app.ui.queue

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.util.setVisibility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.DataTransfer
import com.ledpanel.led_panel_control_app.MainActivity
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.databinding.FragmentQueueBinding
import com.ledpanel.led_panel_control_app.getCurrentTime
import com.ledpanel.led_panel_control_app.hideKeyboard
import com.ledpanel.led_panel_control_app.isLater
import com.ledpanel.led_panel_control_app.setBackgroundColor
import com.ledpanel.led_panel_control_app.ui.about.AboutFragment

const val QUEUE = 0
const val TIMETABLE = 1

class QueueFragment : Fragment(), QueueAdapter.OnItemClickListener {

    // Data Binding
    private lateinit var binding: FragmentQueueBinding

    // ViewModel for QueueFragment
    private lateinit var queueViewModel: QueueViewModel

    // DataTransfer interface
    private lateinit var comm: DataTransfer

    // Adapter for Recycler View
    private var adapter: QueueAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_queue, container, false
        )

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.queue_actionbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                val fragment = AboutFragment.create(getString(R.string.about_queue_title), getString(R.string.about_queue_description))
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_main__nav_host_fragment, fragment, "AboutQueue")
                    .addToBackStack(null)
                    .commit()
            }

            R.id.add -> showAddDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set fragment title
        (requireActivity() as MainActivity).updateActionBarTitle(getString(R.string.queue_title))

        comm = requireActivity() as DataTransfer

        // Creating QueueViewModel object with QueueViewModelFactory
        queueViewModel = ViewModelProvider(this, QueueViewModelFactory())
            .get(QueueViewModel::class.java)

        // Data Binding
        binding.queueViewModel = queueViewModel

        // Live Data Binding
        binding.lifecycleOwner = this

        // Adapter for Recycler View
        adapter = QueueAdapter(queueViewModel.type.value!!, queueViewModel.queue, this)

        binding.fragmentQueueRvQueue.adapter = adapter
        binding.fragmentQueueRvQueue.layoutManager = LinearLayoutManager(context)

        // Clear Button
        binding.fragmentQueueBtnClear.setOnClickListener { onClearButtonClicked() }

        // Type Spinner
        binding.fragmentQueueSpinnerType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newPosition = parent!!.selectedItemPosition
                if (newPosition != queueViewModel.type.value) {
                    setVisibility(newPosition)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        // Skip Button
        queueViewModel.type.observe(
            viewLifecycleOwner,
            { type ->
                binding.fragmentQueueBtnSkip.setVisibility(type == QUEUE)
            }
        )

        // Color Button
        binding.fragmentQueueBtnColor.setOnClickListener {

            // Open the Color Picker Dialog
            ColorPickerDialog
                .Builder(requireActivity()) // Pass Activity Instance
                .setColorShape(ColorShape.SQAURE) // Or ColorShape.CIRCLE
                .setDefaultColor(queueViewModel.color.value!!) // Pass Default Color
                .setColorListener { color, _ ->
                    queueViewModel.setColor(color)
                }
                .show()
        }

        queueViewModel.color.observe(
            viewLifecycleOwner,
            { newColor ->
                setBackgroundColor(binding.fragmentQueueBtnColor, newColor)
            }
        )

        // Skip button
        binding.fragmentQueueBtnSkip.setOnClickListener { onSkipButtonClicked() }

        // Display Button
        binding.fragmentQueueBtnDisplay.setOnClickListener { onDisplayClicked() }
    }

    /**
     *  Called when display button clicked.
     *  Transfer data to the panel.
     */
    private fun onDisplayClicked() {
        hideKeyboard()
        sendData()
    }

    /**
     *  Transfer data to the panel
     */
    private fun sendData() {
        if (!comm.isConnected())
            Toast.makeText(requireContext(), getString(R.string.device_not_connected), Toast.LENGTH_SHORT).show()
        else if (!checkValid())
            Toast.makeText(requireContext(), "Incorrect time sequence!", Toast.LENGTH_SHORT).show()
        else
            comm.showQueue(
                queueViewModel.queue,
                queueViewModel.type.value == TIMETABLE,
                Color.red(queueViewModel.color.value!!), Color.green(queueViewModel.color.value!!), Color.blue(queueViewModel.color.value!!)
            )
    }

    /**
     *  Check time sequence
     *  @return 'true' if valid time sequence
     */
    private fun checkValid(): Boolean {
        if (queueViewModel.type.value == TIMETABLE) {
            var prevTime = getCurrentTime()
            for (item in queueViewModel.queue) {
                if (!isLater(item.time, prevTime))
                    return false
                prevTime = item.time
            }
        }
        return true
    }

    /**
     *  Called when the skip button is pressed.
     *  Delete the item on position 0.
     */
    private fun onSkipButtonClicked() {
        if ((requireActivity() as MainActivity).isShowingQueue() &&
            queueViewModel.type.value == QUEUE &&
            queueViewModel.queue.isNotEmpty()
        ) {
            queueViewModel.removeItemAt(0)
            adapter?.notifyItemRemoved(0)
            binding.fragmentQueueRvQueue.scrollToPosition(0)

            sendData()
        }
    }

    /**
     *  Called when the clear button is pressed.
     *  Clear the queue and the recycler view.
     */
    private fun onClearButtonClicked() {
        queueViewModel.clear()
        adapter?.notifyDataSetChanged()
    }

    /**
     *  Open Add Dialog and add a new item if valid
     */
    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_queue_add, null)

        dialogView.findViewById<EditText>(R.id.dialog_queue_add__et_time)
            .setVisibility(queueViewModel.type.value == TIMETABLE)
        dialogView.findViewById<ImageView>(R.id.dialog_queue_add__iv_time)
            .setVisibility(queueViewModel.type.value == TIMETABLE)

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)

        val alertDialog = builder.show()

        val timeField = if (queueViewModel.type.value == TIMETABLE) dialogView.findViewById<EditText>(R.id.dialog_queue_add__et_time) else null
        val itemField = dialogView.findViewById<EditText>(R.id.dialog_queue_add__et_item)

        dialogView.findViewById<Button>(R.id.dialog_queue_add__btn_edit).setOnClickListener {
            hideKeyboard()
            alertDialog.dismiss()
            if (queueViewModel.addQueueItem(itemField.text.toString(), if (queueViewModel.type.value == TIMETABLE) timeField?.text.toString() else null)) {
                adapter?.notifyDataSetChanged()
            }
        }
    }

    /**
     *  Change Add Card and Recycler View layout based on type selected on Spinner
     *  @param position Selected type position
     */
    private fun setVisibility(position: Int) {
        queueViewModel.setQueueType(position)

        adapter = QueueAdapter(queueViewModel.type.value!!, queueViewModel.queue, this@QueueFragment)
        binding.fragmentQueueRvQueue.adapter = adapter
    }

    /**
     *  Show Edit dialog, change information of queue[position] and update layout
     *  @param position Recycler View position
     */
    override fun onItemClick(position: Int) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_queue_edit, null)

        dialogView.findViewById<EditText>(R.id.dialog_queue_edit__et_time)
            .setVisibility(queueViewModel.type.value == TIMETABLE)
        dialogView.findViewById<ImageView>(R.id.dialog_queue_edit__iv_time)
            .setVisibility(queueViewModel.type.value == TIMETABLE)

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)

        val alertDialog = builder.show()

        val timeField = if (queueViewModel.type.value == TIMETABLE) dialogView.findViewById<EditText>(R.id.dialog_queue_edit__et_time) else null
        val itemField = dialogView.findViewById<EditText>(R.id.edit_queue_dialog__et_item)
        timeField?.setText(queueViewModel.queue[position].time)
        itemField.setText(queueViewModel.queue[position].text)

        dialogView.findViewById<Button>(R.id.dialog_queue_edit__btn_edit).setOnClickListener {
            hideKeyboard()
            alertDialog.dismiss()
            if (queueViewModel.editQueueItem(position, itemField.text.toString(), if (queueViewModel.type.value == TIMETABLE) timeField?.text.toString() else null))
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

        if ((requireActivity() as MainActivity).isShowingQueue() &&
            queueViewModel.queue.isNotEmpty() &&
            queueViewModel.type.value == QUEUE
        ) {
            sendData()
        }
    }
}
