package com.example.qspottertest.actividades

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qspottertest.R
import com.example.qspottertest.adapters.AdaptadorInfoMarcador
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var locationManager: LocationManager
    private var myLocation : Location? = null
    private var hasGps = false
    private var santiago = LatLng(-33.4727092,-70.7699144)


    companion object {
        private const val TAG = "Supermercados"
    }

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ) {
            map.isMyLocationEnabled = true
            getLocation()
            val pos = LatLng(myLocation!!.latitude, myLocation!!.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST
            )
        }


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
                getLocation()
                val pos = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
            }
            else {
                Toast.makeText(
                    this,
                    "El usuario ha denegado los permisos de ubicación",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(santiago, 5f))
        getLocationAccess()
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.estilo_silver))
        getSupermercados()
        map.setInfoWindowAdapter(AdaptadorInfoMarcador(this))
    }

    private fun getSupermercados(){
        val userId = intent.getStringExtra("id")
        val db = FirebaseFirestore.getInstance()
        db.collection("Supermercados")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    /*val coder = Geocoder(this)
                    var address: List<Address?>
                    var p1: GeoPoint? = null
                    val strAddress = document.getString("Direccion")
                    address = coder.getFromLocationName(strAddress, 5);
                    val location = address[0]!!
                    p1 = GeoPoint(
                        (location.latitude ),
                        (location.longitude)
                    )
                    db.collection("Supermercados").document(document.id).update("Coordenadas",p1)*/
                    /*db.collection("Supermercados").document(document.id).update("CantAvisoDesabastecimiento",0)
                    db.collection("Supermercados").document(document.id).update("CantAvisoFila",0)*/
                    val geoPoint = document.getGeoPoint("Coordenadas")
                    val lat = geoPoint!!.latitude
                    val lng = geoPoint.longitude
                    val latLng = LatLng(lat, lng)
                    map.addMarker(
                        MarkerOptions().position(latLng).title(document.get("Nombre") as String?)
                            .snippet(
                                "${
                                    document.get(
                                        "Direccion"
                                    )
                                }-${document.id}" as String?
                            )
                    )
                    map.setOnMarkerClickListener { marker ->
                        val nombre = marker.title
                        val datos: List<String> = marker.snippet.split("-")
                        val superDireccion = datos[0].split(",")[0]
                        val idSupermercado = datos[1]
                        val intent = Intent(this, SeleccionarAvisoActivity::class.java).apply {
                            //putExtra("idSupermercado", idSupermercado)
                            putExtra("userId", userId)
                            putExtra("superNombre", nombre)
                            putExtra("superDireccion", superDireccion)
                            putExtra("idSupermercado", idSupermercado)
                        }
                        startActivity(intent)
                        false
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al obtener documentos: ", exception)
            }
    }

    fun onClickFavoritos(view: View){
        val db = FirebaseFirestore.getInstance()
        val userId = intent.getStringExtra("id")
        var favoritos: String
        db.collection("Usuarios").document(userId!!)
            .get().addOnSuccessListener {
                val arreglo = it.get("Favoritos") as? ArrayList<*>
                if(arreglo != null)
                    favoritos = arreglo.joinToString(",")
                else
                    favoritos = ""
                startActivity(
                    Intent(this, FavoritosActivity::class.java).putExtra("userID", userId).putExtra(
                        "favoritos",
                        favoritos
                    )
                )
            }
            .addOnFailureListener { exception ->
                Log.w("Productos", "Error al obtener documentos: ", exception)
            }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (hasGps){
            Log.d("CodeAndroidLocation", "hasGps")
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 0f
            ) { location -> myLocation = location }
            val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null)
                myLocation = localGpsLocation
        }else
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

    }



}