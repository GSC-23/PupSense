package com.example.gsc.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gsc.DataClass.PostItem
import com.example.gsc.R
import org.w3c.dom.Text

class PreviousRecyclerAdapter(private val postItem:ArrayList<PostItem>): RecyclerView.Adapter<previousViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): previousViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.rv_previousfragment_items,parent,false)
        return previousViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postItem.size
    }

    override fun onBindViewHolder(holder: previousViewHolder, position: Int) {
        val currentItem=postItem[position]
        holder.postImage.setImageResource(currentItem.imageUrl)
        holder.postText.text=currentItem.content
        if(currentItem.isLiked)
        {
            holder.postLiked.setImageResource(R.drawable.heart_filled)
        }else{
            holder.postLiked.setImageResource(R.drawable.heart_outline)
        }
        holder.postLiked.setOnClickListener {
            currentItem.isLiked = !currentItem.isLiked
            notifyItemChanged(position)
        }
    }

}

class previousViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val postImage:ImageView = itemView.findViewById(R.id.iv_previous)
    val postText:TextView = itemView.findViewById(R.id.tv_previousPost)
    val postLiked:ImageView=itemView.findViewById(R.id.iv_liked)
}