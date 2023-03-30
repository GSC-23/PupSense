package com.example.gsc.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gsc.DataClass.HelpActivityDataClass
import com.example.gsc.HelpActivity
import com.example.gsc.R
import com.google.android.gms.maps.model.LatLng

class HelpRecyclerAdapter(private val items:List<HelpActivityDataClass>, private val listener: HelpActivity) :
    RecyclerView.Adapter<helpViewHolder>() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): helpViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.help_recycler_item, parent, false)
        val viewHolder=helpViewHolder(view)
        view.findViewById<ImageView>(R.id.directionBtn).setOnClickListener {
            listener.onButtonClicked(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: helpViewHolder, position: Int) {
        val image=holder.image
        holder.location.text = items[position].name
        holder.image.setImageResource(R.drawable.baseline_healing_24)
        holder.distance_tv.text="${items[position].distance} km far"
        val isExpandable: Boolean = items[position].isExpandable
        Glide.with(holder.itemView)
            .load(items[position].image)
            .into(image)
        holder.cv_image.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.cv_location.setOnClickListener {
            isAnyItemExpanded(position)
            items[position].isExpandable = !items[position].isExpandable
            notifyItemChanged(position, Unit)
        }
    }

    private fun isAnyItemExpanded(position: Int) {
        val temp = items.indexOfFirst {
            it.isExpandable
        }
        if (temp >= 0 && temp != position) {
            items[temp].isExpandable = false
            notifyItemChanged(temp, 0)
        }
    }

    override fun onBindViewHolder(
        holder: helpViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] == 0) {
            holder.collapseExpandedView()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }

    }
}

interface directionButtonClicked {
    fun onButtonClicked(item:HelpActivityDataClass)
}

class helpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val location = itemView.findViewById<TextView>(R.id.tv_helpHospital)!!
    val image = itemView.findViewById<ImageView>(R.id.iv_helpHospital)!!
    val constrainLayout = itemView.findViewById<ConstraintLayout>(R.id.cl_helpactivity)
    val cv_image=itemView.findViewById<CardView>(R.id.cv_image)
    val distance_tv=itemView.findViewById<TextView>(R.id.tv_distance)
    val cv_location = itemView.findViewById<LinearLayout>(R.id.ll_helpactivity)
    fun collapseExpandedView() {
        cv_image.visibility = View.GONE
    }
}