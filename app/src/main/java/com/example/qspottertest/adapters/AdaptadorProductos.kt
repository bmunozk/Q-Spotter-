package com.example.qspottertest.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qspottertest.datas.InfoItemsDesabastecimiento
import com.example.qspottertest.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.avisos.view.*


class AdaptadorProductos(options: FirestoreRecyclerOptions<InfoItemsDesabastecimiento>) : FirestoreRecyclerAdapter<InfoItemsDesabastecimiento, AdaptadorProductos.ProductosViewHolder>(options){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.avisos, parent, false)

        return ProductosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductosViewHolder, position: Int, model: InfoItemsDesabastecimiento) {

        holder.prodDes.text = model.producto
        holder.horaPub.text = model.hora
    }

    class ProductosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val prodDes = itemView.rangFIl
        val horaPub = itemView.horaFil
    }


}