package com.example.gsc.DataClass

import android.graphics.Bitmap
import android.net.Uri

data class HelpActivityDataClass(
    var name:String="",
    var address:String="",
    val image:String="",
    val origin:com.google.maps.model.LatLng =com.google.maps.model.LatLng(0.0,0.0),
    val destination:com.google.maps.model.LatLng =com.google.maps.model.LatLng(0.0,0.0),
    val distance:Double=0.0,
    var isExpandable:Boolean = false
)
