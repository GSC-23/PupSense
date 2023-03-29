package com.example.gsc

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gsc.Constants.API_KEY
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONObject
import java.util.*

object DataManipulation {

    suspend fun getAddress(location: com.google.android.gms.maps.model.LatLng, context: Context): String? {
        val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=${location.latitude},${location.longitude}&key=$API_KEY"
        val queue = Volley.newRequestQueue(context)

        val deferredAddress = CompletableDeferred<String?>()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Handle successful response
                val result = response.getJSONArray("results").getJSONObject(0)
                val addressComponents = result.getJSONArray("address_components")
                val address = buildString {
                    for (i in 0 until addressComponents.length()) {
                        val component = addressComponents.getJSONObject(i)
                        val longName = component.getString("long_name")
                        append("$longName ")
                    }
                }
                deferredAddress.complete(address)
            },
            { error ->
                // Handle error
                deferredAddress.complete(null)
            }
        )

        queue.add(jsonObjectRequest)
        return deferredAddress.await()
    }
}