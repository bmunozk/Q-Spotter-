package com.example.qspottertest.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qspottertest.R
import com.example.qspottertest.adapters.AdaptadorFavoritos
import com.example.qspottertest.datas.InfoItemsSupermercado
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_favoritos.*
import java.text.FieldPosition
import java.util.ArrayList

class FavoritosActivity : AppCompatActivity(), AdaptadorFavoritos.OnItemClickListener {

    private lateinit var adapFav: AdaptadorFavoritos
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var pathFavoritos: CollectionReference
    private lateinit var userId: String
    private lateinit var favoritos: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        userId = intent.getStringExtra("userID").toString()
        val favString = intent.getStringExtra("favoritos").toString()

        favoritos = favString.split(",").toMutableList()
        favoritos.sort()

        if (favoritos.contains("")) {
            favoritos.remove("")
            favoritos.add("save")
        }

        pathFavoritos = db.collection("Supermercados")

        setUpRecyclerViewFila()
        if (favString != "") {
            noHayfav.visibility = View.GONE;
            rvFavUsr.visibility = View.VISIBLE;
        }else{
            noHayfav.visibility = View.VISIBLE;
            rvFavUsr.visibility = View.GONE;
        }
    }

    fun setUpRecyclerViewFila(){
        val query: Query =  pathFavoritos.whereIn(FieldPath.documentId(),favoritos).orderBy(FieldPath.documentId())
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<InfoItemsSupermercado> = FirestoreRecyclerOptions.Builder<InfoItemsSupermercado>()
            .setQuery(query, InfoItemsSupermercado::class.java)
            .build()
        adapFav = AdaptadorFavoritos(firestoreRecyclerOptions,this)
        rvFavUsr.layoutManager = LinearLayoutManager(this)
        rvFavUsr.adapter = adapFav
        rvFavUsr.isNestedScrollingEnabled = true
    }

    fun cerrarActividadFavorito(view: View) { //cerrar con la x
        finish()
    }

    override fun onNoteClick(position: Int) {
        db.collection("Supermercados").document(favoritos[position])
            .get()
            .addOnSuccessListener {
                val nombre = it.get("Nombre").toString()
                val superDireccion = it.get("Direccion").toString()
                val intent = Intent(this, SeleccionarAvisoActivity::class.java).apply {
                    //putExtra("idSupermercado", idSupermercado)
                    putExtra("userId",userId)
                    putExtra("superNombre",nombre)
                    putExtra("superDireccion",superDireccion)
                    putExtra("idSupermercado",favoritos[position])
                }
                startActivity(intent)
                finish()
            }

    }

    override fun onStart() {
        super.onStart()
        adapFav.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapFav.stopListening()
    }


}