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
import com.ledpanel.led_panel_control_app.databinding.FragmentSettingsBinding
import com.ledpanel.led_panel_control_app.hideKeyboard
import com.ledpanel.led_panel_control_app.ui.about.AboutFragment
import java.lang.NumberFormatException

class SettingsFragment : Fragment() {

    companion object {
        fun create(deviceName: String, deviceAddress: String, width: Int, height: Int, brightness: Int): SettingsFragment {
            val extras = Bundle().apply {
                putString("name", deviceName)
                putString("address", deviceAddress)
                putInt("width", width)
                putInt("height", height)
                putInt("brightness", brightness)
            }

            return SettingsFragment().apply {
                arguments = extras
            }
        }
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
                val fragment = AboutFragment.create(getString(R.string.about_settings_title), getString(R.string.about_settings_description))
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

        (requireActivity() as MainActivity).updateActionBarTitle(getString(R.string.settings_title))

        // Creating SettingsViewModel object with TextViewModelFactory
        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory())
            .get(SettingsViewModel::class.java)

        binding.settingsViewModel = settingsViewModel

        binding.lifecycleOwner = this

        // Device name
        settingsViewModel.deviceName.observe(
            viewLifecycleOwner,
            { newName ->
                binding.fragmentSettingsTvName.text = if (newName.isNotEmpty()) "${getString(R.string.device_name)} $newName" else getString(R.string.no_connected_devices_text)
            }
        )

        // Set device name and address
        settingsViewModel.setDeviceData(
            requireArguments().getString("name")!!,
            requireArguments().getString("address")!!
        )

        binding.fragmentSettingsEtWidth.setText(requireArguments().getInt("width").toString())
        binding.fragmentSettingsEtHeight.setText(requireArguments().getInt("height").toString())
        binding.fragmentSettingsEtBrightness.setText(requireArguments().getInt("brightness").toString())

        // Connect Button
        binding.fragmentSettingsBtnConnect.setOnClickListener {

            // List of device names
            val deviceNames = (activity as MainActivity).getDevicesNames(true)
            when (deviceNames.size) {
                0 -> Toast.makeText(requireContext(), getString(R.string.no_paired_devices_text), Toast.LENGTH_SHORT).show()

                else -> MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.paired_devices_text))
                    .setItems(deviceNames.toTypedArray()) { _, which ->
                        comm.sendDeviceId(which, true)
                        val deviceName = deviceNames[which].substringBeforeLast("\nMAC: ")
                        val deviceAddress = deviceNames[which].substringAfterLast("MAC: ")
                        settingsViewModel.setDeviceData(deviceName, deviceAddress)
                        comm.setDeviceData(deviceName, deviceAddress)
                    }
                    .show()
            }
        }

        // Save Button
        binding.fragmentSettingsBtnSave.setOnClickListener { onSaveClicked() }

        // disconnect Button
        binding.fragmentSettingsBtnDisconnect.setOnClickListener {
            comm.disconnectDevice()
            settingsViewModel.deleteDeviceData()
        }
    }

    /**
     *  Send configuration data.
     */
    private fun onSaveClicked() {
        try {
            val width: Int = binding.fragmentSettingsEtWidth.text.toString().toInt()
            val height: Int = binding.fragmentSettingsEtHeight.text.toString().toInt()
            val brightness: Int = binding.fragmentSettingsEtBrightness.text.toString().toInt()
            if (width <= 0 || height <= 0)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.size_restriction_text),
                    Toast.LENGTH_SHORT
                ).show()
            else if (brightness < 0 || brightness > 255)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.brightness_restriction_text),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                comm.setConfiguration(width, height, brightness)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.saved_text),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(
                requireContext(),
                getString(R.string.incorrect_size_text),
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            hideKeyboard()
        }
    }
}
