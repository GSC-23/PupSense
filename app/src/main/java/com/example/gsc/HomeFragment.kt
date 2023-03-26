package com.example.gsc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gsc.Adapters.HomeRecyclerAdapter
import com.example.gsc.DataClass.dataClass

private lateinit var RecyclerView:RecyclerView
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_home,container,false)
        RecyclerView = view.findViewById(R.id.rv_map)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items= ArrayList<dataClass>()
        val obj1:dataClass= dataClass(30.970259886942088, 75.6284345320183)
        items.add(obj1)
        val obj2:dataClass = dataClass(41.06679809537876, 28.807470708520285)
        items.add(obj2)

        val obj3:dataClass = dataClass(39.99853337024502, 32.7517672842157)
        items.add(obj3)

        val obj4:dataClass = dataClass(42.54624511,1.601554)
        items.add(obj4)

        val obj5:dataClass = dataClass(12.565679,104.990963)
        items.add(obj5)
        RecyclerView.adapter=HomeRecyclerAdapter(items)
        RecyclerView.layoutManager=LinearLayoutManager(requireContext())
    }

}