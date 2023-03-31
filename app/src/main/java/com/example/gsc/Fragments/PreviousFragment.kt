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
            array.add(PostItem("He may have been hit, but he's not out! Our furry friend is on the road to recovery.", R.drawable.previoushelp))
            array.add(PostItem("Meet our newest rescue dog! He recently survived a terrible accident and is now in our care. Despite his ordeal, he's a brave little fighter and is already responding well to treatment. We're determined to give him all the love and care he needs to make a full recovery. Stay tuned for updates on his progress!",R.drawable.ovrs_hospital))
            array.add(PostItem("We recently took in a furry friend who was in a terrible accident. Despite the trauma he endured, he's already proving to be a fighter and is responding well to his treatments. Our team is dedicated to giving him the best care possible so he can make a full recovery. Join us on his journey and stay tuned for updates!",R.drawable.pet_exam_))
            array.add(PostItem("Meet our newest rescue pup! She's a survivor of a terrible accident and is now in our care. We're committed to helping her heal and find a loving forever home. Follow her journey and help us spread awareness about responsible pet ownership.",R.drawable.dog_sedation))

        return array
    }
}