package com.ledpanel.led_panel_control_app.ui.settings

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.MainActivity
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.aboutDraw
import com.ledpanel.led_panel_control_app.aboutSettings
import com.ledpanel.led_panel_control_app.databinding.FragmentSettingsBinding
import com.ledpanel.led_panel_control_app.ui.about.AboutFragment

class SettingsFragment : Fragment() {

    // Data binding
    private lateinit var binding: FragmentSettingsBinding

    // ViewModel for SettingsFragment
    private lateinit var settingsViewModel: SettingsViewModel

    // Communicator to MainActivity
    private lateinit var comm: Communicator

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        comm = requireActivity() as Communicator

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_settings, container, false)

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
                val fragment = AboutFragment.create("Settings", aboutSettings)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, "AboutSettings")
                    .addToBackStack(null)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Creating SettingsViewModel object with TextViewModelFactory
        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory())
            .get(SettingsViewModel::class.java)

        binding.settingsViewModel = settingsViewModel

        binding.lifecycleOwner = this

        // Connect Button
        binding.connectButton.setOnClickListener {

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

        // disconnect Button
        binding.disconnectButton.setOnClickListener {
            comm.disconnectDevice()
            settingsViewModel.deleteDeviceData()
        }
    }

    /**
     *  Interface for transferring data to MainActivity
     */
    interface Communicator {
        /**
         *  Transfer Device Id to MainActivity
         *  @param deviceID
         *  @param isPaired 'true' if device is paired
         */
        fun sendDeviceId(deviceID: Int, isPaired: Boolean)

        /**
         *  Transfer disconnect device signal
         */
        fun disconnectDevice()
    }
}