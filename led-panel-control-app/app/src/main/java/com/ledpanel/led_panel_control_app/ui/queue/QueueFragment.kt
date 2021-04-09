package com.ledpanel.led_panel_control_app.ui.queue

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.colorpicker.util.setVisibility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.aboutQueue
import com.ledpanel.led_panel_control_app.databinding.FragmentQueueBinding
import com.ledpanel.led_panel_control_app.hideKeyboard
import com.ledpanel.led_panel_control_app.ui.about.AboutFragment

const val QUEUE = 0
const val TIMETABLE = 1

class QueueFragment : Fragment(), QueueAdapter.OnItemClickListener {

    // Data Binding
    private lateinit var binding: FragmentQueueBinding

    // ViewModel for QueueFragment
    private lateinit var queueViewModel: QueueViewModel

    // Adapter for Recycler View
    private var adapter: QueueAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_queue, container, false)

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
                val fragment = AboutFragment.create("Queue", aboutQueue)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, "AboutQueue")
                    .addToBackStack(null)
                    .commit()
            }

            R.id.add -> showAddDialog()
        }

        return super.onOptionsItemSelected(item)
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

        // Clear Button
        binding.fragmentQueueClearButton.setOnClickListener { onClearButtonClicked() }

        // Type Spinner
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

        // Skip Button
        binding.fragmentQueueSkipButton.setOnClickListener { onSkipButtonClicked() }

        // Display Button
        binding.fragmentQueueDisplayButton.setOnClickListener {
            hideKeyboard()
            // TODO: CHECK AND SEND DATA
        }
    }

    /**
     *  Called when the skip button is pressed.
     *  Delete the item on position 0.
     */
    private fun onSkipButtonClicked() {
        if (queueViewModel.queue.isNotEmpty()) {
            queueViewModel.removeItemAt(0)
            adapter?.notifyItemRemoved(0)
            binding.fragmentQueueRvQueueList.scrollToPosition(0)

            // TODO: Show item on pos 0
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
                .inflate(R.layout.add_queue_dialog, null)

        dialogView.findViewById<EditText>(R.id.add_queue_dialog__time)
                .setVisibility(queueViewModel.type.value == QUEUE)
        dialogView.findViewById<ImageView>(R.id.add_queue_dialog__time_image)
                .setVisibility(queueViewModel.type.value == QUEUE)

        val builder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)

        val alertDialog = builder.show()

        val timeField = if (queueViewModel.type.value == QUEUE) dialogView.findViewById<EditText>(R.id.add_queue_dialog__time) else null
        val itemField = dialogView.findViewById<EditText>(R.id.add_queue_dialog__item)

        dialogView.findViewById<Button>(R.id.add_queue_dialog__edit_button).setOnClickListener {
            hideKeyboard()
            alertDialog.dismiss()
            if (queueViewModel.addQueueItem(itemField.text.toString(), if (queueViewModel.type.value == QUEUE) timeField?.text.toString() else null)) {
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