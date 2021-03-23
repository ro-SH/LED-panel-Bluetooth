package com.ledpanel.led_panel_control_app.ui.text

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.util.ColorUtil
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.databinding.FragmentTextBinding

class TextFragment : Fragment() {

    override fun onDestroy() {
        super.onDestroy()
        Log.i("TextFragment", "OnDestrView")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i("TextFragment", "OnCreateView")

        val binding: FragmentTextBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_text, container, false)

        // Creating TextViewModel object with TextViewModelFactory
        val textViewModel = ViewModelProvider(this, TextViewModelFactory())
            .get(TextViewModel::class.java)

        // Data Binding
        binding.textViewModel = textViewModel

        // Live Data Binding
        binding.lifecycleOwner = this

        binding.stringTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                textViewModel.setType(parent!!.selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }

        // Color Button
        binding.colorButton.setOnClickListener { _ ->

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

        textViewModel.color.observe(viewLifecycleOwner, { newColor ->
            setBackgroundColor(binding.colorButton, newColor)
        })

        // Speed Slider
        binding.speedSlider.addOnChangeListener { _, value, _ ->
            textViewModel.setSpeed(value)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setBackgroundColor(button: Button, newColor: Int) {
        if (ColorUtil.isDarkColor(newColor)) {
            button.setTextColor(Color.WHITE)
        } else {
            button.setTextColor(Color.BLACK)
        }
        button.setBackgroundColor(newColor)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.actionbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> this.findNavController().navigate(R.id.action_navigation_text_to_navigation_about)
        }
        return super.onOptionsItemSelected(item)
    }
}