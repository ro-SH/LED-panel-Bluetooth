package com.ledpanel.led_panel_control_app.ui.draw

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.ledpanel.led_panel_control_app.DataTransfer
import com.ledpanel.led_panel_control_app.R
import com.ledpanel.led_panel_control_app.aboutDraw
import com.ledpanel.led_panel_control_app.databinding.FragmentDrawBinding
import com.ledpanel.led_panel_control_app.setBackgroundColor
import com.ledpanel.led_panel_control_app.ui.about.AboutFragment

class DrawFragment : Fragment() {

    companion object {
        fun create(width: Int, height: Int): DrawFragment {
            val extras = Bundle().apply {
                putInt("width", width)
                putInt("height", height)
            }
            return DrawFragment().apply {
                arguments = extras
            }
        }
    }

    private lateinit var comm: DataTransfer

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
            inflater, R.layout.fragment_draw, container, false
        )

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

        comm = requireActivity() as DataTransfer

        // ViewModel for DrawFragment
        viewModel = ViewModelProvider(this).get(DrawViewModel::class.java)
        viewModel.height = requireArguments().getInt("height")
        viewModel.width = requireArguments().getInt("width")

        // View for drawing on screen
        drawView = DrawView(
            requireContext(),
            viewModel.height,
            viewModel.width,
            viewModel.color.value!!
        )

        drawView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        binding.fragmentDrawCanvasLayout.addView(drawView)

        // Drawing
        drawView.setOnTouchListener { _, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val coordinates: Pair<Int, Int>? = drawView.fillPixel(event.x, event.y)
                    if (coordinates != null && comm.isConnected()) {
                        val red = Color.red(viewModel.color.value!!)
                        val green = Color.green(viewModel.color.value!!)
                        val blue = Color.blue(viewModel.color.value!!)
                        val data = "${coordinates.first}+${coordinates.second}+$red+$green+$blue+|"
                        comm.sendData(data)
                    }
                }
            }

            return@setOnTouchListener true
        }

        // Color Button
        binding.fragmentDrawColorButton.setOnClickListener {

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

        viewModel.color.observe(
            viewLifecycleOwner,
            { newColor ->
                setBackgroundColor(binding.fragmentDrawColorButton, newColor)
            }
        )

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
