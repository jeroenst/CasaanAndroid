package com.example.casaan.ui.dashboard

import android.graphics.Paint
import android.icu.text.AlphabeticIndex
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.casaan.R
import com.example.casaan.databinding.FragmentDashboardBinding

class MqttItem {
    var label : String = ""
    var topic : String = ""
    var decimals : Int = 0
    var min : Double = Double.NaN
    var max : Double = Double.NaN
    var inverse : Boolean = false
    var unit : String = ""

    constructor (label: String, topic : String, decimals: Int, min : Double, max :Double, inverse: Boolean, unit: String){
        this.label = label
        this.topic = topic
        this.decimals = decimals
        this.min = min
        this.max = max
        this.inverse = inverse
        this.unit = unit
    }
}

class MqttItems {

    var items = mutableListOf<MqttItem>()

    fun addItem (label: String, topic : String, decimals: Int, min : Double, max :Double, inverse: Boolean, unit: String){
        var mqttTextViewItem = MqttItem(label, topic, decimals, min, max, inverse, unit)
        this.items.add(mqttTextViewItem)
    }

    fun addItem (label: String, topic : String, decimals: Int, min : Int, max :Int, inverse: Boolean, unit: String){
        var mqttTextViewItem = MqttItem(label, topic, decimals, min.toDouble(), max.toDouble(), inverse, unit)
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


    fun getItemByNr (nr : Int) : MqttItem?
    {
        return this.items[nr]
    }
}


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private val mqttmap by lazy { (activity as com.example.casaan.MainActivity).mqttmap }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val mqttItems = MqttItems()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        this.mqttItems.addItem("Electriciteitsverbruik", "home/ESP_SDM120/power", 0, 400, 1000, false, "Watt")
        this.mqttItems.addItem("Netverbruik", "home/ESP_SMARTMETER/electricity/watt",0, 400, 1000, false, "Watt")
        this.mqttItems.addItem("Zonnepanelen", "home/ESP_GROWATT/grid/watt",0, 400, 1000, true, "Watt")
        this.mqttItems.addItem("", "",0, 120, 140, false, "")
        this.mqttItems.addItem("Airco", "home/SONOFF_AIRCO/power",0, 4, 1000, false, "Watt")
        this.mqttItems.addItem("TV", "home/SONOFF_TV/power",0, 4, 100, false, "Watt")
        this.mqttItems.addItem("Vaatwasser", "home/SONOFF_DISHWASHER/power",0, 4, 1000, false, "Watt")
        this.mqttItems.addItem("Wasmachine", "home/SONOFF_WASHINGMACHINE/power",0, 4, 1000, false, "Watt")
        this.mqttItems.addItem("Koelkast", "home/SONOFF_REFRIDGERATOR/power", 0,4, 1000, false, "Watt")
        this.mqttItems.addItem("Espresso", "home/BLITZWOLF_COFFEE/power",0, 4, 1000, false, "Watt")
        this.mqttItems.addItem("Server", "home/SONOFF_SERVER/power",0, 1201, 140, false, "Watt")
        this.mqttItems.addItem("", "",0, 120, 140, false, "")
        this.mqttItems.addItem("Barbeque 1", "home/ESP_BBQ/temperature/0",0, 20, 70, false, "°C")
        this.mqttItems.addItem("Barbeque 2", "home/ESP_BBQ/temperature/1",0, 20, 70, false, "°C")

        val table = root.findViewById<TableLayout>(R.id.tableLayout)
        for (mqttItem in this.mqttItems.items) {
            val row = android.widget.TableRow(context)
            val textView1 = TextView(context)
            val textView2 = TextView(context)

            textView1.text = mqttItem?.label
            textView1.width = 600
            textView2.width = 300
            if (mqttItem.topic == "") textView2.text = ""
            else textView2.text = "-" + " " + mqttItem?.unit
            textView2.tag = mqttItem?.topic
            textView2.gravity = Gravity.RIGHT


            row.addView(textView1)
            row.addView(textView2)

            val param = textView2.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(10,0,0,0)

            table.addView(row)
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

    fun numberFormat(s: String?, decimals: Int): String {
        if (s == null) return "-"
        var f = s.toFloatOrNull()
        var r = s
        if (f !== null) r = ("%.0" + decimals + "f").format(f)
        return r
    }

    fun valueToColor(value: String?, min: Double, max: Double, inverse: Boolean): Int {
        val fvalue = value?.toFloatOrNull()
        if (fvalue !== null) {
            if (fvalue < min) if (!inverse) return 0xFF00FF00.toInt() else return 0xFFFF0000.toInt()
            if (fvalue > max) if (inverse) return 0xFF00FF00.toInt() else return 0xFFFF0000.toInt()
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
        unit: String = ""
    ) {
        val root: View = binding.root
        val textView = root.findViewById<TextView>(textViewId)
        textView?.text = numberFormat(value, decimals) + unit
        textView?.setTextColor(valueToColor(value, min, max, inverse))
    }

    fun fillTextView(
        textView: TextView?,
        value: String?,
        decimals: Int,
        min: Double,
        max: Double,
        inverse: Boolean = false,
        unit: String = ""
    ) {
        textView?.text = numberFormat(value?:"-", decimals) + " " + unit
        textView?.setTextColor(valueToColor(value?:"0", min, max, inverse))
    }


    fun mqttupdate(topic: String?) {
        val root: View = binding.root

        var value: String

        for (i in this.mqttItems.items)
        {
            if (i.topic !== "") fillTextView(
                root.findViewWithTag<TextView>(i.topic),
                mqttmap?.get(i.topic),
                i.decimals,
                i.min,
                i.max,
                i.inverse,
                i.unit)
        }
/*        root.findViewById<ProgressBar>(R.id.progressBar).max = 3000
        root.findViewById<ProgressBar>(R.id.progressBar).progress =
            (mqttmap.get("home/ESP_GROWATT/grid/watt")?.toFloatOrNull() ?: 0).toInt()
        root.findViewById<ProgressBar>(R.id.progressBar).progressDrawable.setColorFilter(
            valueToColor(mqttmap.get("home/ESP_GROWATT/grid/watt"), 400.0, 1000.0, true),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
*/
    }
}