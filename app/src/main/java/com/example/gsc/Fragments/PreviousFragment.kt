package com.example.gsc.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gsc.Adapters.PreviousRecyclerAdapter
import com.example.gsc.DataClass.PostItem
import com.example.gsc.R

class PreviousFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_previous, container, false)
        recyclerView=view.findViewById(R.id.rv_previousFragment)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter=PreviousRecyclerAdapter(fetchData())
        return view
    }

    private fun fetchData(): ArrayList<PostItem> {
        val array=ArrayList<PostItem>()
        for (i in 1 until 10){
                array.add(PostItem("He may have been hit, but he's not out! Our furry friend is on the road to recovery ...", R.drawable.previoushelp))
        }
        return array
    }
}