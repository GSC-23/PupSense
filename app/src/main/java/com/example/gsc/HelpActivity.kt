package com.example.gsc

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.gsc.Adapters.HelpRecyclerAdapter
import com.example.gsc.Adapters.directionButtonClicked
import com.example.gsc.Constants.API_KEY
import com.example.gsc.DataClass.HelpActivityDataClass
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.google.rpc.Help
import kotlinx.coroutines.*
import java.lang.Math.cos
import java.net.URL

class HelpActivity : AppCompatActivity(),directionButtonClicked {
    private lateinit var data :ArrayList<HelpActivityDataClass>
    private lateinit var helpRecyclerAdapter:HelpRecyclerAdapter
    private lateinit var shimmerLayout :ShimmerFrameLayout
    private var latitude:Double = 0.0
    private var longitude:Double = 0.0
    private lateinit var recyclerView:RecyclerView
    private lateinit var directionButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        data=ArrayList<HelpActivityDataClass>()
        recyclerView=findViewById(R.id.rv_hospitals)
        shimmerLayout=findViewById(R.id.shimmer_layout)
        shimmerLayout.startShimmer()
        latitude=intent.getDoubleExtra("latitude",3.0)
        longitude=intent.getDoubleExtra("longitude",0.0)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_hospitals)
        recyclerView.layoutManager = LinearLayoutManager(this)
        GlobalScope.launch (Dispatchers.IO){
            delay(1500)
            fetchData(latitude,longitude)
        }

    }

    private suspend fun fetchData(latitude: Double, longitude: Double) {

        val context = GeoApiContext.Builder()
            .apiKey(API_KEY)
            .build()

        if (!Places.isInitialized()) {
            Places.initialize(this, API_KEY)
        }

        val placesClient = Places.createClient(this)
        val radius = 2000 // in meters
        val type = "veterinary_care"

        val queue = Volley.newRequestQueue(this)
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=$radius&type=$type&key=$API_KEY"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val JsonArray=response.getJSONArray("results")
                if(JsonArray.length()==0){
                    runOnUiThread {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                        Toast.makeText(this, "No hospitals nearby !!", Toast.LENGTH_SHORT).show()
                    }
                }

                val data = mutableListOf<HelpActivityDataClass>()
                for (i in 0 until JsonArray.length()) {
                    val JsonObject = JsonArray.getJSONObject(i)
                    val name = JsonObject.getString("name")
                    var address = ""
                    if (JsonObject.has("plus_code") && !JsonObject.isNull("plus_code")) {
                        address = JsonObject.getJSONObject("plus_code").getString("compound_code")
                    }

                    val origin = com.google.maps.model.LatLng(latitude, longitude)

                    val destination = com.google.maps.model.LatLng(
                        JsonObject.getJSONObject("geometry").getJSONObject("location")
                            .getString("lat").toDouble(),
                        JsonObject.getJSONObject("geometry").getJSONObject("location")
                            .getString("lng").toDouble()
                    )

                    data.add(HelpActivityDataClass(name, address, destination = destination))
                }
                GlobalScope.launch(Dispatchers.IO){
                    val directionsResults = mutableListOf<DirectionsResult>()
                    for (destination in data.map { it.destination }) {
                        val directionsResult = DirectionsApi.newRequest(context)
                            .origin(com.google.maps.model.LatLng(latitude, longitude))
                            .destination(destination)
                            .mode(TravelMode.DRIVING)
                            .await()

                        directionsResults.add(directionsResult)
                    }
                    val newData = mutableListOf<HelpActivityDataClass>()
                    for ((i, directionsResult) in directionsResults.withIndex()) {
                        val JsonObject = JsonArray.getJSONObject(i)
                        val polyline = directionsResult.routes[0].overviewPolyline
                        val distance = directionsResult.routes[0].legs[0].distance
                        val origin = com.google.maps.model.LatLng(latitude, longitude)
                        val destination:com.google.maps.model.LatLng = com.google.maps.model.LatLng(JsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat").toDouble(), JsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng").toDouble())
                        val staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap" +
                                "?size=600x400" + "&zoom=13" +
                                "&maptype=roadmap"+"&path=enc:${polyline.encodedPath}" +
                                "&markers=color:green|label:S|${origin}" +
                                "&markers=color:red|label:D|${destination}" +
                                "&key=$API_KEY"
                        val name = JsonObject.getString("name")
                        var address = ""
                        if (JsonObject.has("plus_code") && !JsonObject.isNull("plus_code")) {
                            address = JsonObject.getJSONObject("plus_code").getString("compound_code")
                        }
                        newData.add(HelpActivityDataClass(name, address, staticMapUrl, origin, destination, distance.humanReadable.substringBefore(" ").toDouble()))
                    }
                    data.clear()
                    data.addAll(newData)
                    helpRecyclerAdapter= HelpRecyclerAdapter(data,this@HelpActivity)
                    runOnUiThread {
                        recyclerView.adapter = helpRecyclerAdapter
                        helpRecyclerAdapter?.notifyDataSetChanged()


                        if (helpRecyclerAdapter?.itemCount ?: 0 > 0) {
                            // RecyclerView is ready to display data
                            shimmerLayout.stopShimmer()
                            shimmerLayout.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        } else {
                            // RecyclerView is not ready to display data
                            shimmerLayout.startShimmer()
                            shimmerLayout.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }

                }
            },
            { error ->
            }
        )
        queue.add(jsonObjectRequest)
    }
    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
    override fun onButtonClicked(item:HelpActivityDataClass){
        val origin = item.origin
        val destination = item.destination
        Log.d("origin",origin.toString())
        Log.d("destination",destination.toString())
        val uri = Uri.parse("geo:${origin.lat},${origin.lng}?q=${destination.lat},${destination.lng}(${item.name})")

// Create an intent to open the Google Maps app
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

// Start the Google Maps app to show the route and distance
        startActivity(mapIntent)
    }

    fun onSavedBtnClicked(view: View) {
        val db = FirebaseFirestore.getInstance()
        val locationsRef = db.collection("Recent  Alerts")
        val query = locationsRef.whereEqualTo("latitude", latitude)
            .whereEqualTo("longitude", longitude)

        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.delete()
            }
        }.addOnFailureListener { exception ->
            Log.e("Deleted", "Error deleting document: $exception")
        }
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}