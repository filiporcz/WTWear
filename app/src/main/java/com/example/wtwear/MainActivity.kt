    package com.example.wtwear

    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import androidx.fragment.app.Fragment
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import android.Manifest
    import android.content.pm.PackageManager
    import android.location.Geocoder
    import android.util.Log
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import com.google.android.gms.location.FusedLocationProviderClient
    import com.google.android.gms.location.LocationServices
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.lifecycleScope
    import kotlinx.coroutines.CoroutineExceptionHandler
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import java.util.Locale
    import android.content.Context
    import android.net.ConnectivityManager
    import android.net.NetworkCapabilities
    import android.os.Build
    import androidx.appcompat.app.AlertDialog

    val exceptionHandler = CoroutineExceptionHandler{ _, throwable->
        Log.d("ERROR FROM EXCEPTION HANDLER:", "$throwable")
        throwable.printStackTrace()
    }

    lateinit var userViewModel: UserViewModel
    lateinit var savedLocation: SavedLocation
    lateinit var geocoder: Geocoder

    class MainActivity : AppCompatActivity() {
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        private lateinit var bottomNavigationView: BottomNavigationView

        //change this later accordingly
        //private val hatImages = listOf(R.drawable.hat1_n, R.drawable.hat2_f, R.drawable.hat3_n)
        //private val coatImages = listOf(R.drawable.coat1_n, R.drawable.coat2_f, R.drawable.coat3_n)
        //private val trousersImages = listOf(R.drawable.trousers1_n, R.drawable.trousers2_n, R.drawable.trousers3_n)
        //private val shoesImages = listOf(R.drawable.shoes1_n, R.drawable.shoes2_n, R.drawable.shoes3_n)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val sharedPref = getPreferences(MODE_PRIVATE)
            geocoder = Geocoder(this, Locale.getDefault())
            supportActionBar?.hide()
            userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

            // Check for internet connectivity
            if (!isNetworkAvailable()) {
                showNoInternetDialog()
                return
            }

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            if (//ActivityCompat.checkSelfPermission(
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

                    supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.ui2))
                    supportActionBar?.elevation = 0.0f
                    val addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1,
                    )

                    val cityName = addresses?.get(0)?.subAdminArea
                    val countryCode = addresses?.get(0)?.countryCode
                    supportActionBar?.title = "$cityName, $countryCode"

                    savedLocation = SavedLocation(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        cityName,
                        countryCode
                    )

                    userViewModel.user.observe(this) {
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
                    }
                    // REPLACE THE ONE ON withContext ABOVE WITH THIS IF IT DOES NOT GO PAST LOADING SCREEN
                    Log.d("CHANGING:", "Going to main layout")
                    showMainLayout()

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

            userViewModel.user.observe(this) {
                Log.d("userViewModel Test 2 User:", it.toString())
            }

            //toolBar = findViewById(R.id.toolbar)
            //setSupportActionBar(toolBar)
            supportActionBar?.show()
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

        private fun isNetworkAvailable(): Boolean {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

                return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }

        private fun showNoInternetDialog() {
            AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("The app won't function properly without an internet connection.")
                .setPositiveButton("Exit") { _, _ -> finish() }
                .setCancelable(false)
                .show()
        }

        //private fun fetchLocation() {
        //    //var latitude: Double? = null
        //    //var longitude: Double? = null

        //    if (ActivityCompat.checkSelfPermission(
        //            this,
        //            Manifest.permission.ACCESS_FINE_LOCATION
        //        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        //            this,
        //            Manifest.permission.ACCESS_COARSE_LOCATION
        //        ) != PackageManager.PERMISSION_GRANTED
        //    ) {
        //        ActivityCompat.requestPermissions(
        //            this,
        //            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        //            101
        //        )
        //        return
        //    }
        //    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
        //        if (location != null) {
        //            // Update the tvLocation TextView with the location information
        //            //val locationText = "${location.latitude}, ${location.longitude}"
        //            latitude = location.latitude
        //            longitude = location.longitude
        //        }
        //    }
        //}
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