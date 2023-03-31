package com.example.gsc.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import com.example.gsc.Constants.API_KEY
import com.example.gsc.DataClass.MarkerDataClass
import com.example.gsc.DataClass.RecentAlert
import com.example.gsc.HelpActivity
import com.example.gsc.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMapClickListener

import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MapsFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private var mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private var passingLatlng:LatLng = LatLng(0.0,0.0)
    private lateinit var db:FirebaseFirestore
    private lateinit var markerList1:ArrayList<MarkerDataClass>
    private lateinit var tvModalView:TextView
    private lateinit var bottomSheetView: FrameLayout
    private var polylines:Polyline? = null
    private var address = ""
    private var outputdate=""
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
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
        tvModalView=view.findViewById(R.id.tv_address_modal)
//        marker=ArrayList<Marker>()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val currentUser=mAuth.currentUser
        GlobalScope.launch(Dispatchers.IO) {
            fetchData()
        }
        view?.findViewById<Button>(R.id.help_button)?.setOnClickListener {
            val add=view.findViewById<TextView>(R.id.tv_address_modal).text
            val time=view.findViewById<TextView>(R.id.tv_maps_time).text
            val intent=Intent(requireContext(),HelpActivity::class.java)
            intent.putExtra("latitude", passingLatlng.latitude)
            intent.putExtra("address","$time - $add")
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
                GlobalScope.launch(Dispatchers.Main){
                    val markerOptions = MarkerOptions()
                        .position(LatLng(latitude, longitude))
                        .icon(fromVectorToBitmap(R.drawable.baseline_pets_24,R.color.black))
                    map.addMarker(markerOptions)
                }
            }
            }
            .addOnFailureListener {
                Log.d("Error","Error getting the document")
            }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.map_username_tv).text = "Hello, ${mAuth.currentUser?.displayName?.substringBefore(" ")}"
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.peekHeight = 175
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.isHideable = true
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener {location->
            if(location!=null){
                view.findViewById<TextView>(R.id.map_tv_location).text= getAddress(LatLng(location.latitude,location.longitude))
            }
        }
        geoApiContext = GeoApiContext.Builder().apiKey(API_KEY).build()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener {
            bottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
        }
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
        subscribeToUpdates()

    }

    private fun subscribeToUpdates() {
        db = FirebaseFirestore.getInstance()
        db.collection("Recent  Alerts")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Listen Failed", "Listen failed.", e)
                    return@addSnapshotListener
                }

                // Process snapshot updates
                snapshot?.let {
                    for (documentChange in it.documentChanges) {
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val myData = documentChange.document.toObject(MarkerDataClass::class.java)
                                val latitude = myData.latitude
                                val longitude = myData.longitude
                                markerList1.add(myData)

                                GlobalScope.launch(Dispatchers.Main) {
                                    val markerOptions = MarkerOptions()
                                        .position(LatLng(latitude, longitude))
                                        .icon(fromVectorToBitmap(R.drawable.baseline_pets_24,R.color.black))
                                    map.addMarker(markerOptions)
                                }
                            }
                            // Handle other document changes if needed
                            DocumentChange.Type.MODIFIED -> {}
                            DocumentChange.Type.REMOVED -> {}
                        }
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        subscribeToUpdates()
    }
    private fun getAddress(position:LatLng):String{
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        passingLatlng = position
        try {
            val addresses = geocoder.getFromLocation(passingLatlng.latitude, passingLatlng.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                return addresses[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("MissingPermission")
    override fun onMarkerClick(marker: Marker): Boolean {
        polylines?.remove()
        address=getAddress(marker.position)
        tvModalView.text = address
        bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
        db.collection("Recent  Alerts")
            .whereEqualTo("latitude", marker.position.latitude)
            .whereEqualTo("longitude", marker.position.longitude)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Loop through the matching documents
                for (document in querySnapshot.documents) {
                    // Access the timestamp field of each document
                    val timestamp = document.getTimestamp("time")?.toDate()
                    if (timestamp != null) {
                        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH)
                        val outputFormat = SimpleDateFormat("MMM dd hh:mm a", Locale.ENGLISH)
                        val inputDate = inputFormat.parse(timestamp.toString())
                        outputdate = outputFormat.format(inputDate!!)
                        view?.findViewById<TextView>(R.id.tv_maps_time)?.text = outputdate
                    } else {
                        Toast.makeText(requireContext(),"Error ocurred while reading database",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.w("TimeStamp", "Error getting documents: ", exception)
            }
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener {location->
            if(location!=null){
                val origin=com.google.maps.model.LatLng(location.latitude, location.longitude)
                val destination=com.google.maps.model.LatLng(marker.position.latitude,marker.position.longitude)
                val directionsResult: DirectionsResult = DirectionsApi.newRequest(geoApiContext)
                    .origin(origin)
                    .destination(destination)
                    .mode(TravelMode.DRIVING)
                    .await()
                val points = mutableListOf<LatLng>()
                val legs = directionsResult.routes[0].legs
                for (i in 0 until legs.size) {
                    val steps = legs[i].steps
                    for (j in 0 until steps.size) {
                        val polyline = steps[j].polyline
                        val encodedPoints = polyline.encodedPath
                        points.addAll(PolyUtil.decode(encodedPoints))
                    }
                }

                val polylineOptions = PolylineOptions().apply {
                    color(Color.BLUE)
                    width(10f)
                    addAll(points)
                }
                val boundsBuilder = LatLngBounds.builder()
                for (point in points) {
                    boundsBuilder.include(point)
                }
                val bounds = boundsBuilder.build()

                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200), 500, null)


                polylines=map.addPolyline(polylineOptions)

                    val distance = directionsResult.routes[0].legs[0].distance
                    view?.findViewById<TextView>(R.id.tv_maps_distance)?.text =
                        " | $distance kms from you"

            }
        }
        return true
        }
    override fun onResume() {
        GlobalScope.launch(Dispatchers.IO) {
            fetchData()
        }
        super.onResume()
    }
    private fun fromVectorToBitmap(id:Int,color:Int):BitmapDescriptor{
        val vectorDrawable: Drawable? = ResourcesCompat.getDrawable(resources,id,null)
        if(vectorDrawable == null){
            Log.d("MainActivity","Resource not found.")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap= Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas= Canvas(bitmap)
        vectorDrawable.setBounds(0,0,canvas.width,canvas.height)
        DrawableCompat.setTint(vectorDrawable,color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    }


