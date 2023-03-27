package com.example.gsc

import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gsc.Adapters.HomeRecyclerAdapter
import com.example.gsc.Adapters.RecyclerItemClicked
import com.example.gsc.DataClass.RecentAlert
import com.google.android.gms.location.LocationListener
import com.google.firebase.firestore.*

class HomeFragment : Fragment(),RecyclerItemClicked {
    private var HomeRecyclerAdapter:HomeRecyclerAdapter?= null
    private lateinit var db: FirebaseFirestore
    private lateinit var alertList:ArrayList<RecentAlert>
    private lateinit var RecyclerView:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_home,container,false)
        RecyclerView = view.findViewById(R.id.rv_map)
        alertList= ArrayList()
        db= FirebaseFirestore.getInstance()
        db.collection("Recent  Alerts")
            .get()
            .addOnSuccessListener {result->
                for(document in result){
                    val myData=document.toObject(RecentAlert::class.java)
                    alertList.add(myData)
                }
                Log.d("arraysize","${alertList.size}")
                // Set up the recycler view and notify adapter inside the success listener
                HomeRecyclerAdapter=HomeRecyclerAdapter(alertList,this)
                RecyclerView.adapter=HomeRecyclerAdapter
                RecyclerView.layoutManager=LinearLayoutManager(requireContext())
                HomeRecyclerAdapter!!.notifyDataSetChanged()
            }
            .addOnFailureListener {exception->
                Log.d("Answer","Error getting document")
            }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Remove the code for setting up the recycler view and notifyi ng adapter from here
        if(HomeRecyclerAdapter!=null){
            subscribeToUpdates()
        }

    }

    private fun subscribeToUpdates(){
        db.collection("Recent  Alerts")
            .addSnapshotListener{querySnapshot,firebaseFirestoreException->
                firebaseFirestoreException?.let{
                    Toast.makeText(activity,it.message,Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                val alertList1= mutableListOf<RecentAlert>()
                for (document in querySnapshot!!){
                    val mydata=document.toObject(RecentAlert::class.java)
                    alertList1.add(mydata)
                }


//                querySnapshot?.let {
//                    alertList.clear()
//                    for (document in it){
//                        val myData=document.toObject(RecentAlert::class.java)
//                        alertList.add(myData)
//                    }
//                    HomeRecyclerAdapter!!.notifyDataSetChanged()
//                }
                alertList.clear()
                alertList.addAll(alertList1)
                HomeRecyclerAdapter!!.notifyDataSetChanged()
            }
    }

    override fun onItemClicked(item: RecentAlert) {

        val intent= Intent(activity,HelpActivity::class.java)
        intent.putExtra("latitude",item.latitude)
        intent.putExtra("longitude",item.longitude)
        startActivity(intent)
    }


}






