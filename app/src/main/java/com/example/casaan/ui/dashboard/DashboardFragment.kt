package com.example.casaan.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.casaan.R
import com.example.casaan.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private val mqttmap by lazy { (activity as com.example.casaan.MainActivity).mqttmap }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

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

    fun numberFormat(s: String?, decimals: Int): String? {
        if (s == null) return "-"
        var f = s.toFloatOrNull()
        var r = s
        if (f !== null) r =  ("%.0"+decimals+"f").format(f)
        return r
    }

    fun mqttupdate(topic : String?)
    {
        val root: View = binding.root
        root.findViewById<TextView>(R.id.textViewSmartmeterWatt)?.text = numberFormat(mqttmap.get("home/ESP_SMARTMETER/electricity/watt"),0) + " Watt"
        root.findViewById<TextView>(R.id.textViewPowermeterWatt)?.text = numberFormat(mqttmap.get("home/ESP_SDM120/power"), 0) + " Watt"
        root.findViewById<TextView>(R.id.textViewSolarPanelsWatt)?.text = numberFormat(mqttmap.get("home/ESP_GROWATT/grid/watt"),0) + " Watt"

        root.findViewById<TextView>(R.id.textViewAircoWatt)?.text = numberFormat(mqttmap.get("home/SONOFF_AIRCO/power"),0) + " Watt"
        root.findViewById<TextView>(R.id.textViewTVWatt)?.text = numberFormat(mqttmap.get("home/SONOFF_TV/power"),0) + " Watt"
    }
}