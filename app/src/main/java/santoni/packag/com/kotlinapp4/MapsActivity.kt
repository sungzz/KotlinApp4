package santoni.packag.com.kotlinapp4

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val USER_LOCATION_REQUEST_CODE = 1000

    private var playerLocation: Location? = null
    private var oldLocationPlayer: Location? = null

    var locationManager: LocationManager? = null
    var locationListener: PlayerLocationListerner? = null

    private var pokemonCharacter: ArrayList<PokemonCharacter> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = PlayerLocationListerner()

        requestLocationPermission()
        initializePokemonCharacters()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }

    // ask permission

    private fun requestLocationPermission() {

        if(Build.VERSION.SDK_INT>=23){

            if(ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                ,USER_LOCATION_REQUEST_CODE)

                return
            }
        }

        accessUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {

        if (requestCode == USER_LOCATION_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                accessUserLocation()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    inner class PlayerLocationListerner: LocationListener {

        constructor(){

            playerLocation = Location("MyProvider")
            playerLocation?.latitude = 1.0
            playerLocation?.longitude = 1.0

        }

        override fun onLocationChanged(updatedLocation: Location?) {

            playerLocation = updatedLocation

        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }


    }

    private fun initializePokemonCharacters() {

        pokemonCharacter.add(PokemonCharacter("Hello, this is c1",
                "I'm powerful", R.drawable.c1, 1.651729,
                31.996134))

        pokemonCharacter.add(PokemonCharacter("Hello, this is c2",
                "I'm powerful", R.drawable.c2, 27.404523,
                29.647654))

        pokemonCharacter.add(PokemonCharacter("Hello, this is c3",
                "I'm powerful", R.drawable.c3, 10.492703,
                10.709112))

        pokemonCharacter.add(PokemonCharacter("Hello, this is c4",
                "I'm powerful", R.drawable.c4, 28.220750,
                1.898764))

    }

    private fun accessUserLocation() {

        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2,2f,locationListener!!)

        val newThread = NewThread()
        newThread.start()

    }

    inner class NewThread: Thread {

        constructor(): super() {

            oldLocationPlayer = Location("MyProvider")
            oldLocationPlayer?.latitude = 0.0
            oldLocationPlayer?.longitude = 0.0

        }

        override fun run() {
            super.run()

            while (true) {

                if (oldLocationPlayer?.distanceTo(playerLocation) == 0f) {

                    continue
                }

               oldLocationPlayer = playerLocation

                try {
                    //user Location MAP
                    runOnUiThread {

                        mMap.clear()

                        //player on map
                        val pLocation = LatLng(playerLocation!!.latitude, playerLocation!!.longitude)
                        mMap.addMarker(MarkerOptions().position(pLocation)
                                .title("Hi, I am the Player")
                                .snippet("Let's go")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.player)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pLocation))

                        //pokemon on map
                        for (pokemonCharacterIndex in 0.until(pokemonCharacter.size)) {

                            // 0, 1, 2, 3
                            var pc = pokemonCharacter[pokemonCharacterIndex]
                            if (pc.isKilled == false) {

                                var pcLocation = LatLng(pc.location!!.latitude, pc.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                        .position(pcLocation)
                                        .title(pc.titleOfPokemon)
                                        .snippet(pc.message)
                                        .icon(BitmapDescriptorFactory.fromResource(pc.iconOfPokemon!!)))

                                if (playerLocation!!.distanceTo(pc.location) < 1) {

                                    Toast.makeText(this@MapsActivity,"${pc.titleOfPokemon} is eliminated",
                                            Toast.LENGTH_SHORT).show()
                                    pc.isKilled = true
                                    pokemonCharacter[pokemonCharacterIndex] = pc
                                }

                            }



                        }

                    }
                    //Thread.sleep(2000)

                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }


        }

    }
}
