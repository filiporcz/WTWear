package com.example.wtwear

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.widget.SwitchCompat

class SettingsFragment : Fragment() {

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val editPref = sharedPref?.edit()


        val genderRadio = view.findViewById<RadioGroup>(R.id.genderRadioGroup)
        //val checkedGender = genderRadio.checkedRadioButtonId
        //val genderButton = view.findViewById<Button>(checkedGender)

        val applyButton = view.findViewById<Button>(R.id.applyButton)

        userViewModel.user.observe(viewLifecycleOwner) {
            val city = it.city
            val country = it.country

            var gender: String? = null
            applyButton.setOnClickListener {
                val checkedGender = genderRadio.checkedRadioButtonId
                val genderButton = view.findViewById<Button>(checkedGender)

                if (genderButton.text == "Male") {
                    gender = "m"
                } else if (genderButton.text == "Female") {
                    gender = "f"
                }

                val unitSwitch = view.findViewById<SwitchCompat>(R.id.unitsSwitch)

                editPref?.apply {
                    putString("gender", gender)

                    if (!unitSwitch.isChecked) {
                        putString("unit", "metric")
                    } else {
                        putString("unit", "imperial")
                    }

                    apply()
                }

                userViewModel.initOrUpdate(
                    city,
                    country,
                    gender
                )
            }
        }
        return view
    }
}