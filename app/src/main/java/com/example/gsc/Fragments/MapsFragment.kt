package com.example.gsc.Fragments

import android.annotation.SuppressLint
import android.content.Intent
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.GeoApiContext
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MapsFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private var mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private var passingLatlng:LatLng = LatLng(0.0,0.0)
    private lateinit var db:FirebaseFirestore
    private lateinit var markerList1:ArrayList<MarkerDataClass>
    private lateinit var tvModalView:TextView
    private lateinit var bottomSheetView: FrameLayout
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

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.peekHeight = 180
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

    override fun onMarkerClick(marker: Marker): Boolean {
        tvModalView.text=getAddress(marker.position)
        bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED

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

