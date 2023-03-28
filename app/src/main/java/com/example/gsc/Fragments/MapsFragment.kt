package com.example.gsc.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.gsc.Constants.API_KEY
import com.example.gsc.DataClass.RecentAlert
import com.example.gsc.HelpActivity
import com.example.gsc.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import java.io.IOException
import kotlin.math.cos
import kotlinx.coroutines.*
class MapsFragment : Fragment(),OnMapReadyCallback{
    private lateinit var mAuth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var bottomSheetView: FrameLayout
    private lateinit var map:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var alertList: ArrayList<RecentAlert>
    private lateinit var geoApiContext: GeoApiContext
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_maps, container, false)
    alertList=ArrayList<RecentAlert>()
        bottomSheetView=view.findViewById(R.id.frame_modal)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    mAuth= FirebaseAuth.getInstance()
    val currentUser=mAuth.currentUser
    fetchData()

        return view
    }

    private fun fetchData() {
        db= FirebaseFirestore.getInstance()
        db.collection("Recent  Alerts")
            .get()
            .addOnSuccessListener {result->
            for (document in result){
                val myData=document.toObject(RecentAlert::class.java)
                alertList.add(myData)
                Log.d("Data of firebase",myData.toString())
            }
                Log.d("Size of array",alertList.size.toString())
            }
            .addOnFailureListener {
                Log.d("Error","Error getting the document")
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.isHideable = false
        geoApiContext = GeoApiContext.Builder().apiKey(API_KEY).build()
        view.findViewById<Button>(R.id.help_button).setOnClickListener {
            startActivity(Intent(requireContext(),HelpActivity::class.java))
        }
        view.findViewById<ImageView>(R.id.iv_profile).setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
        val origin=LatLng(31.399175561065167, 75.5353661341174)

//           val queue=Volley.newRequestQueue(requireContext())
//            val url="https://maps.googleapis.com/maps/api/directions/json?" +
//                    "origin=${origin.latitude},${origin.longitude}&" +
//                    "destination=31.39458252816213, 75.54150787910709&" +
//                    "key=$API_KEY"
//            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
//                { response ->
//                    // Parse the response JSON and extract the distance.
//                    val array=response.getJSONObject("routes")
//                    Log.d("distance",array.toString())
//
//                    // Do something with the distance value.
//                },
//                { error ->
//                    // Handle error
//                })
//            queue.add(jsonObjectRequest)

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)

        map.clear()
//        map.setOnMyLocationButtonClickListener()
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            // Check if the location is not null
            if (location != null) {
                // Move the camera to the user's current location
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(location.latitude, location.longitude))
                    .zoom(15f)
                    .build()
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null)
                map.setMapStyle(style)

//                if (!Places.isInitialized()) {
//                    Places.initialize(requireContext(), "AIzaSyBWJu44Js9xy8ZFUy1wAsxfSWmgbrtEv18")
//                }
//
//                val placesClient = Places.createClient(requireContext())
//                val radius = 5000 // in meters
//                val type = "veterinary_care"
//                val apiKey="AIzaSyBWJu44Js9xy8ZFUy1wAsxfSWmgbrtEv18"
//
//                val bounds = LatLngBounds.builder()
//                    .include(
//                        LatLng(
//                            22.718137699108006 - radius / 111000.0,
//                            75.90834320343207 - radius / (111000.0 * cos(location.latitude * Math.PI / 180.0))
//                        )
//                    )
//                    .include(
//                        LatLng(
//                            22.718137699108006 + radius / 111000.0,
//                            75.90834320343207 + radius / (111000.0 * cos(location.latitude * Math.PI / 180.0))
//                        )
//                    )
//                    .build()
//                val queue=Volley.newRequestQueue(requireContext())
//                val url =
//                    "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=22.718137699108006,75.90834320343207&radius=$radius&type=$type&key=$apiKey"
//                val jsonObjectRequest: JsonObjectRequest =JsonObjectRequest(
//                    Request.Method.GET,
//                    url,
//                    null,
//                    {
//                        val newsJsonArray=it.getJSONArray("results")
//                        for (i in 0 until newsJsonArray.length()){
//                            val newsJSONObject=newsJsonArray.getJSONObject(i)
//                            val location =newsJSONObject.getJSONObject("geometry").getJSONObject("location")
//                            val lat=location.getDouble("lat")
//                            val lng=location.getDouble("lng")
//                            val name=newsJSONObject.getString("name")
//                            map.addMarker(MarkerOptions()
//                                .position(LatLng(lat,lng))
//                                .title(name)
//                            )
//                        }
//                    },
//                    {
//
//                    })
//                queue.add(jsonObjectRequest)
            }
            map.uiSettings.apply {
                isZoomControlsEnabled = false
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
//                isZoomGesturesEnabled = false
            }
        }

//    @SuppressLint("MissingPermission")
//    override fun onMyLocationButtonClick(): Boolean {
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            // Check if the location is not null
//            if (location != null) {
//                // Move the camera to the user's current location
//                val cameraPosition = CameraPosition.Builder()
//                    .target(LatLng(location.latitude, location.longitude))
//                    .zoom(15f)
//                    .build()
//                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//            } else {
//                // If the location is null, show a toast message
//                Toast.makeText(
//                    requireContext(),
//                    "Unable to get current location",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//        return true
//    }

    }
    }