package com.example.qspottertest.actividades


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qspottertest.R
import com.example.qspottertest.adapters.AdaptadorProductos
import com.example.qspottertest.adapters.AdaptadorRangos
import com.example.qspottertest.datas.InfoItemsDesabastecimiento
import com.example.qspottertest.datas.InfoItemsFila
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import com.google.type.LatLng
import kotlinx.android.synthetic.main.activity_seleccionar_aviso.*


class SeleccionarAvisoActivity : AppCompatActivity() {


    private lateinit var adapRang: AdaptadorRangos
    private lateinit var adapProd: AdaptadorProductos
    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var pathAvisosDesabasteciemiento: CollectionReference
    private lateinit var pathAvisosFila: CollectionReference
    private lateinit var idSupermercado: String
    private lateinit var superNombre : String
    private lateinit var superDireccion: String
    private lateinit var locationManager: LocationManager
    private var locationGps : Location? = null
    private var hasGps = false
    private var fav = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_aviso)
        this.setFinishOnTouchOutside(true)
        superNombre = intent.getStringExtra("superNombre")!!
        superDireccion = intent.getStringExtra("superDireccion")!!
        idSupermercado = intent.getStringExtra("idSupermercado")!!

        pathAvisosFila =db.collection("AvisoFila")
        pathAvisosDesabasteciemiento = db.collection("AvisoDesabastecimiento")

        setUpRecyclerViewFila()
        setUpRecyclerViewDesabastecimiento()
        setUpFavorito()
        getLocation()

        nombSup.text=superNombre
        dirSup.text=superDireccion

    }

    fun setUpRecyclerViewFila(){

        val query: Query =  pathAvisosFila.whereEqualTo("idSupermercado",idSupermercado).orderBy("hora", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<InfoItemsFila> = FirestoreRecyclerOptions.Builder<InfoItemsFila>()
            .setQuery(query, InfoItemsFila::class.java)
            .build()
        adapRang = AdaptadorRangos(firestoreRecyclerOptions)

        infoRangos.layoutManager = LinearLayoutManager(this)
        infoRangos.adapter = adapRang
        infoRangos.isNestedScrollingEnabled = true
    }

    fun setUpRecyclerViewDesabastecimiento(){

        val query: Query =  pathAvisosDesabasteciemiento.whereEqualTo("idSupermercado",idSupermercado).orderBy("hora", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<InfoItemsDesabastecimiento> = FirestoreRecyclerOptions.Builder<InfoItemsDesabastecimiento>()
            .setQuery(query, InfoItemsDesabastecimiento::class.java)
            .build()
        adapProd = AdaptadorProductos(firestoreRecyclerOptions)
        adapProd.onDataChanged()

        infoProd.layoutManager = LinearLayoutManager(this)
        infoProd.adapter = adapProd
        infoProd.isNestedScrollingEnabled = true
    }

    fun setUpFavorito(){
        val userId = intent.getStringExtra("userId")
        val ref = db.collection("Usuarios").document(userId!!)
        ref.get().addOnSuccessListener {
            val favoritos = it.get("Favoritos") as? ArrayList<*>
            if(!it.exists()) {
                val data = hashMapOf("Favoritos" to listOf<String>())
                ref.set(data)
            }
            if (favoritos != null && favoritos.contains(idSupermercado)){
                ivFav.setColorFilter(
                    ContextCompat.getColor(this, R.color.favorito),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                fav = 1
            }else {
                ivFav.setColorFilter(
                    ContextCompat.getColor(this, R.color.noFavorito),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                fav = 0
            }
        }
    }


    fun onClickPubAvisoFila(view: View) {
        db.collection("Supermercados").document(idSupermercado)
            .get()
            .addOnSuccessListener {
                val geoPointSupermercado = it.getGeoPoint("Coordenadas")
                val locationSupermercado = Location("")
                locationSupermercado.latitude = geoPointSupermercado!!.latitude
                locationSupermercado.longitude = geoPointSupermercado.longitude
                val distanceInMeters = locationGps!!.distanceTo(locationSupermercado)
                if (distanceInMeters < 1000) {
                    val userId = intent.getStringExtra("userId")
                    val intent = Intent(this, PublicarAvisoFilaActivity::class.java).apply {
                        putExtra("idSupermercado", idSupermercado)
                        putExtra("userId", userId)
                    }
                    startActivityForResult(intent,1)
                }else
                    Toast.makeText(this, "Esta muy lejos para publicar un aviso de fila", Toast.LENGTH_LONG).show()
            }
    }

    fun onClickPubAvisoDesa(view: View) {
        db.collection("Supermercados").document(idSupermercado)
            .get()
            .addOnSuccessListener {
                val geoPointSupermercado = it.getGeoPoint("Coordenadas")
                val locationSupermercado = Location("")
                locationSupermercado.latitude = geoPointSupermercado!!.latitude
                locationSupermercado.longitude = geoPointSupermercado.longitude
                val distanceInMeters = locationGps!!.distanceTo(locationSupermercado)
                if (distanceInMeters < 1000) {
                    val userId = intent.getStringExtra("userId")
                    val intent = Intent(this, PublicarAvisoDesabastecimientoActivity::class.java).apply {
                        putExtra("idSupermercado", idSupermercado)
                        putExtra("userId", userId)
                    }
                    startActivity(intent)
                }else
                    Toast.makeText(this, "Esta muy lejos para publicar un aviso de desabastecimiento", Toast.LENGTH_LONG).show()
            }
    }

    fun onClickFavorito(view: View){
        val userId = intent.getStringExtra("userId")
        val db = FirebaseFirestore.getInstance()
        if(fav == 0) {
            ivFav.setColorFilter(
                ContextCompat.getColor(this, R.color.favorito),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            fav = 1
            Toast.makeText(this, "Supermercado agregado como favorito", Toast.LENGTH_SHORT).show()

            db.collection("Usuarios").document(userId!!).update(
                "Favoritos", FieldValue.arrayUnion(
                    idSupermercado
                )
            )
            val data = hashMapOf(
                "Favoritos" to arrayListOf(idSupermercado)
            )
        }else{
            ivFav.setColorFilter(
                ContextCompat.getColor(this, R.color.noFavorito),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            fav = 0
            Toast.makeText(this, "Supermercado eliminado como favorito", Toast.LENGTH_SHORT).show()

            db.collection("Usuarios").document(userId!!).update(
                "Favoritos", FieldValue.arrayRemove(
                    idSupermercado
                )
            )
        }
    }

    fun cerrarActividadSelAviso(view: View) { //cerrar con la x
        finish()
    }


    override fun onStart() {
        super.onStart()
        adapRang.startListening()
        adapProd.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapRang.stopListening()
        adapProd.stopListening()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (hasGps){
            Log.d("CodeAndroidLocation", "hasGps")
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f
            ) { location -> locationGps = location }
            val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null)
                locationGps = localGpsLocation
        }else
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

    }

}


