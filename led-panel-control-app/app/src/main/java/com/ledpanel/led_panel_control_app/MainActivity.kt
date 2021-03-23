package com.ledpanel.led_panel_control_app

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ledpanel.led_panel_control_app.ui.draw.DrawFragment
import com.ledpanel.led_panel_control_app.ui.image.ImageFragment
import com.ledpanel.led_panel_control_app.ui.queue.QueueFragment
import com.ledpanel.led_panel_control_app.ui.settings.SettingsFragment
import com.ledpanel.led_panel_control_app.ui.text.TextFragment
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val manager = supportFragmentManager

    private var textFragment: Fragment? = null
    private var imageFragment: Fragment? = null
    private var drawFragment: Fragment? = null
    private var queueFragment: Fragment? = null
    private var settingsFragment: Fragment? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_text -> {
                createTextFragment()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_image -> {
                createImageFragment()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_draw -> {
                createDrawFragment()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_queue -> {
                createQueueFragment()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_settings -> {
                createSettingsFragment()
                return@OnNavigationItemSelectedListener true
            }
        }
        return@OnNavigationItemSelectedListener false
    }

    private fun createSettingsFragment() {
//        if(settingsFragment == null) settingsFragment = SettingsFragment()
//        switchFragments(settingsFragment)
//        val transaction = manager.beginTransaction()
        if (settingsFragment == null) settingsFragment = SettingsFragment()   // *****code changed here***********
//        transaction.replace(R.id.nav_host_fragment, settingsFragment!!)
//        transaction.addToBackStack(null)
//        transaction.commit()
        switchFragments(settingsFragment)
    }

    private fun createQueueFragment() {
//        if(queueFragment == null) queueFragment = QueueFragment()
//        switchFragments(queueFragment)
        val transaction = manager.beginTransaction()
        if(queueFragment == null) queueFragment = QueueFragment()   // *****code changed here***********
        transaction.replace(R.id.nav_host_fragment, queueFragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
//        switchFragments(queueFragment)
    }

    private fun createDrawFragment() {
//        if(drawFragment == null) drawFragment = DrawFragment()
//        switchFragments(drawFragment)
//        val transaction = manager.beginTransaction()
        if(drawFragment == null) drawFragment = DrawFragment()   // *****code changed here***********
//        transaction.replace(R.id.nav_host_fragment, drawFragment!!)
//        transaction.addToBackStack(null)
//        transaction.commit()
        switchFragments(drawFragment)
    }

    private fun createImageFragment() {
//        if(imageFragment == null) imageFragment = ImageFragment()
//        switchFragments(imageFragment)
//        val transaction = manager.beginTransaction()
        if(imageFragment == null) imageFragment = ImageFragment()   // *****code changed here***********
//        transaction.replace(R.id.nav_host_fragment, imageFragment!!)
//        transaction.addToBackStack(null)
//        transaction.commit()
        switchFragments(imageFragment)
    }

    private fun createTextFragment() {
//        if(textFragment == null) textFragment = TextFragment()
//        switchFragments(textFragment)
//        val transaction = manager.beginTransaction()
        if(textFragment == null) {
            Log.i("Main", "TextCreated")
            textFragment = TextFragment()
        }   // *****code changed here***********
//        transaction.replace(R.id.nav_host_fragment, textFragment!!)
//        transaction.addToBackStack(null)
//        transaction.commit()
        switchFragments(textFragment)
    }

    private fun switchFragments(fragment: Fragment?) {
        manager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment!!)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.itemIconTintList = null

        createTextFragment()
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}