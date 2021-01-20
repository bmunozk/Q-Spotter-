package com.example.qspottertest.adapters


import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.qspottertest.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.avisos.view.*


class AdaptadorInfoMarcador(context: Context) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.infowindow_layout, null)

    private fun rendowWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.titleInfo)
        val tvSnippet = view.findViewById<TextView>(R.id.snippetInfo)

        tvTitle.text = marker.title
        tvSnippet.text = marker.snippet.split("-")[0].split(",")[0]

    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}
