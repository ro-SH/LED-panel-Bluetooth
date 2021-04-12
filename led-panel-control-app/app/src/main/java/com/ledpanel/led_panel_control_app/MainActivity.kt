package com.ledpanel.led_panel_control_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ledpanel.led_panel_control_app.bluetooth.BluetoothConnection
import com.ledpanel.led_panel_control_app.ui.draw.DrawFragment
import com.ledpanel.led_panel_control_app.ui.queue.QueueFragment
import com.ledpanel.led_panel_control_app.ui.queue.QueueItem
import com.ledpanel.led_panel_control_app.ui.settings.SettingsFragment
import com.ledpanel.led_panel_control_app.ui.text.TextFragment

const val DEFAULT_WIDTH = 32
const val DEFAULT_HEIGHT = 8

class MainActivity : AppCompatActivity(), DataTransfer {

    private var width: Int = DEFAULT_WIDTH
    private var height: Int = DEFAULT_HEIGHT

    private val manager = supportFragmentManager

    // Bluetooth Connection
    private lateinit var btConnection: BluetoothConnection

    // True if passing time to the panel
    private var passTime: Boolean = false

    // True if passing Queue
    private var passQueue: Boolean = false

    // Fragments
    private var textFragment: Fragment? = null
    private var drawFragment: Fragment? = null
    private var queueFragment: Fragment? = null
    private var settingsFragment: Fragment? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_text -> {
                createTextFragment()
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
        if (settingsFragment == null) settingsFragment = SettingsFragment.create(width, height)
        switchFragments(settingsFragment)
    }

    /**
     *  Create QueueFragment instance if not created. Switch to QueueFragment.
     */
    private fun createQueueFragment() {
        if (queueFragment == null) queueFragment = QueueFragment()
        switchFragments(queueFragment)
    }

    /**
     *  Create DrawFragment instance if not created. Switch to DrawFragment.
     */
    private fun createDrawFragment() {
        if (drawFragment == null ||
            drawFragment!!.requireArguments().getInt("width") != width ||
            drawFragment!!.requireArguments().getInt("height") != height
        ) {
            drawFragment = DrawFragment.create(width, height)
        }

        switchFragments(drawFragment)
    }

    /**
     *  Create TextFragment instance if not created. Switch to TextFragment.
     */
    private fun createTextFragment() {
        if (textFragment == null) textFragment = TextFragment()
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
            stopPassing()
            btConnection.sendCommand("0+0+0+ +|")
            btConnection.stopConnectDevice()
        }
    }

    override fun showTime(red: Int, green: Int, blue: Int) {
        stopPassing()
        passTime = true
        Thread(
            Runnable {
                var currentTime = ""
                while (passTime) {
                    val newTime = getCurrentTime()
                    if (newTime != currentTime) {
                        currentTime = newTime
                        val data = "$red+$green+$blue+$currentTime+|"
                        btConnection.sendCommand(data)
                    }
                }
            }
        ).start()
    }

    fun isShowingQueue() = passQueue

    override fun showQueue(queue: List<QueueItem>, showTime: Boolean, red: Int, green: Int, blue: Int) {
        stopPassing()

        passQueue = true
        when (showTime) {
            true -> {
                val tempQueue = queue.toMutableList()
                Thread(
                    Runnable {
                        while (passQueue && tempQueue.size > 0) {
                            val newTime = getCurrentTime()
                            if (tempQueue[0].time == newTime) {
                                val text = tempQueue[0].text
                                val data = "$red+$green+$blue+$newTime $text+|"
                                btConnection.sendCommand(data)
                                tempQueue.removeAt(0)
                            }
                        }
                    }
                ).start()
            }

            else -> {
                val text = queue[0].text
                val data = "$red+$green+$blue+$text+|"
                btConnection.sendCommand(data)
            }
        }
    }

    /**
     *  Stop passing data
     */
    private fun stopPassing() {
        passTime = false
        passQueue = false
    }

    /**
     *  Fill panel with RGB color
     *  @param red
     *  @param green
     *  @param blue
     */
    override fun fill(red: Int, green: Int, blue: Int) {
        val data = "f+$red+$green+$blue+|"
//        btConnection.sendCommand(data)
    }

    /**
     *  Transfer data via Bluetooth
     *  @param data
     */
    override fun sendData(data: String) {
        stopPassing()
        btConnection.sendCommand(data)
    }

    override fun isConnected(): Boolean {
        return btConnection.isConnected()
    }

    override fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun onDestroy() {
        super.onDestroy()
//        btConnection.cleanUp()
    }

    fun updateActionBarTitle(title: String) {
        supportActionBar?.title = title
    }
}

/**
 *  Interface for transferring data Via Bluetooth
 */
interface DataTransfer {

    /**
     *  Transfer disconnect device signal
     */
    fun disconnectDevice()

    /**
     *  Fill the panel with color
     */
    fun fill(red: Int = 0, green: Int = 0, blue: Int = 0)

    /**
     *  Transfer Device Id to MainActivity
     *  @param deviceID
     *  @param isPaired 'true' if device is paired
     */
    fun sendDeviceId(deviceID: Int, isPaired: Boolean)

    /**
     *  Set new LED panel size
     *  @param width
     *  @param height
     */
    fun setSize(width: Int, height: Int)

    /**
     *  Send Data bia Bluetooth
     *  @param data as String
     */
    fun sendData(data: String)

    /**
     *  Start showing queue on panel
     */
    fun showQueue(queue: List<QueueItem>, showTime: Boolean, red: Int, green: Int, blue: Int)

    /**
     *  Start showing current time on panel
     */
    fun showTime(red: Int, green: Int, blue: Int)

    /**
     *  Returns 'true' if connected to Bluetooth device
     */
    fun isConnected(): Boolean
}
