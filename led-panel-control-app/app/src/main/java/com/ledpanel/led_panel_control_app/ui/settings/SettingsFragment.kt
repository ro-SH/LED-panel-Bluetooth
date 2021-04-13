package com.ledpanel.led_panel_control_app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.DataTransfer
import com.ledpanel.led_panel_control_app.MainActivity
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.aboutSettings
import com.ledpanel.led_panel_control_app.databinding.FragmentSettingsBinding
import com.ledpanel.led_panel_control_app.hideKeyboard
import com.ledpanel.led_panel_control_app.ui.about.AboutFragment
import java.lang.NumberFormatException

class SettingsFragment : Fragment() {

    companion object {
        fun create(width: Int, height: Int): SettingsFragment {
            val extras = Bundle().apply {
                putInt("width", width)
                putInt("height", height)
            }

            return SettingsFragment().apply {
                arguments = extras
            }
        }

        private const val TAG = "Settings"
    }

    // Data binding
    private lateinit var binding: FragmentSettingsBinding

    // ViewModel for SettingsFragment
    private lateinit var settingsViewModel: SettingsViewModel

    // DataTransfer interface
    private lateinit var comm: DataTransfer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        comm = requireActivity() as DataTransfer

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_settings, container, false
        )

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.actionbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                val fragment = AboutFragment.create(TAG, aboutSettings)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_main__nav_host_fragment, fragment, "AboutSettings")
                    .addToBackStack(null)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).updateActionBarTitle(TAG)

        // Creating SettingsViewModel object with TextViewModelFactory
        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory())
            .get(SettingsViewModel::class.java)

        binding.settingsViewModel = settingsViewModel

        binding.lifecycleOwner = this

        binding.fragmentSettingsEtWidth.setText(requireArguments().getInt("width").toString())
        binding.fragmentSettingsEtHeight.setText(requireArguments().getInt("height").toString())

        // Connect Button
        binding.fragmentSettingsBtnConnect.setOnClickListener {

            // List of device names
            val deviceNames = (activity as MainActivity).getDevicesNames(true)
            when (deviceNames.size) {
                0 -> Toast.makeText(requireContext(), "No paired devices found!", Toast.LENGTH_SHORT).show()

                else -> MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Paired Bluetooth Devices")
                    .setItems(deviceNames.toTypedArray()) { _, which ->
                        comm.sendDeviceId(which, true)
                        val deviceName = deviceNames[which].substringBeforeLast("MAC: ")
                        val deviceAddress = deviceNames[which].substringAfterLast("MAC: ")
                        settingsViewModel.setDeviceData(deviceName, deviceAddress)
                    }
                    .show()
            }
        }

        // Save Button
        binding.fragmentSettingsBtnSave.setOnClickListener {
            try {
                comm.setSize(binding.fragmentSettingsEtWidth.text.toString().toInt(), binding.fragmentSettingsEtWidth.text.toString().toInt())
                Toast.makeText(requireContext(), "Successfully saved!", Toast.LENGTH_SHORT).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Incorrect size!", Toast.LENGTH_SHORT).show()
            } finally {
                hideKeyboard()
            }
        }

        // disconnect Button
        binding.fragmentSettingsBtnDisconnect.setOnClickListener {
            comm.disconnectDevice()
            settingsViewModel.deleteDeviceData()
        }
    }
}
