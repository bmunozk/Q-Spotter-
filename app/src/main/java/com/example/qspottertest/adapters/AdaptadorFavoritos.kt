package com.example.qspottertest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qspottertest.R
import com.example.qspottertest.datas.InfoItemsSupermercado
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.info_favoritos.view.*

class AdaptadorFavoritos(options: FirestoreRecyclerOptions<InfoItemsSupermercado>, private val listener: OnItemClickListener) : FirestoreRecyclerAdapter<InfoItemsSupermercado, AdaptadorFavoritos.FavoritosViewHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritosViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.info_favoritos,
            parent,
            false
        )

        return FavoritosViewHolder(itemView)
    }

    override fun onBindViewHolder( holder: FavoritosViewHolder, position: Int, model: InfoItemsSupermercado ) {

        holder.nombSupermercado.text = model.Nombre
        holder.dirSupermercado.text = model.Direccion!!.split(",")[0]
        holder.cantAvisosFila.text = model.CantAvisoFila.toString()
        holder.cantAvisoDesabastecimiento.text = model.CantAvisoDesabastecimiento.toString()

    }


    inner class FavoritosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{

        val nombSupermercado = itemView.tvNomSup
        val dirSupermercado = itemView.tvDirSup
        val cantAvisosFila = itemView.tvCantAvFil
        val cantAvisoDesabastecimiento = itemView.tvCantAvDes

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onNoteClick(adapterPosition)
        }
    }

    interface OnItemClickListener{
        fun onNoteClick(position: Int)
    }


}




