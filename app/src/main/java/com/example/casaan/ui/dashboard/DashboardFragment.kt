package com.example.casaan.ui.dashboard

import android.graphics.Paint
import android.graphics.Typeface
import android.icu.text.AlphabeticIndex
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.casaan.R
import com.example.casaan.databinding.FragmentDashboardBinding
import kotlin.math.pow
import kotlin.math.roundToInt

class MqttItem(
    var label: String,
    var topic: String,
    var decimals: Int,
    var min: Double,
    var max: Double,
    var inverse: Boolean,
    var unit: String,
    var lastwilltopic: String = ""
)

class MqttItems {

    var items = mutableListOf<MqttItem>()

    fun addItem (label: String, topic : String, decimals: Int, min : Double, max :Double, inverse: Boolean, unit: String, lastwilltopic: String = ""){
        val mqttTextViewItem = MqttItem(label, topic, decimals, min, max, inverse, unit, lastwilltopic)
        this.items.add(mqttTextViewItem)
    }

    fun addItem (label: String, topic : String, decimals: Int, min : Int, max :Int, inverse: Boolean, unit: String, lastwilltopic: String = ""){
        val mqttTextViewItem = MqttItem(label, topic, decimals, min.toDouble(), max.toDouble(), inverse, unit, lastwilltopic)
        this.items.add(mqttTextViewItem)
    }

    fun getItemByTopic (topic: String) : MqttItem?
    {
        for (i in this.items)
        {
            if (i.topic == topic) return i
        }
        return null
    }


