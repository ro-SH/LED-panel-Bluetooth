package com.ledpanel.led_panel_control_app.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ledpanel.led_panel_control_app.R

class AboutFragment : Fragment() {

    companion object {
        fun create(mainFragmentName: String, text: String): AboutFragment {
            val extras = Bundle().apply {
                putString("mainFragmentName", mainFragmentName)
                putString("text", text)
            }

            return AboutFragment().apply {
                arguments = extras
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.fragment_about__description).text = requireArguments().getString("text")
    }
}
