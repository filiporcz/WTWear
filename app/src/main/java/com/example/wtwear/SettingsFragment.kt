package com.example.wtwear

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
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

        when (sharedPref?.getString("gender", null)) {
            "m" -> {
                val maleButton = view.findViewById<RadioButton>(R.id.maleRadioButton)
                maleButton.isChecked = true
            }
            "f" -> {
                val femaleButton = view.findViewById<RadioButton>(R.id.femaleRadioButton)
                femaleButton.isChecked = true
            }
            else -> {
                val neutralButton = view.findViewById<RadioButton>(R.id.neutralRadioButton)
                neutralButton.isChecked = true
            }
        }

        val unitSwitch = view.findViewById<SwitchCompat>(R.id.unitsSwitch)
        unitSwitch.isChecked = sharedPref?.getString("unit", "metric") == "imperial"

        val genderRadio = view.findViewById<RadioGroup>(R.id.genderRadioGroup)
        val applyButton = view.findViewById<Button>(R.id.applyButton)

        userViewModel.user.observe(viewLifecycleOwner) {
            val city = it.city
            val country = it.country

            applyButton.setOnClickListener {
                val checkedGender = genderRadio.checkedRadioButtonId
                val genderButton = view.findViewById<Button>(checkedGender)

                val gender = when (genderButton.id) {
                    R.id.maleRadioButton -> {
                        "m"
                    }
                    R.id.femaleRadioButton -> {
                        "f"
                    }
                    else -> {
                        null
                    }
                }

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