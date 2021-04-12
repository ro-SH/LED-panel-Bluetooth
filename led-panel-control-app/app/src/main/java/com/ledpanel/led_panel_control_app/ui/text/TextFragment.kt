package com.ledpanel.led_panel_control_app.ui.text

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.ledpanel.led_panel_control_app.*
import com.ledpanel.led_panel_control_app.databinding.FragmentTextBinding
import com.ledpanel.led_panel_control_app.ui.about.AboutFragment
import java.util.*

private const val STATIC = 0
private const val ROLLER = 1
private const val TIME = 2

class TextFragment : Fragment() {

    private val TAG = "Text"

    // Data Binding
    private lateinit var binding: FragmentTextBinding

    // ViewModel for TextFragment
    private lateinit var textViewModel: TextViewModel

    private lateinit var comm: DataTransfer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_text, container, false
        )

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).updateActionBarTitle(TAG)

        comm = requireActivity() as DataTransfer

        // Creating TextViewModel object with TextViewModelFactory
        textViewModel = ViewModelProvider(this, TextViewModelFactory())
            .get(TextViewModel::class.java)

        // Data Binding
        binding.textViewModel = textViewModel

        // Live Data Binding
        binding.lifecycleOwner = this

        // Select Type Spinner
        binding.stringTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                textViewModel.setType(parent!!.selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        // Color Button
        binding.colorButton.setOnClickListener {

            // Open the Color Picker Dialog
            ColorPickerDialog
                .Builder(requireActivity()) // Pass Activity Instance
                .setColorShape(ColorShape.SQAURE) // Or ColorShape.CIRCLE
                .setDefaultColor(textViewModel.color.value!!) // Pass Default Color
                .setColorListener { color, _ ->
                    textViewModel.setColor(color)
                }
                .show()
        }

        textViewModel.color.observe(
            viewLifecycleOwner,
            { newColor ->
                setBackgroundColor(binding.colorButton, newColor)
            }
        )

        // Speed Slider
        binding.speedSlider.addOnChangeListener { _, value, _ ->
            textViewModel.setSpeed(value)
        }

        // Display Button
        binding.displayButton.setOnClickListener { onDisplayButtonClick() }
    }

    /**
     *  Called when the display button is pressed.
     *  Send information via Bluetooth.
     */
    private fun onDisplayButtonClick() {
        if (comm.isConnected()) {

            val red = Color.red(textViewModel.color.value!!)
            val green = Color.green(textViewModel.color.value!!)
            val blue = Color.blue(textViewModel.color.value!!)

            if (textViewModel.type.value == TIME)
                comm.showTime(red, green, blue)
            else {
                val text = textViewModel.text.value
                val speed = textViewModel.speed.value
                val data = when (textViewModel.type.value) {
                    STATIC -> "$red+$green+$blue+$text+|"
                    ROLLER -> "$red+$green+$blue+$text+$speed+|"
                    else -> null
                }

                data?.let { comm.sendData(it) }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.actionbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                val fragment = AboutFragment.create(TAG, aboutText)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, "AboutText")
                    .addToBackStack(null)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
