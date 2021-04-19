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
import com.ledpanel.led_panel_control_app.*
import com.ledpanel.led_panel_control_app.databinding.FragmentDrawBinding
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

    // DataTransfer interface
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
                val fragment = AboutFragment.create(getString(R.string.about_draw_title), getString(R.string.about_draw_description))
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.activity_main__nav_host_fragment, fragment, "AboutDraw")
                    .addToBackStack(null)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update fragment title
        (requireActivity() as MainActivity).updateActionBarTitle(getString(R.string.draw_title))

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

        // Draw Mode
        viewModel.drawMode.observe(
            viewLifecycleOwner,
            { newMode ->
                val newText = "Mode: " + when (newMode) {
                    DRAW -> "Draw"
                    ERASE -> "Erase"
                    else -> ""
                }
                binding.fragmentSettingsTvMode?.text = newText
            }
        )

        // Drawing
        drawView.setOnTouchListener { _, event -> return@setOnTouchListener onDrawViewTouch(event) }

        // Color Button
        binding.fragmentDrawBtnColor.setOnClickListener {

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
                setBackgroundColor(binding.fragmentDrawBtnColor, newColor)
            }
        )

        // Clear Button
        binding.fragmentDrawIbClear.setOnClickListener { onClearClicked() }

        // Fill Button
        binding.fragmentDrawIbFill.setOnClickListener { onFillClicked() }

        // Draw Button
        binding.fragmentDrawIbDraw.setOnClickListener {
            viewModel.setDrawMode(DRAW)
        }

        // Erase Button
        binding.fragmentDrawIbErase.setOnClickListener { viewModel.setDrawMode(ERASE) }
    }

    /**
     *  Fill the panel with black color.
     */
    private fun onClearClicked() {
        drawView.fill()
        if (comm.isConnected()) comm.fill()
    }

    /**
     *  Fill the panel with current color.
     */
    private fun onFillClicked() {
        drawView.fill(true)
        if (comm.isConnected())
            comm.fill(
                Color.red(viewModel.color.value!!),
                Color.green(viewModel.color.value!!),
                Color.blue(viewModel.color.value!!)
            )
    }

    private var saved_data = ""

    /**
     *  On DrawViewTouch.
     *  Performs drawing.
     *  @return 'true'
     */
    private fun onDrawViewTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
//            MotionEvent.ACTION_DOWN -> {
                val coordinates: Pair<Int, Int>? = drawView.fillPixel(viewModel.drawMode.value!!, event.x, event.y)
                if (coordinates != null && comm.isConnected()) {
                    val data = when (viewModel.drawMode.value) {
                        DRAW -> {
                            val red = Color.red(viewModel.color.value!!)
                            val green = Color.green(viewModel.color.value!!)
                            val blue = Color.blue(viewModel.color.value!!)
                            "d+${coordinates.first}+${coordinates.second}+$red+$green+$blue+|"
                        }

                        else -> "d+${coordinates.first}+${coordinates.second}+0+0+0+|"
                    }
                    if (data != saved_data) {
                        comm.sendData(data)
                        saved_data = data
                    }
                }
            }
        }

        return true
    }
}
