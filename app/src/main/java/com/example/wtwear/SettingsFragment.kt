package com.example.wtwear

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.android.material.textfield.TextInputEditText
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationManagerCompat
import android.content.Intent
import android.provider.Settings
import android.net.Uri


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

        val locationSwitch = view.findViewById<SwitchCompat>(R.id.locationSwitch)
        locationSwitch.isChecked = sharedPref?.getBoolean("location", true) == true

        val inputCountry = view.findViewById<TextInputEditText>(R.id.countryText)
        val inputCity = view.findViewById<TextInputEditText>(R.id.cityText)

        var savedInputCountry = sharedPref?.getString("inputCountry", null)
        var savedInputCity = sharedPref?.getString("inputCity", null)

        inputCountry.setText(savedInputCountry)
        inputCity.setText(savedInputCity)

        if (locationSwitch.isChecked) { // During fragment startup
            Log.d("location switch Test", "ON")
            inputCountry.isEnabled = false
            inputCity.isEnabled = false
        } else {
            Log.d("location switch Test", "OFF")
            inputCountry.isEnabled = true
            inputCity.isEnabled = true
        }

        locationSwitch.setOnCheckedChangeListener { _, isChecked -> // During button switch
            if (isChecked) {
                Log.d("location switch Test", "ON")
                inputCountry.isEnabled = false
                inputCity.isEnabled = false
            } else {
                Log.d("location switch Test", "OFF")
                inputCountry.isEnabled = true
                inputCity.isEnabled = true
            }
        }

        val sampleCities = getWorldCities()
        Log.d("Cities Test", sampleCities.toString())

        val unitSwitch = view.findViewById<SwitchCompat>(R.id.unitsSwitch)
        unitSwitch.isChecked = sharedPref?.getString("unit", "metric") == "imperial"

        val genderRadio = view.findViewById<RadioGroup>(R.id.genderRadioGroup)
        val applyButton = view.findViewById<Button>(R.id.applyButton)

        userViewModel.user.observe(viewLifecycleOwner) {
            var latitude = savedLocation.latitude
            var longitude = savedLocation.longitude
            var locationUserInput = true
            savedInputCountry = null
            savedInputCity = null

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

                (activity as AppCompatActivity).supportActionBar?.title =
                    "${savedLocation.city}, ${savedLocation.country}"
                if (!locationSwitch.isChecked
                    and !inputCountry.text.isNullOrEmpty()
                    and !inputCity.text.isNullOrEmpty()
                ) {
                    val matchedCountries = sampleCities.country.mapIndexed { index, value ->
                        if (inputCountry.text.toString() == value) {
                            index
                        } else {
                            null
                        }
                    }.filterNotNull()
                    Log.d("input country matches Test", matchedCountries.toString())

                    val matchedCities = sampleCities.city.mapIndexed { index, value ->
                        if (inputCity.text.toString() == value) {
                            index
                        } else {
                            null
                        }
                    }.filterNotNull()
                    Log.d("input city matches Test", matchedCities.toString())

                    if (matchedCountries.isNotEmpty() and matchedCities.isNotEmpty()) {
                        val matchedBoth = matchedCountries.find { value ->
                            value == matchedCities[0]
                        }
                        Log.d("input country and city match Test", matchedBoth.toString())

                        if (matchedBoth != null) {
                            latitude = sampleCities.latitude[matchedBoth]!!
                            longitude = sampleCities.longitude[matchedBoth]!!

                            locationUserInput = false
                            savedInputCountry = inputCountry.text.toString()
                            savedInputCity = inputCity.text.toString()

                            (activity as AppCompatActivity).supportActionBar?.title =
                                "$savedInputCity, $savedInputCountry"

                            Log.d("new latitude Test", latitude)
                            Log.d("new longitude Test", longitude)
                        }
                    }
                }

                editPref?.apply {
                    putString("gender", gender)

                    if (!unitSwitch.isChecked) {
                        putString("unit", "metric")
                    } else {
                        putString("unit", "imperial")
                    }

                    putBoolean("location", locationUserInput)
                    putString("inputCountry", savedInputCountry)
                    putString("inputCity", savedInputCity)

                    apply()
                }

                userViewModel.initOrUpdate(
                    latitude,
                    longitude,
                    gender
                )
            }
            val notificationSwitch = view.findViewById<SwitchCompat>(R.id.notificationsSwitch)

            // Set the initial state of the notifications switch based on notification permissions
            notificationSwitch.isChecked = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()

            notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Check if notification permission is granted
                    if (!NotificationManagerCompat.from(requireContext())
                            .areNotificationsEnabled()
                    ) {
                        // Request notification permission
                        requestNotificationPermission()
                    }
                } else {
                    revokeNotificationPermission()
                }
            }
        }
        return view
    }

    private fun getWorldCities(): MapSample {
        val csvFile = resources.openRawResource(R.raw.worldcities)
        val rows: List<Map<String, String>> = csvReader().readAllWithHeader(csvFile)

        return MapSample(
            country = rows.map { it["iso2"] }, // 2 letter country code
            city = rows.map { it["city_ascii"] },
            latitude = rows.map { it["lat"] },
            longitude = rows.map { it["lng"] }
        )
    }

    private fun requestNotificationPermission() {
        val notificationManager =
            requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "channel_id"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(notificationChannel)

        // Check if permission is not granted
        if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            // Request notification permission
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            startActivity(intent)
        }
    }
    private fun revokeNotificationPermission() {
        // Check if notification permission is granted
        if (NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            // Revoke notification permission
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", requireContext().packageName, null)
            startActivity(intent)
        }
    }
}

private data class MapSample(
    val country: List<String?>,
    val city: List<String?>,
    val latitude: List<String?>,
    val longitude: List<String?>
)
