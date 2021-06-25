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
    private val mqttmap by lazy { (activity as com.example.casaan.MainActivity).mqttmap }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val buttonTV = root.findViewById(R.id.buttonTV) as Button
        buttonTV.setOnClickListener() {
            mqttClient.publish("home/setscene/livingroom", "tvonly")
        }

        val buttonEvening = root.findViewById (R.id.buttonEvening) as Button
        buttonEvening.setOnClickListener() {
            mqttClient.publish("home/setscene/livingroom", "evening")
        }

        val buttonDinner = root.findViewById (R.id.buttonDinner) as Button
        buttonDinner.setOnClickListener() {
            mqttClient.publish("home/setscene/livingroom", "diner")
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

        mqttupdate(null)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun mqttupdate(topic : String?)
    {
        val root: View = binding.root
        try {
            val offcolor = 0xFF999999.toInt()
            val oncolor = 0xFF00FF00.toInt()

            var gardenscene = mqttmap.get("home/scene/garden")

              var gardenoncolor = offcolor
                var gardenfencecolor = offcolor
                var gardenoffcolor = offcolor
                if (gardenscene == "on") gardenoncolor = oncolor
                if (gardenscene == "fenceon") gardenfencecolor = oncolor
                if (gardenscene == "off") gardenoffcolor = oncolor

                root.findViewById<View>(R.id.buttonGardenOn)?.setBackgroundColor(gardenoncolor)
                root.findViewById<View>(R.id.buttonGardenFence)?.setBackgroundColor(
                    gardenfencecolor
                )
                root.findViewById<View>(R.id.buttonGardenOff)?.setBackgroundColor(gardenoffcolor)

                var livingroomscene = mqttmap.get("home/scene/livingroom")
                var livingroomtvcolor = offcolor
               var livingroomeveningcolor = offcolor
                var livingroomdinnercolor = offcolor
                var livingroombrightcolor = offcolor
                var livingroomoffcolor = offcolor
                if (livingroomscene == "tvonly") livingroomtvcolor = oncolor
                if (livingroomscene == "evening") livingroomeveningcolor = oncolor
                if (livingroomscene == "diner") livingroomdinnercolor = oncolor
                if (livingroomscene == "bright") livingroombrightcolor = oncolor
                if (livingroomscene == "off") livingroomoffcolor = oncolor

                root.findViewById<View>(R.id.buttonTV)?.setBackgroundColor(livingroomtvcolor)
                root.findViewById<View>(R.id.buttonEvening)?.setBackgroundColor(livingroomeveningcolor)
                root.findViewById<View>(R.id.buttonDinner)?.setBackgroundColor(livingroomdinnercolor)
                root.findViewById<View>(R.id.buttonBright)?.setBackgroundColor(
                    livingroombrightcolor
                )
                root.findViewById<View>(R.id.buttonOff)?.setBackgroundColor(livingroomoffcolor)

            if ((livingroomscene == "off") && (gardenscene == "off")) {
                root.findViewById<View>(R.id.buttonAllOff)?.setBackgroundColor(oncolor)
            } else {
                root.findViewById<View>(R.id.buttonAllOff)?.setBackgroundColor(offcolor)
            }
        }
        catch (e: Exception)
        {
            android.util.Log.e("HomeFragment", e.toString())
        }
    }
}