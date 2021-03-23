package com.ledpanel.led_panel_control_app.ui.settings

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    interface OnFragmentSendBluetoothDataListener {
        fun onSendBluetoothData(m_bluetoothSocket: BluetoothSocket?)
    }

    private lateinit var fragmentSendBluetoothDataListener: OnFragmentSendBluetoothDataListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentSendBluetoothDataListener = context as OnFragmentSendBluetoothDataListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context должен реализовывать интерфейс OnFragmentSendBluetoothDataListener")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {



        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_settings, container, false)

        // Creating SettingsViewModel object with TextViewModelFactory
        val settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory())
                .get(SettingsViewModel::class.java)

        binding.settingsViewModel = settingsViewModel

        binding.lifecycleOwner = this

        binding.connectButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Bluetooth Devices")
                    .setItems(settingsViewModel.pairedDeviceList().toTypedArray()) { _, which ->
                        settingsViewModel.connectTo(which)
                    }
                    .show()
        }

        return binding.root
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}