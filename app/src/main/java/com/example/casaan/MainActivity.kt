package com.example.casaan

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.casaan.databinding.ActivityMainBinding
import com.example.casaan.ui.home.HomeFragment
import org.eclipse.paho.client.mqttv3.*
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import org.w3c.dom.Text
import java.io.FileReader

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var gardenscene = "";
    private var livingroomscene = "";

    val activityViewModel by viewModels<MainViewModel>()

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

                var mm = mqttMessage.toString()
                val offcolor = 0xFF999999.toInt()
                val oncolor = 0xFF00FF00.toInt()


                if (topic == "home/scene/garden") {
                    gardenscene = mm;
                    var gardenoncolor = offcolor
                    var gardenfencecolor = offcolor
                    var gardenoffcolor = offcolor
                    if (mm == "on") gardenoncolor = oncolor
                    if (mm == "fenceon") gardenfencecolor = oncolor
                    if (mm == "off") gardenoffcolor = oncolor

                    findViewById<View>(R.id.buttonGardenOn).setBackgroundColor(gardenoncolor)
                    findViewById<View>(R.id.buttonGardenFence).setBackgroundColor(gardenfencecolor)
                    findViewById<View>(R.id.buttonGardenOff).setBackgroundColor(gardenoffcolor)
                }

                if (topic == "home/scene/livingroom") {
                    livingroomscene = mm
                    var livingroomeveningcolor = offcolor
                    var livingroomtvcolor = offcolor
                    var livingroombrightcolor = offcolor
                    var livingroomoffcolor = offcolor
                    if (mm == "evening") livingroomeveningcolor = oncolor
                    if (mm == "tvonly") livingroomtvcolor = oncolor
                    if (mm == "bright") livingroombrightcolor = oncolor
                    if (mm == "off") livingroomoffcolor = oncolor

                    findViewById<View>(R.id.buttonEvening).setBackgroundColor(livingroomeveningcolor)
                    findViewById<View>(R.id.buttonTV).setBackgroundColor(livingroomtvcolor)
                    findViewById<View>(R.id.buttonBright).setBackgroundColor(livingroombrightcolor)
                    findViewById<View>(R.id.buttonOff).setBackgroundColor(livingroomoffcolor)
                }

                if ((livingroomscene == "off") && (gardenscene == "off")) {
                    findViewById<View>(R.id.buttonAllOff).setBackgroundColor(oncolor)
                } else {
                    findViewById<View>(R.id.buttonAllOff).setBackgroundColor(offcolor)
                }


                if (topic == "home/ESP_SMARTMETER/electricity/watt") {
                    val txt = findViewById<TextView>(R.id.text_notifications)
                    activityViewModel.setNotificationMessage("test")
                }
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                android.util.Log.w("Debug", "Message published to host '$MQTT_MQTT_HOST'")
            }
        })
    }
}
