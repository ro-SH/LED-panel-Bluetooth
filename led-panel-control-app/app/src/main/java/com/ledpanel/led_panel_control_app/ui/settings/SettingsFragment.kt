package com.ledpanel.led_panel_control_app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.MainActivity
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    interface Communicator {
        fun sendDeviceId(deviceID: Int)
    }

    private lateinit var comm: Communicator

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        comm = requireActivity() as Communicator

        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_settings, container, false)

        // Creating SettingsViewModel object with TextViewModelFactory
        val settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory())
                .get(SettingsViewModel::class.java)

        binding.settingsViewModel = settingsViewModel

        binding.lifecycleOwner = this

        binding.connectButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Paired Bluetooth Devices")
//                    .setItems(settingsViewModel.pairedDeviceList().toTypedArray()) { _, which ->
//                        settingsViewModel.connectTo(which)
//                    }
                    .setItems((activity as MainActivity).getPairedDevicesNames().toTypedArray()) { _, which ->
                        comm.sendDeviceId(which)
                    }
                    .show()
        }

        return binding.root
    }
}