//
// Based on: https://solace.com/blog/event-driven-kotlin-android-app-mqtt-solace-pubsub/

package com.example.casaan

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

const val MQTT_CLIENT_USER_NAME = "user"
const val MQTT_CLIENT_PASSWORD = "pass"
const val MQTT_MQTT_HOST = "ssl://server:8883"

// Other options
const val MQTT_CONNECTION_TIMEOUT = 3
const val MQTT_CONNECTION_KEEP_ALIVE_INTERVAL = 60
const val MQTT_CONNECTION_CLEAN_SESSION = true
const val MQTT_CONNECTION_RECONNECT = true

class MqttClientHelper(context: Context?) {

    companion object {
        const val TAG = "MqttClientHelper"
    }

    var mqttAndroidClient: MqttAndroidClient
    val serverUri = MQTT_MQTT_HOST
    private val clientId: String = MqttClient.generateClientId()

    fun setCallback(callback: MqttCallbackExtended?) {
        mqttAndroidClient.setCallback(callback)
    }

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w(TAG, s)
                Log.w(TAG, "Mqtt Connected")
            }

            override fun connectionLost(throwable: Throwable)
            {
                Log.w(TAG, "Mqtt Connection Lost")
            }
            @Throws(Exception::class)
            override fun messageArrived(
                topic: String,
                mqttMessage: MqttMessage
            ) {
                Log.w(TAG, mqttMessage.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
        connect()
    }

    public fun connect() {
        if (this.isConnected()) return

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = MQTT_CONNECTION_RECONNECT
        mqttConnectOptions.isCleanSession = MQTT_CONNECTION_CLEAN_SESSION
        mqttConnectOptions.userName = MQTT_CLIENT_USER_NAME
        mqttConnectOptions.password = MQTT_CLIENT_PASSWORD.toCharArray()
        mqttConnectOptions.connectionTimeout = MQTT_CONNECTION_TIMEOUT
        mqttConnectOptions.keepAliveInterval = MQTT_CONNECTION_KEEP_ALIVE_INTERVAL
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions =
                        DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    Log.w(TAG, "Mqtt Connected")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.w(TAG, "Failed to connect to: $serverUri ; $exception")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    fun subscribe(subscriptionTopic: String, qos: Int = 0) {
        try {
            if (!this.isConnected()) this.connect()
            if (!this.isConnected()) return

            mqttAndroidClient.subscribe(subscriptionTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w(TAG, "Subscribed to topic '$subscriptionTopic'")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.w(TAG, "Subscription to topic '$subscriptionTopic' failed!")
                }
            })
        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$subscriptionTopic'")
            ex.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 0) {
        try {
            if (!this.isConnected()) this.connect()
            if (!this.isConnected()) return

            if (this.isConnected()) {
                val message = MqttMessage()
                message.payload = msg.toByteArray()
                mqttAndroidClient.publish(topic, message.payload, qos, false)
                Log.d(TAG, "Message published to topic `$topic`: $msg")
            }
            else {
                Log.w(TAG, "Message NOT published (not connected) to topic `$topic`: $msg")
            }
        } catch (e: MqttException) {
            Log.d(TAG, "Error Publishing to $topic: " + e.message)
            e.printStackTrace()
        }

    }

    fun isConnected() : Boolean {
        return mqttAndroidClient.isConnected
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }
}