package com.example.gsc.onBoarding.carouselView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.gsc.R

class ViewPagerAdapter(private val text:List<String>,private var images:List<Int>,private val listener:btnClicked): RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {
    lateinit var recyclerView: RecyclerView
    inner class Pager2ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val itemText=itemView.findViewById<TextView>(R.id.tv_carousel)
        val itemImage:ImageView = itemView.findViewById(R.id.iv_carousel)
        val btn=itemView.findViewById<Button>(R.id.btn)
        init {
            itemImage.setOnClickListener {
                val position = adapterPosition
                Toast.makeText(itemView.context,"You clicked on item ${position + 1}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_carousel,parent,false)
        val viewHolder=Pager2ViewHolder(view)
//        if(viewHolder.adapterPosition==text.size-1){
//            view.findViewById<Button>(R.id.btn).visibility=View.VISIBLE
//            view.findViewById<Button>(R.id.btn).setOnClickListener {
//                listener.onBtnCLicked()
//            }
//        }
        viewHolder.itemView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val currentPosition = viewHolder.adapterPosition

            if (currentPosition == itemCount - 1) {
                view.findViewById<Button>(R.id.btn).text = "Begin"
                view.findViewById<Button>(R.id.btn).setOnClickListener {
                    listener.onBtnCLicked()
                }
            }else{
                val nextPosition = currentPosition + 1
                if (nextPosition < itemCount) {
                    view.findViewById<Button>(R.id.btn).setOnClickListener {
                        recyclerView.smoothScrollToPosition(nextPosition)
                    }
                }
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return text.size
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        holder.itemText.text = text[position]
        holder.itemImage.setImageResource(images[position])
    }
}

interface btnClicked{
    fun onBtnCLicked()
}