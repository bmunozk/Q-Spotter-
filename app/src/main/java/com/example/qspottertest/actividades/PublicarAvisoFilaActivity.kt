package com.example.qspottertest.actividades

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.qspottertest.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_publicar_aviso_fila.*
import java.text.SimpleDateFormat
import java.util.*

class PublicarAvisoFilaActivity() : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var listaRangos = mutableListOf<String>()
    private var flagItemSelec = false
    private var posLisRan: Int= -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicar_aviso_fila)
        this.setFinishOnTouchOutside(true)

        //spiner fila:
        getRangos(listaRangos)
        listaRangos.add(0, "Seleccionar rango de personas")
        val seleccionFil = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaRangos)
        seleccionFil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(selRanFil) {
            adapter = seleccionFil
            setSelection(0, false)
            onItemSelectedListener = this@PublicarAvisoFilaActivity
            gravity = Gravity.CENTER
        }

    }

    fun getRangos(lista: MutableList<String>){
        val db = FirebaseFirestore.getInstance()
        db.collection("Rangos")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val arreglo = document.get("ListaRango") as ArrayList<*>
                    for(i in arreglo){
                        lista.add(i.toString())
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Rango", "Error al obtener documentos: ", exception)
            }


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position>0) { //se ve que se seleccione un rango valido
            flagItemSelec = true
            posLisRan=position
        }
        if(position==0)
            flagItemSelec = false
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this@PublicarAvisoFilaActivity, "Nada seleccionado", Toast.LENGTH_SHORT).show()
    }

    fun onClickConfAvFil(view: View){
        val idSupermercado = intent.getStringExtra("idSupermercado")
        val userId = intent.getStringExtra("userId")
        @SuppressLint("SimpleDateFormat")
        val sdf  = SimpleDateFormat("HH:mm:ss")
        val hora = sdf.format(Date())
        if(flagItemSelec) {
            val rango = listaRangos[posLisRan]
            val data = hashMapOf(
                "hora" to hora,
                "usuario" to userId,
                "rango" to rango,
                "idSupermercado" to idSupermercado,
                "timeStamp" to FieldValue.serverTimestamp()
            )

            val db = FirebaseFirestore.getInstance()


            db.collection("AvisoFila").document(idSupermercado+userId).get().addOnSuccessListener {
                if (!it.exists())
                    db.collection("Supermercados").document(idSupermercado!!).update("CantAvisoFila",FieldValue.increment(1))

                db.collection("AvisoFila").document(idSupermercado+userId).set(data)
                    .addOnSuccessListener { documentReference ->
                        Log.d("AvisoFila", "DocumentSnapshot written with ID: $documentReference")
                    }
                    .addOnFailureListener { e ->
                        Log.w("AvisoFila", "Error adding document", e)
                    }
            }


            Toast.makeText(
                this@PublicarAvisoFilaActivity,
                "Aviso de fila confirmado",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }else{
            Toast.makeText(
                this@PublicarAvisoFilaActivity,
                "Debe seleccionar un rango",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun cerrarActividadPubFil(view: View) { //cerrar con la x
        finish()
    }
}