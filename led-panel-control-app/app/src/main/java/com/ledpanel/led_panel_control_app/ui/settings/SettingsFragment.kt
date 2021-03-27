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
        fun sendDeviceId(deviceID: Int, isPaired: Boolean)
        fun disconnectDevice()
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
            val deviceNames = (activity as MainActivity).getDevicesNames(true)
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Paired Bluetooth Devices")
                    .setItems(deviceNames.toTypedArray()) { _, which ->
                        comm.sendDeviceId(which, true)
                        val deviceName = deviceNames[which].substringBeforeLast("MAC: ")
                        val deviceAddress = deviceNames[which].substringAfterLast("MAC: ")
                        settingsViewModel.setDeviceData(deviceName, deviceAddress)
                    }
                    .show()
        }

        binding.disconnectButton.setOnClickListener {
            comm.disconnectDevice()
            settingsViewModel.deleteDeviceData()
        }

        return binding.root
    }
}