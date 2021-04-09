package com.ledpanel.led_panel_control_app.ui.draw

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.aboutDraw
import com.ledpanel.led_panel_control_app.aboutText
import com.ledpanel.led_panel_control_app.databinding.FragmentDrawBinding
import com.ledpanel.led_panel_control_app.setBackgroundColor
import com.ledpanel.led_panel_control_app.ui.about.AboutFragment

const val WIDTH = 32
const val HEIGHT = 8

class DrawFragment : Fragment() {

    // DataBinding
    private lateinit var binding: FragmentDrawBinding

    // ViewModel for DrawFragment
    private lateinit var viewModel: DrawViewModel

    // Canvas
    private lateinit var drawView: DrawView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_draw, container, false)

        binding.lifecycleOwner = this

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
                val fragment = AboutFragment.create("Draw", aboutDraw)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, "AboutDraw")
                    .addToBackStack(null)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel for DrawFragment
        viewModel = ViewModelProvider(this).get(DrawViewModel::class.java)

        // View for drawing on screen
        drawView = DrawView(requireContext(), HEIGHT, WIDTH, viewModel.color.value!!)

        drawView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )

        binding.fragmentDrawCanvasLayout.addView(drawView)

        // Color Button
        binding.fragmentDrawColorButton.setOnClickListener { _ ->

            // Open the Color Picker Dialog
            ColorPickerDialog
                    .Builder(requireActivity()) // Pass Activity Instance
                    .setColorShape(ColorShape.SQAURE) // Or ColorShape.CIRCLE
                    .setDefaultColor(viewModel.color.value!!) // Pass Default Color
                    .setColorListener { color, _ ->
                        viewModel.setColor(color)
                        drawView.setDrawColor(color)
                    }
                    .show()
        }

        viewModel.color.observe(viewLifecycleOwner, { newColor ->
            setBackgroundColor(binding.fragmentDrawColorButton, newColor)
        })

        // Clear Button
        binding.fragmentDrawIbClear.setOnClickListener { drawView.fill() }

        // Fill Button
        binding.fragmentDrawIbFill.setOnClickListener { drawView.fill(true) }

        // Draw Button
        binding.fragmentDrawIbDraw.setOnClickListener { drawView.setDrawMode(DRAW) }

        // Erase Button
        binding.fragmentDrawIbErase.setOnClickListener { drawView.setDrawMode(ERASE) }
    }
}
