package com.example.gsc.DataClass

import java.sql.Timestamp
import java.util.Date

data class MarkerDataClass(
    var latitude:Double = 0.0,
    var longitude:Double=0.0,
    var time:Date?=null
)
