package com.example.casaan

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.casaan.databinding.ActivityMainBinding
import com.example.casaan.ui.home.HomeFragment
import com.example.casaan.ui.dashboard.DashboardFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val mqttmap: MutableMap<String, String> = mutableMapOf()

    val mqttClient by lazy {
        MqttClientHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        setMqttCallBack()
        mqttClient.connect()
    }

    fun getIntance() {
        return this.getIntance()
    }

    override fun onResume() {
        super.onResume()
        if (!mqttClient.isConnected()) mqttClient.connect()
    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                val snackbarMsg = "Connected to host:\n'$MQTT_MQTT_HOST'."
                android.util.Log.w("Debug", snackbarMsg)

                mqttClient.subscribe("home/scene/livingroom")
                mqttClient.subscribe("home/scene/garden")
                mqttClient.subscribe("home/ESP_SMARTMETER/electricity/watt")
                mqttClient.subscribe("home/ESP_GROWATT/grid/watt")
                mqttClient.subscribe("home/ESP_SDM120/power")
                mqttClient.subscribe("home/SONOFF_AIRCO/power")
                mqttClient.subscribe("home/SONOFF_TV/power")
            }

            override fun connectionLost(throwable: Throwable) {
                val snackbarMsg = "Connection to host lost:\n'$MQTT_MQTT_HOST'"
                android.util.Log.w("Debug", snackbarMsg)
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                android.util.Log.w(
                    "Debug",
                    "Message received from host '$MQTT_MQTT_HOST': $topic = $mqttMessage"
                )

                mqttmap[topic] = mqttMessage.toString()

                try {
                    var navHomeFragment = supportFragmentManager.primaryNavigationFragment
                    var navFragment = navHomeFragment?.childFragmentManager?.fragments?.get(0)
                    if (navFragment is DashboardFragment) {
                        navFragment.mqttupdate(topic)
                    }
                    if (navFragment is HomeFragment) {
                        navFragment.mqttupdate(topic)
                    }
                } catch (e: java.lang.Exception) {
                    android.util.Log.e("mqtt", e.toString())
                }

            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                android.util.Log.w("Debug", "Message published to host '$MQTT_MQTT_HOST'")
            }
        })
    }
}
