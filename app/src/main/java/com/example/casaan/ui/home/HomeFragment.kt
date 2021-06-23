package com.example.casaan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.casaan.MainActivity
import com.example.casaan.R
import com.example.casaan.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mqttClient by lazy { (activity as com.example.casaan.MainActivity).mqttClient }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val buttonEvening = root.findViewById (R.id.buttonEvening) as Button
        buttonEvening.setOnClickListener() {
            mqttClient.publish("home/setscene/livingroom", "evening")
        }

        val buttonTV = root.findViewById(R.id.buttonTV) as Button
        buttonTV.setOnClickListener() {
            mqttClient.publish("home/setscene/livingroom", "tvonly")
        }

        val buttonBright = root.findViewById(R.id.buttonBright) as Button
        buttonBright.setOnClickListener() {
            mqttClient.publish("home/setscene/livingroom", "bright")
        }

        val buttonOff = root.findViewById(R.id.buttonOff) as Button
        buttonOff.setOnClickListener() {
            mqttClient.publish("home/setscene/livingroom", "off")
        }

        val buttonGardenOn = root.findViewById(R.id.buttonGardenOn) as Button
        buttonGardenOn.setOnClickListener() {
            mqttClient.publish("home/setscene/garden", "on")
        }

        val buttonGardenFence = root.findViewById(R.id.buttonGardenFence) as Button
        buttonGardenFence.setOnClickListener() {
            mqttClient.publish("home/setscene/garden", "fenceon")
        }

        val buttonGardenOff = root.findViewById(R.id.buttonGardenOff) as Button
        buttonGardenOff.setOnClickListener() {
            mqttClient.publish("home/setscene/garden", "off")
        }

        val buttonAllOff = root.findViewById(R.id.buttonAllOff) as Button
        buttonAllOff.setOnClickListener() {
            mqttClient.publish("home/setscene/livingroom", "off")
            mqttClient.publish("home/setscene/garden", "off")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}