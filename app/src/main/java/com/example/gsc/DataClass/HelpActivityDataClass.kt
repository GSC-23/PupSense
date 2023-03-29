package com.example.gsc.DataClass

import android.graphics.Bitmap
import android.net.Uri

data class HelpActivityDataClass(
    var name:String="",
    val image:String="",
    val rating:String="",
    var isExpandable:Boolean = false
)
