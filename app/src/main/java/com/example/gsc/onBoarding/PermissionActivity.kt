package com.example.gsc.onBoarding

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.example.gsc.MainActivity
import com.example.gsc.Permissions.hasLocationPermission
import com.example.gsc.Permissions.requestLocationPermission
import com.example.gsc.R
import com.example.gsc.onBoarding.carouselView.CarouselView
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class PermissionActivity : AppCompatActivity() , EasyPermissions.PermissionCallbacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        val permissionBtn=findViewById<Button>(R.id.permissionBtn)
        permissionBtn.setOnClickListener {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (hasLocationPermission(this) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }else{
                if(hasLocationPermission(this)){
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    requestLocationPermission(this)
                }
                else{
                    requestLocationPermission(this)
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }

            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this    )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            SettingsDialog.Builder(this).build().show()
        }else{
            requestLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}