    fun getItemByNr (nr : Int) : MqttItem
    {
        return this.items[nr]
    }
}


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private val mainActivity by lazy { (activity as com.example.casaan.MainActivity) }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val mqttItems = MqttItems()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        this.mqttAddItem("Elektra", "",0, 120, 140, false, "")
        this.mqttAddItem("Electriciteitsverbruik", "home/ESP_SDM120/power", 0, 400, 1000, false, "Watt", "home/ESP_SDM120/status")
        this.mqttAddItem("Netverbruik", "home/ESP_SMARTMETER/electricity/watt",0, 400, 1000, false, "Watt","home/ESP_SMARTMETER/status")
        this.mqttAddItem("Zonnepanelen", "home/ESP_GROWATT/grid/watt",0, 400, 1000, true, "Watt","home/ESP_WEATHER/status")

        this.mqttAddItem("Apparaten", "",0, 120, 140, false, "")
        this.mqttAddItem("Airco", "home/SONOFF_AIRCO/power",0, 4, 1000, false, "Watt","home/SONOFF_AIRCO/status")
        this.mqttAddItem("TV", "home/SONOFF_TV/power",0, 4, 100, false, "Watt", "home/SONOFF_TV/status")
        this.mqttAddItem("Vaatwasser", "home/SONOFF_DISHWASHER/power",0, 4, 1000, false, "Watt","home/SONFF_DISWASHER/status")
        this.mqttAddItem("Wasmachine", "home/SONOFF_WASHINGMACHINE/power",0, 4, 1000, false, "Watt", "home/SONOFF_WASHINGMACHINE/status")
        this.mqttAddItem("Koelkast", "home/SONOFF_FRIDGE/power", 0,4, 1000, false, "Watt", "home/SONOFF_FRIDGE/status")
        this.mqttAddItem("Espresso", "home/BLITZWOLF_COFFEE/power",0, 4, 1000, false, "Watt", "home/BLITZWOLF_COFFEE/status")
        this.mqttAddItem("Server", "home/SONOFF_SERVER/power",0, 1201, 140, false, "Watt", "home/SONOFF_SERVER/status")
        this.mqttAddItem("Temperatuur", "",0, 120, 140, false, "")
        this.mqttAddItem("Huiskamer", "home/ESP_OPENTHERM/thermostat/temperature",0, 22, 25, false, "°C", "home/ESP_OPENTHERM/status")
        this.mqttAddItem("Slaapkamer Achter", "home/ESP_BEDROOM2/dht22/temperature",0, 20, 22, false, "°C", "home/ESP_BEDROOM2/status")
        this.mqttAddItem("Kantoor", "home/nodered/zigbee/office/temperature",0, 22, 25, false, "°C")
        this.mqttAddItem("Koelkast", "home/nodered/zigbee/kitchen/fridge/temperature",0, 6, 8, false, "°C")
        this.mqttAddItem("Buiten", "home/ESP_WEATHER/temperature",0, 20, 30, false, "°C", "home/ESP_WEATHER/status")
        this.mqttAddItem("Barbeque 1", "home/ESP_BBQ/temperature/0",0, 40, 70, false, "°C", "home/ESP_BBQ/status")
        this.mqttAddItem("Barbeque 2", "home/ESP_BBQ/temperature/1",0, 40, 70, false, "°C", "home/ESP_BBQ/status")

        val table = root.findViewById<TableLayout>(R.id.tableLayout)
        for (mqttItem in this.mqttItems.items) {
            val row = android.widget.TableRow(context)
            val textView1 = TextView(context)
            val textView2 = TextView(context)

            var rowtopmargin = 0

            textView1.text = mqttItem?.label
            textView1.width = 600
            textView2.width = 300
            if (mqttItem.topic == "")
            {
                rowtopmargin = 20
                textView2.text = ""
                textView1.textSize = 18f
                textView1.setTypeface(null, Typeface.BOLD)
            }
            else textView2.text = ("-" + " " + mqttItem?.unit)
            textView2.tag = mqttItem?.topic
            textView2.gravity = Gravity.END


            row.addView(textView1)
            row.addView(textView2)

            val param = textView2.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(10,0,0,0)

            table.addView(row)
            val paramrow = row.layoutParams as ViewGroup.MarginLayoutParams
            paramrow.setMargins(0, rowtopmargin,0,0)
        }



        this.mqttupdate(null)

        return root
    }

    fun getIntance() {
        return this.getIntance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun mqttAddItem (label: String, topic : String, decimals: Int, min : Int, max :Int, inverse: Boolean, unit: String, lastwilltopic: String = ""){
            this.mqttItems.addItem(label, topic, decimals, min, max, inverse, unit, lastwilltopic)
            mainActivity.mqttClient.subscribe(topic)
        if (lastwilltopic!=="") mainActivity.mqttClient.subscribe(lastwilltopic)

    }

    fun numberFormat(s: String?, decimals: Int): String {
        if (s == null) return "-"
        val f = s.toFloatOrNull()
        var r = s
        if (f !== null) r = ("%.0" + decimals + "f").format(f)
        return r
    }

    fun valueToColor(value: String?, min: Double, max: Double, decimals: Int, inverse: Boolean): Int {
        val fvalue = value?.toDoubleOrNull()
        if (fvalue !== null)
        {
            val factor = 10.0.pow(decimals)

            val lvalue = ((fvalue?:0.0) * factor).roundToInt()
            val lmin = (min * factor).roundToInt()
            val lmax = (max * factor).roundToInt()

            if (lvalue <= lmin) { if (!inverse) return 0xFF00FF00.toInt() else return 0xFFFF0000.toInt() }
            if (lvalue > lmax) { if (inverse) return 0xFF00FF00.toInt() else return 0xFFFF0000.toInt() }
        }
        return 0xFFFFAA00.toInt()
    }

    fun fillTextView(
        textViewId: Int,
        value: String?,
        decimals: Int,
        min: Double,
        max: Double,
        inverse: Boolean = false,
        unit: String = "",
        lastwillvalue: String?
    ) {
        val root: View = binding.root
        val textView = root.findViewById<TextView>(textViewId)
        textView?.text = (numberFormat(value, decimals) + unit)
        textView?.setTextColor(valueToColor(value, min, max, decimals, inverse))
    }

    fun fillTextView(
        textView: TextView?,
        value: String?,
        decimals: Int,
        min: Double,
        max: Double,
        inverse: Boolean = false,
        unit: String = "",
        lastwillvalue: String?
    ) {
        if ((lastwillvalue?.lowercase() == "offline") || (lastwillvalue == null)){
            textView?.text = ("offline")
        }
        else {
            if (value == null)
            {
                textView?.text = ("offline")
            }
            else {
                textView?.text = (numberFormat(value ?: "-", decimals) + " " + unit)
                textView?.setTextColor(valueToColor(value ?: "0", min, max, decimals, inverse))
            }
        }
    }


    fun mqttupdate(topic: String?) {
        val root: View = binding.root

        var value: String

        for (i in this.mqttItems.items)
        {
            if ((i.topic !== "") && (i.lastwilltopic !== "")) fillTextView(
                root.findViewWithTag<TextView>(i.topic),
                mainActivity.mqttmap?.get(i.topic),
                i.decimals,
                i.min,
                i.max,
                i.inverse,
                i.unit,
                mainActivity.mqttmap?.get(i.lastwilltopic))
            if ((i.topic !== "") && (i.lastwilltopic == "")) fillTextView(
                root.findViewWithTag<TextView>(i.topic),
                mainActivity.mqttmap?.get(i.topic),
                i.decimals,
                i.min,
                i.max,
                i.inverse,
                i.unit,
                "")
        }
    }
}