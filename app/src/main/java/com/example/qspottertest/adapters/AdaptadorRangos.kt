package com.example.qspottertest.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qspottertest.datas.InfoItemsFila
import com.example.qspottertest.R
import com.example.qspottertest.datas.InfoItemsSupermercado
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.avisos.view.*


class AdaptadorRangos(options: FirestoreRecyclerOptions<InfoItemsFila>) : FirestoreRecyclerAdapter<InfoItemsFila,AdaptadorRangos.RangosViewHolder>(options){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RangosViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.avisos, parent, false)

        return RangosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RangosViewHolder, position: Int, model: InfoItemsFila) {

        holder.rangFila.text = model.rango
        holder.horaPub.text = model.hora
    }

    class RangosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val rangFila = itemView.rangFIl
        val horaPub = itemView.horaFil
    }



}