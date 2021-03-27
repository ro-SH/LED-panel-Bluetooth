package com.ledpanel.led_panel_control_app

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ledpanel.led_panel_control_app.bluetooth.BluetoothConnection
import com.ledpanel.led_panel_control_app.ui.draw.DrawFragment
import com.ledpanel.led_panel_control_app.ui.image.ImageFragment
import com.ledpanel.led_panel_control_app.ui.queue.QueueFragment
import com.ledpanel.led_panel_control_app.ui.settings.SettingsFragment
import com.ledpanel.led_panel_control_app.ui.text.TextFragment

class MainActivity : AppCompatActivity(), DataTransfer, SettingsFragment.Communicator {

    private val manager = supportFragmentManager

    private lateinit var btConnection: BluetoothConnection

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
        if (settingsFragment == null) settingsFragment = SettingsFragment()
        switchFragments(settingsFragment)
    }

    private fun createQueueFragment() {
        if(queueFragment == null) queueFragment = QueueFragment()
        switchFragments(queueFragment)
    }

    private fun createDrawFragment() {
        if(drawFragment == null) drawFragment = DrawFragment()
        switchFragments(drawFragment)
    }

    private fun createImageFragment() {
        if(imageFragment == null) imageFragment = ImageFragment()
        switchFragments(imageFragment)
    }

    private fun createTextFragment() {
        if(textFragment == null) textFragment = TextFragment()
        switchFragments(textFragment)
    }

    private fun switchFragments(fragment: Fragment?) {
        manager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment!!)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.itemIconTintList = null

        btConnection = BluetoothConnection(this)
        btConnection.enableBluetoothAdapter()

        createTextFragment()
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun getDevicesNames(isPaired: Boolean): ArrayList<String> {
        return btConnection.getDevicesNames(isPaired)
    }

    override fun sendDeviceId(deviceID: Int, isPaired: Boolean) {
        if (!isPaired) btConnection.pairDevice(deviceID)
        btConnection.connectDevice(deviceID)
    }

    override fun disconnectDevice() {
        if (btConnection.isConnected()) {
            btConnection.sendCommand("0+0+0+ +|")
            btConnection.stopConnectDevice()
        }
    }

    override fun sendData(data: String) {
        btConnection.sendCommand(data)
    }

    override fun onDestroy() {
        super.onDestroy()
//        btConnection.cleanUp()
    }
}

interface DataTransfer {
    fun sendData(data: String)
}