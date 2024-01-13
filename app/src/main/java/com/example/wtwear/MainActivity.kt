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
            while (
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
                    Log.d(
                        "lastLocation RESULT:",
                        "LATITUDE: ${location.latitude}, LONGITUDE: ${location.longitude}"
                    )
                } else {
                    // Handle the case where location is null
                    Log.e("LOCATION ERROR:", "Location is null")
                    showMainLayout()
                }
            }
        }

        private fun showMainLayout() {
            setContentView(R.layout.activity_main)

            Log.d("CHANGING:", "Now on main layout")

            userViewModel.user.observe(this) {
                Log.d("userViewModel Test 2 User:", it.toString())
            }
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

            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }

        private fun showNoInternetDialog() {
            AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("The app won't function properly without an internet connection.")
                .setPositiveButton("Exit") { _, _ -> finish() }
                .setCancelable(false)
                .show()
        }
    }