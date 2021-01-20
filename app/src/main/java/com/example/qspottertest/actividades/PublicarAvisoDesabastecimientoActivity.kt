package com.example.qspottertest.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.qspottertest.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_publicar_aviso_desabastecimiento.*
import java.text.SimpleDateFormat

import java.util.*

class PublicarAvisoDesabastecimientoActivity() : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var listaProductos = mutableListOf<String>()
    private var flagItemSelec = false
    private var posLisDes: Int= -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicar_aviso_desabastecimiento)
        this.setFinishOnTouchOutside(true)


        //spiner desabastecimiento:
        getProductos(listaProductos)
        val seleccionDes = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaProductos)
        listaProductos.add(0, "Seleccionar producto")
        seleccionDes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(selRanDes) {
            adapter = seleccionDes
            setSelection(0, false)
            onItemSelectedListener = this@PublicarAvisoDesabastecimientoActivity
            gravity = Gravity.CENTER
        }

    }

    fun getProductos(lista: MutableList<String>){
        val db = FirebaseFirestore.getInstance()
        db.collection("Productos")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val arreglo = document.get("ListaProductos") as ArrayList<*>
                    for(i in arreglo){
                        lista.add(i.toString())
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Productos", "Error al obtener documentos: ", exception)
            }


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position>0) {  //se ve que se seleccione un rango valido
            flagItemSelec = true
            posLisDes=position
        }
        if(position==0)
            flagItemSelec = false
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this@PublicarAvisoDesabastecimientoActivity, "Nada seleccionado", Toast.LENGTH_SHORT).show()
    }


    fun onClickConfAvDes(view: View){
        val idSupermercado = intent.getStringExtra("idSupermercado")
        val userId = intent.getStringExtra("userId")
        @SuppressLint("SimpleDateFormat")
        val sdf  = SimpleDateFormat("HH:mm:ss")
        val hora = sdf.format(Date())

        if(flagItemSelec){

            val producto = listaProductos[posLisDes]

            val data = hashMapOf(
                "hora" to hora,
                "usuario" to userId,
                "producto" to producto,
                "idSupermercado" to idSupermercado,
                "timeStamp" to FieldValue.serverTimestamp()
            )

            val db = FirebaseFirestore.getInstance()

            db.collection("AvisoDesabastecimiento").document(idSupermercado+userId+producto)
                .get()
                .addOnSuccessListener {
                    if (!it.exists())
                        db.collection("Supermercados").document(idSupermercado!!).update("CantAvisoDesabastecimiento",FieldValue.increment(1))

                    db.collection("AvisoDesabastecimiento").document(idSupermercado+userId+producto)
                        .set(data)
                        .addOnSuccessListener { documentReference ->
                            Log.d("AvisoDesabastecimiento", "DocumentSnapshot written with ID: $documentReference")
                        }
                        .addOnFailureListener { e ->
                            Log.w("AvisoDesabastecimiento", "Error adding document", e)
                        }
                }

            Toast.makeText(
                this@PublicarAvisoDesabastecimientoActivity,
                "Aviso de desabastecimiento confirmado",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }else Toast.makeText(
            this@PublicarAvisoDesabastecimientoActivity,
            "Debe seleccionar un producto",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cerrarActividadPubDes(view: View) { //cerrar con la x
        finish()
    }
}