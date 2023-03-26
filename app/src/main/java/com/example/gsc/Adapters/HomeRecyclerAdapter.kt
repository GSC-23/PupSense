package com.example.gsc.Adapters

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gsc.DataClass.dataClass
import com.google.android.gms.maps.OnMapReadyCallback
import com.example.gsc.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer

class HomeRecyclerAdapter(private val items: ArrayList<dataClass>):RecyclerView.Adapter<viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerviewitem, parent, false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val location = items[position]
//        holder.textView.text = location.name!!
        Glide.with(holder.itemView)
            .load(getStaticMapUrl(location.latitude!!, location.longitude!!))
            .into(holder.image_map!!)

    }
    override fun getItemCount() = items.size
}
class viewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)  {
//    val textView:TextView = itemView.findViewById(R.id.timeanddate_tv)
    val image_map=itemView.findViewById<ImageView>(R.id.map)
}

private fun getStaticMapUrl(latitude: Double, longitude: Double): String {
    val apiKey = "AIzaSyAoFVFGd4v0q28Yx-WdreQTelZt2MZCmuQ" // Replace with your API key
    val width = Resources.getSystem().displayMetrics.widthPixels
    val size = "250x${width}" // Set the size of the image
    val color = "#FFB3C1"

    return "https://maps.googleapis.com/maps/api/staticmap?center=$latitude,$longitude&zoom=15&size=$size&key=$apiKey&markers=icon:https://appurl.io/DtWgrrStFQ%7Ccolor:blue%7Clabel:S%7C$latitude,$longitude%7C"
}
