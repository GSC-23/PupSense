package com.example.gsc

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.gsc.Constants.PERMISSION_REQUEST_CODE
import com.example.gsc.onBoarding.PermissionActivity
import com.vmadalin.easypermissions.EasyPermissions

object Permissions {
    fun hasLocationPermission(context: Context)=
        EasyPermissions.hasPermissions(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

    fun requestLocationPermission(activity:PermissionActivity){
        EasyPermissions.requestPermissions(
            activity,
            "This application cannot work without Location Permission",
            PERMISSION_REQUEST_CODE,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}