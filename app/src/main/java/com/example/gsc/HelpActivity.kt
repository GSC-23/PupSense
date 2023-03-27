package com.example.gsc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gsc.Adapters.HelpRecyclerAdapter
import com.example.gsc.DataClass.HelpActivityDataClass

class HelpActivity : AppCompatActivity() {
    private lateinit var data :ArrayList<HelpActivityDataClass>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val recyclerView=findViewById<RecyclerView>(R.id.rv_hospitals)
        recyclerView.layoutManager= LinearLayoutManager(this)
        data=fetchData()
        val adapter=HelpRecyclerAdapter(data)
        recyclerView.adapter=adapter

    }

    private fun fetchData(): ArrayList<HelpActivityDataClass> {
        val array=ArrayList<HelpActivityDataClass>()
        for (i in 1 until 100) {
            array.add(HelpActivityDataClass(35.2, 15.26,false))
        }
        return array
    }
}