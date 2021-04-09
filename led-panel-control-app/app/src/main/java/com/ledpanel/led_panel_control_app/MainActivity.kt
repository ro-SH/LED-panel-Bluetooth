package com.ledpanel.led_panel_control_app

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.ui.NavigationUI
import com.ledpanel.led_panel_control_app.bluetooth.BluetoothConnection
import com.ledpanel.led_panel_control_app.ui.draw.DrawFragment
import com.ledpanel.led_panel_control_app.ui.image.ImageFragment
import com.ledpanel.led_panel_control_app.ui.queue.QueueFragment
import com.ledpanel.led_panel_control_app.ui.settings.SettingsFragment
import com.ledpanel.led_panel_control_app.ui.text.TextFragment

class MainActivity : AppCompatActivity(), DataTransfer, SettingsFragment.Communicator {

    private val manager = supportFragmentManager

    // Bluetooth Connection
    private lateinit var btConnection: BluetoothConnection

    // Fragments
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

    /**
     *  Create SettingsFragment instance if not created. Switch to SettingsFragment.
     */
    private fun createSettingsFragment() {
        if (settingsFragment == null) settingsFragment = SettingsFragment()
        switchFragments(settingsFragment)
    }

    /**
     *  Create QueueFragment instance if not created. Switch to QueueFragment.
     */
    private fun createQueueFragment() {
        if(queueFragment == null) queueFragment = QueueFragment()
        switchFragments(queueFragment)
    }

    /**
     *  Create DrawFragment instance if not created. Switch to DrawFragment.
     */
    private fun createDrawFragment() {
        if(drawFragment == null) drawFragment = DrawFragment()
        switchFragments(drawFragment)
    }

    /**
     *  Create ImageFragment instance if not created. Switch to ImageFragment.
     */
    private fun createImageFragment() {
        if(imageFragment == null) imageFragment = ImageFragment()
        switchFragments(imageFragment)
    }

    /**
     *  Create TextFragment instance if not created. Switch to TextFragment.
     */
    private fun createTextFragment() {
        if(textFragment == null) textFragment = TextFragment()
        switchFragments(textFragment)
    }

    /**
     *  Switch to new Fragment
     *  @param fragment New fragment to show
     */
    private fun switchFragments(fragment: Fragment?) {
        clearBackStack()
        manager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment!!)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
    }

    /**
     *  Clear BackStack to avoid UI overlay
     */
    private fun clearBackStack() {
        if (manager.backStackEntryCount > 0)
            manager.popBackStack(manager.getBackStackEntryAt(0).id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.itemIconTintList = null

        btConnection = BluetoothConnection(this)
        btConnection.enableBluetoothAdapter()

        if (savedInstanceState == null)
            createTextFragment()
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    /**
     *  Get ArrayList of Paired or Discovered Devices
     *  @param isPaired 'true' if paired devices
     *  @return ArrayList of Paired or Discovered Devices
     */
    fun getDevicesNames(isPaired: Boolean): ArrayList<String> {
        return btConnection.getDevicesNames(isPaired)
    }

    /**
     *  Connect to device
     *  @param deviceID Device ID in the list of devices
     *  @param isPaired 'true' if paired devices
     */
    override fun sendDeviceId(deviceID: Int, isPaired: Boolean) {
        if (!isPaired) btConnection.pairDevice(deviceID)
        btConnection.connectDevice(deviceID)
    }

    /**
     *  Disconnect from current device
     */
    override fun disconnectDevice() {
        if (btConnection.isConnected()) {
            btConnection.sendCommand("0+0+0+ +|")
            btConnection.stopConnectDevice()
        }
    }

    /**
     *  Transfer data via Bluetooth
     *  @param data
     */
    override fun sendData(data: String) {
        btConnection.sendCommand(data)
    }

    override fun isConnected(): Boolean {
        return btConnection.isConnected()
    }

    override fun onDestroy() {
        super.onDestroy()
//        btConnection.cleanUp()
    }
}

/**
 *  Interface for transferring data Via Bluetooth
 */
interface DataTransfer {
    fun sendData(data: String)
    fun isConnected(): Boolean
}