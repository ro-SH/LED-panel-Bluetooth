package com.ledpanel.led_panel_control_app.ui.settings

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ledpanel.led_panel_control_app.*
import com.ledpanel.led_panel_control_app.databinding.FragmentSettingsBinding
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
    }

    private val TAG = "Settings"

    // Data binding
    private lateinit var binding: FragmentSettingsBinding

    // ViewModel for SettingsFragment
    private lateinit var settingsViewModel: SettingsViewModel

    // Communicator to MainActivity
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
                    .replace(R.id.nav_host_fragment, fragment, "AboutSettings")
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

        binding.fragmentSettingsWidth.setText(requireArguments().getInt("width").toString())
        binding.fragmentSettingsHeight.setText(requireArguments().getInt("height").toString())

        // Connect Button
        binding.fragmentSettingsConnectButton.setOnClickListener {

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
        binding.fragmentSettingsSaveButton.setOnClickListener {
            try {
                comm.setSize(binding.fragmentSettingsWidth.text.toString().toInt(), binding.fragmentSettingsHeight.text.toString().toInt())
                Toast.makeText(requireContext(), "Successfully saved!", Toast.LENGTH_SHORT).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Incorrect size!", Toast.LENGTH_SHORT).show()
            } finally {
                hideKeyboard()
            }
        }

        // disconnect Button
        binding.fragmentSettingsDisconnectButton.setOnClickListener {
            comm.disconnectDevice()
            settingsViewModel.deleteDeviceData()
        }
    }
}
