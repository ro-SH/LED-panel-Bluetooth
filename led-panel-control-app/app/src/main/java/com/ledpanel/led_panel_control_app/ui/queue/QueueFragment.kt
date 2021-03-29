package com.ledpanel.led_panel_control_app.ui.queue

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.databinding.FragmentQueueBinding
import com.ledpanel.led_panel_control_app.ui.text.TextViewModel
import com.ledpanel.led_panel_control_app.ui.text.TextViewModelFactory

class QueueFragment : Fragment() {

    private lateinit var viewModel: QueueViewModel

    var adapter: QueueAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentQueueBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_queue, container, false)

        // Creating QueueViewModel object with QueueViewModelFactory
        val queueViewModel = ViewModelProvider(this, QueueViewModelFactory())
                .get(QueueViewModel::class.java)

        // Data Binding
        binding.queueViewModel = queueViewModel

        // Live Data Binding
        binding.lifecycleOwner = this

        adapter = QueueAdapter(queueViewModel.queue)
        // TODO: INCAPSULATION

        binding.fragmentQueueRvQueueList.adapter = adapter
        binding.fragmentQueueRvQueueList.layoutManager = LinearLayoutManager(context)

        binding.fragmentQueueAddButton.setOnClickListener {
            val time = binding.fragmentQueueTime.text.toString()
            val text = binding.fragmentQueueItem.text.toString()
            if (queueViewModel.addQueueItem(text, time))
                adapter?.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(QueueViewModel::class.java)
        // TODO: Use the ViewModel
    }

}