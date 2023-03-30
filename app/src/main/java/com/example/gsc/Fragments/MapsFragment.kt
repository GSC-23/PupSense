package com.example.gsc.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.gsc.Constants.API_KEY
import com.example.gsc.DataClass.MarkerDataClass
import com.example.gsc.DataClass.RecentAlert
import com.example.gsc.DataManipulation.getAddress
import com.example.gsc.HelpActivity
import com.example.gsc.MainActivity
import com.example.gsc.R
import com.example.gsc.onBoarding.LoginScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.GeoApiContext
import kotlinx.coroutines.*
class MapsFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private var mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private var passingLatlng:LatLng = LatLng(0.0,0.0)
    private lateinit var db:FirebaseFirestore
    private lateinit var markerList1:ArrayList<MarkerDataClass>
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
        bottomSheetView=view.findViewById(R.id.frame_modal)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        markerList1= ArrayList()
//        marker=ArrayList<Marker>()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val currentUser=mAuth.currentUser
        GlobalScope.launch(Dispatchers.IO) {
            fetchData()
        }
        view?.findViewById<Button>(R.id.help_button)?.setOnClickListener {
            val intent=Intent(requireContext(),HelpActivity::class.java)
            intent.putExtra("latitude", passingLatlng.latitude)
            intent.putExtra("longitude", passingLatlng.longitude)
            startActivity(intent)
        }
        return view
    }

    private suspend fun fetchData() {
        db= FirebaseFirestore.getInstance()
        db.collection("Recent  Alerts")
            .get()
            .addOnSuccessListener {result->
            for (document in result){
                val myData=document.toObject(MarkerDataClass::class.java)
                val latitude = myData.latitude
                val longitude = myData.longitude
                markerList1.add(myData)

//                val deferredAddress = GlobalScope.async {
//                    getAddress(LatLng(latitude, longitude), requireContext())
//                }
                GlobalScope.launch(Dispatchers.Main){
                    val markerOptions = MarkerOptions()
                        .position(LatLng(latitude, longitude))
//                        .icon(fromVectorToBitmap(R.drawable.baseline_pets_24,R.color.black))
                    map.addMarker(markerOptions)
//                    val varMarker=map.addMarker(markerOptions)
//                    varMarker?.let { marker.add(it) }
                }
//                if(markerList1.size!=0){
//                    markerList1.forEach { marker ->
//                        val location = LatLng(marker.latitude,marker.longitude)
//
//                        GlobalScope.launch(Dispatchers.IO) {
//                            val deferredAddress = async {
//                                getAddress(LatLng(latitude, longitude), requireContext())
//                            }
//
//                            val address = deferredAddress.await()
//                            // Update marker title with address if available
//                            if (address != null) {
//                                withContext(Dispatchers.Main) {
//                                    val markerOptions = MarkerOptions()
//                                        .position(location)
//                                        .title(address)
//                                    map.addMarker(markerOptions)
//                                }
//                            } else {
//                                withContext(Dispatchers.Main) {
//                                    val markerOptions = MarkerOptions()
//                                        .position(LatLng(latitude, longitude))
//                                        .title("Couldnt fetch")
//                                }
//                            }
//                        }
//                    }
//
//                }
            }
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
        bottomSheetBehavior.peekHeight = 150
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.isHideable = true
        geoApiContext = GeoApiContext.Builder().apiKey(API_KEY).build()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        map.setOnMarkerClickListener(this)
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
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500, null)
                map.setMapStyle(style)
            }

            map.uiSettings.apply {
                isZoomControlsEnabled = false
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
//                isZoomGesturesEnabled = false
            }
            map.setPadding(0,340,0,0)
        }
        GlobalScope.launch (Dispatchers.IO){
            fetchData()
        }

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        passingLatlng = marker.position
//        GlobalScope.launch(Dispatchers.IO) {
//            val deferredAddress = GlobalScope.async {
//                val deferredAddress = getAddress(marker.position, requireContext())
//            }
//            val address = deferredAddress.await()
//            withContext(Dispatchers.Main){
//                marker.title=address.toString()
//            }
//        }
        return true
        }

//    private fun fromVectorToBitmap(id:Int,color:Int):BitmapDescriptor{
//        val vectorDrawable: Drawable? = ResourcesCompat.getDrawable(resources,id,null)
//        if(vectorDrawable == null){
//            Log.d("MainActivity","Resource not found.")
//            return BitmapDescriptorFactory.defaultMarker()
//        }
//        val bitmap= Bitmap.createBitmap(
//            vectorDrawable.intrinsicWidth,
//            vectorDrawable.intrinsicHeight,
//            Bitmap.Config.ARGB_8888
//        )
//        val canvas= Canvas(bitmap)
//        vectorDrawable.setBounds(0,0,canvas.width,canvas.height)
//        DrawableCompat.setTint(vectorDrawable,color)
//        vectorDrawable.draw(canvas)
//        return BitmapDescriptorFactory.fromBitmap(bitmap)
//    }

    }







//Distance request volley
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

//Radial serach implementation
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

