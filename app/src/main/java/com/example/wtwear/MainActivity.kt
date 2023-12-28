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
    import com.example.wtwear.databinding.ActivityMainBinding
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





    val supabase = createSupabaseClient(
        supabaseUrl = "https://lwalmarsdlzmzzuvseus.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx3YWxtYXJzZGx6bXp6dXZzZXVzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDEwMTA2NzAsImV4cCI6MjAxNjU4NjY3MH0.pceUzcBcSClI84-1hOV9uvwwGqPTMYcU9Go6mGKaMsQ"
    ) {
        install(Postgrest)
    }
    //val url = BuildConfig.SUPABASE_URL
    //val apiKey = BuildConfig.SUPABASE_ANON_KEY
    class MainActivity : AppCompatActivity() {
        private lateinit var etCity: EditText
        private lateinit var etCountry: EditText
        private lateinit var tvResult: TextView
        private val url = "https://api.openweathermap.org/data/2.5/weather"
        private val appid = "32e60a591b19acc174f0c20a4610964f"
        private val df = DecimalFormat("#.##")

        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        private lateinit var tvLocation: TextView

        private lateinit var bottomNavigationView: BottomNavigationView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.loading_screen)
            // Delay switching to the main layout
            Handler().postDelayed({
                showMainLayout()
            }, 3000) // 3000 milliseconds (3 seconds)

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }

        private fun showMainLayout() {
            setContentView(R.layout.activity_main)

            tvLocation = findViewById(R.id.tvLocation)

            val btnGetLocation = findViewById<Button>(R.id.btn_get_location)
            btnGetLocation.setOnClickListener {
                fetchLocation()
            }

            etCity = findViewById(R.id.etCity)
            etCountry = findViewById(R.id.etCountry)
            tvResult = findViewById(R.id.tvResult)

            bottomNavigationView = findViewById(R.id.bottomNavigationView)

            bottomNavigationView.setOnItemSelectedListener { menuItem ->
                when(menuItem.itemId){
                    R.id.home -> {
                        replaceFragment(HomeFragment())
                        true
                    }
                    R.id.weather -> {
                        replaceFragment(WeatherFragment())
                        true
                    }
                    R.id.profile -> {
                        replaceFragment(ProfileFragment())
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

        private fun replaceFragment(fragment: Fragment){
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
        }

        private fun fetchLocation() {
            val task = fusedLocationProviderClient.lastLocation
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
                return
            }
            task.addOnSuccessListener {
                if (it != null) {
                    val locationText = "Latitude: ${it.latitude}, Longitude: ${it.longitude}"
                    tvLocation.text = locationText
                }
            }
        }

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
    }