    package com.example.wtwear

    import android.graphics.Color
    import android.os.Bundle
    import android.os.Handler
    import android.view.View
    import android.widget.EditText
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.fragment.app.Fragment
    import com.android.volley.Request
    import com.android.volley.RequestQueue
    import com.android.volley.toolbox.StringRequest
    import com.android.volley.toolbox.Volley
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import org.json.JSONArray
    import org.json.JSONException
    import org.json.JSONObject
    import java.text.DecimalFormat
    import io.github.jan.supabase.createSupabaseClient
    import io.github.jan.supabase.postgrest.Postgrest
    import android.Manifest
    import android.annotation.SuppressLint
    import android.content.pm.PackageManager
    import android.location.LocationManager
    import android.util.Log
    import android.widget.Button
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.core.app.ActivityCompat
    import com.google.android.gms.common.api.ResolvableApiException
    import com.google.android.gms.location.LocationRequest
    import com.google.android.gms.location.LocationSettingsRequest
    import com.google.android.gms.location.Priority
    import com.google.android.gms.tasks.CancellationTokenSource
    import com.google.android.gms.location.FusedLocationProviderClient
    import com.google.android.gms.location.LocationServices
    import android.widget.ImageView
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.lifecycleScope
    import com.example.wtwear.backend.data.User
    import com.example.wtwear.backend.data.fetchWSData
    import com.example.wtwear.backend.logic.matchTemp
    import kotlinx.coroutines.CoroutineExceptionHandler
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.GlobalScope
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext

    val exceptionHandler = CoroutineExceptionHandler{ _, throwable->
        Log.d("ERROR FROM EXCEPTION HANDLER:", "$throwable")
        throwable.printStackTrace()
    }

    lateinit var userViewModel: UserViewModel

    class MainActivity : AppCompatActivity() {
        private var latitude: Double? = null
        private var longitude: Double? = null

        private lateinit var etCity: EditText
        private lateinit var etCountry: EditText
        private lateinit var tvResult: TextView
        private val url = "https://api.openweathermap.org/data/2.5/weather"
        private val appid = "32e60a591b19acc174f0c20a4610964f"
        private val df = DecimalFormat("#.##")

        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        private lateinit var locationText: String

        private lateinit var bottomNavigationView: BottomNavigationView

        //change this later accordingly
        //private val hatImages = listOf(R.drawable.hat1_n, R.drawable.hat2_f, R.drawable.hat3_n)
        //private val coatImages = listOf(R.drawable.coat1_n, R.drawable.coat2_f, R.drawable.coat3_n)
        //private val trousersImages = listOf(R.drawable.trousers1_n, R.drawable.trousers2_n, R.drawable.trousers3_n)
        //private val shoesImages = listOf(R.drawable.shoes1_n, R.drawable.shoes2_n, R.drawable.shoes3_n)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val sharedPref = getPreferences(MODE_PRIVATE)
            userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            while (//ActivityCompat.checkSelfPermission(
            //    this,
            //    Manifest.permission.ACCESS_FINE_LOCATION
            //) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    101
                )
            }

            Log.d("CHANGING", "Switching to loading screen")
            setContentView(R.layout.loading_screen)

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userViewModel.initOrUpdate(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        sharedPref.getString("gender", null)
                    )

                    userViewModel.user.observe(this, Observer {
                        Log.d("userViewModel Test User:", it.toString())

                        lifecycleScope.launch(Dispatchers.IO + exceptionHandler) {
                            Log.d("userViewModel test assign:", "initializing weather")
                            userViewModel.weather.postValue(it.weatherInfo())
                            Log.d("userViewModel test assign:", "initializing clothes")
                            userViewModel.clothes.postValue(it.clothes())
                            Log.d("userViewModel test assign:", "initializing clothes successful")

                            withContext(Dispatchers.Main) {
                                //
                                Log.d("CHANGING:", "Going to main layout")
                                showMainLayout()
                            }
                        }
                    })
                    // REPLACE THE ONE ON withContext ABOVE WITH THIS IF IT DOES NOT GO PAST LOADING SCREEN
                    //Log.d("CHANGING:", "Going to main layout")
                    //showMainLayout()

                    Log.d(
                        "lastLocation RESULT:",
                        "LATITUDE: ${location.latitude}, LONGITUDE: ${location.longitude}"
                    )
                }
            }

            //val button = findViewById<Button>(R.id.button)

            //button.setOnClickListener {
            //    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            //        if (location != null) {
            //             userViewModel.initOrUpdate(
            //                location.latitude.toString(),
            //                location.longitude.toString(),
            //                sharedPref.getString("gender", null)
            //            )

            //            userViewModel.user.observe(this, Observer {
            //                Log.d("userViewModel Test User:", it.toString())

            //                lifecycleScope.launch(Dispatchers.IO + exceptionHandler) {
            //                    Log.d("userViewModel test assign:", "initializing weather")
            //                    userViewModel.weather.postValue(it.weatherInfo())
            //                    Log.d("userViewModel test assign:", "initializing clothes")
            //                    userViewModel.clothes.postValue(it.clothes())
            //                    Log.d("userViewModel test assign:", "initializing clothes successful")

            //                    withContext(Dispatchers.Main) {
            //                        //
            //                        Log.d("CHANGING:", "Going to main layout")
            //                        showMainLayout()
            //                    }
            //                }
            //            })
            //            // REPLACE THE ONE ON withContext ABOVE WITH THIS IF IT DOES NOT GO PAST LOADING SCREEN
            //            //Log.d("CHANGING:", "Going to main layout")
            //            //showMainLayout()

            //            Log.d(
            //                "lastLocation RESULT:",
            //                "LATITUDE: ${location.latitude}, LONGITUDE: ${location.longitude}"
            //            )
            //        }
            //    }
            //}

        }

        private fun showMainLayout() {
            setContentView(R.layout.activity_main)

            Log.d("CHANGING:", "Now on main layout")

            //val sharedPref = getPreferences(MODE_PRIVATE)
            //Log.d("Current Unit Test:", sharedPref.getString("unit", "metric").toString())
            //fetchLocation()
            //Log.d("LATITUDE:", latitude.toString())
            //Log.d("LONGITUDE:", longitude.toString())
            //lifecycleScope.launch(Dispatchers.IO + exceptionHandler) {
            //    val test = fetchWSData("Bristol", "UK")
            //    Log.d("Weather", test.toString())
            //    val test2 = matchTemp(10)
            //    Log.d("Temp", test2.toString())
            //    Log.d("userViewModel TEST 2:", userViewModel.getData().toString())
            //    Log.d("userViewModel Weather TEST 2:", userViewModel.getData().weatherInfo().toString())
            //}

            userViewModel.user.observe(this, Observer {
                Log.d("userViewModel Test 2 User:", it.toString())
            })

            bottomNavigationView = findViewById(R.id.bottomNavigationView)

            bottomNavigationView.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.home -> {
                        replaceFragment(HomeFragment())
                        true
                    }

                    R.id.weather -> {
                        replaceFragment(WeatherFragment())
                        true
                    }

                    R.id.settings -> {
                        replaceFragment(SettingsFragment())
                        true
                    }

                    else -> false
                }
            }
            replaceFragment(HomeFragment())
        }

        private fun replaceFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
        }

        private fun fetchLocation() {
            //var latitude: Double? = null
            //var longitude: Double? = null

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
                return
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // Update the tvLocation TextView with the location information
                    //val locationText = "${location.latitude}, ${location.longitude}"
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        }

        //private fun retrievePreferences() {
        //    val gender = sharedPref.getString("gender", null)
        //}

        private fun setOnClickListenerForImage(
            button: ImageView,
            middleImage: ImageView,
            imageList: List<Int>
        ) {
            button.setOnClickListener {
                // Get the current index of the middle image
                val currentIndex = imageList.indexOf(middleImage.tag as? Int ?: 0)

                // Calculate the new index based on the button pressed
                val newIndex = when (button.id) {
                    R.id.leftButton1, R.id.leftButton2, R.id.leftButton3, R.id.leftButton4 -> {
                        (currentIndex - 1 + imageList.size) % imageList.size
                    }

                    R.id.rightButton1, R.id.rightButton2, R.id.rightButton3, R.id.rightButton4 -> {
                        (currentIndex + 1) % imageList.size
                    }

                    else -> currentIndex
                }

                // Update the middle image with the new resource
                middleImage.setImageResource(imageList[newIndex])

                // Save the new index as a tag for the middle image
                middleImage.tag = newIndex
            }
        }
    }

        /*
        fun getWeatherDetails(view: View) {
            val city = etCity.text.toString().trim()
            val country = etCountry.text.toString().trim()

            val tempUrl = if (country.isNotEmpty()) {
                "$url?q=$city,$country&appid=$appid"
            } else {
                "$url?q=$city&appid=$appid"
            }

            if (city.isEmpty()) {
                tvResult.setText(R.string.error_empty_city)
            } else {
                val stringRequest = StringRequest(
                    Request.Method.POST,
                    tempUrl,
                    { response ->
                        // onResponse logic
                        var output = ""
                        try {
                            val jsonResponse = JSONObject(response)
                            val jsonArray = jsonResponse.getJSONArray("weather")
                            val jsonObjectWeather = jsonArray.getJSONObject(0)
                            val description = jsonObjectWeather.getString("description")
                            val jsonObjectMain = jsonResponse.getJSONObject("main")
                            val temp = jsonObjectMain.getDouble("temp") - 273.15
                            val feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15
                            val pressure = jsonObjectMain.getInt("pressure")
                            val humidity = jsonObjectMain.getInt("humidity")
                            val jsonObjectWind = jsonResponse.getJSONObject("wind")
                            val wind = jsonObjectWind.getString("speed")
                            val jsonObjectClouds = jsonResponse.getJSONObject("clouds")
                            val clouds = jsonObjectClouds.getString("all")
                            val jsonObjectSys = jsonResponse.getJSONObject("sys")
                            val countryName = jsonObjectSys.getString("country")
                            val cityName = jsonResponse.getString("name")
                            tvResult.setTextColor(Color.rgb(68, 134, 199))
                            output += "Current weather of $cityName ($countryName)" +
                                    "\n Temp: ${df.format(temp)} °C" +
                                    "\n Feels Like: ${df.format(feelsLike)} °C" +
                                    "\n Humidity: $humidity%" +
                                    "\n Description: $description" +
                                    "\n Wind Speed: $wind m/s (meters per second)" +
                                    "\n Cloudiness: $clouds%" +
                                    "\n Pressure: $pressure hPa"
                            tvResult.text = output
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    { error ->
                        // onErrorResponse logic
                        Toast.makeText(
                            applicationContext,
                            error.toString().trim { it <= ' ' },
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                val requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
                requestQueue.add(stringRequest)
            }
        }
         */