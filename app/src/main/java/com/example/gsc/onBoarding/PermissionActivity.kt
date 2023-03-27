package com.example.gsc.onBoarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.example.gsc.MainActivity
import com.example.gsc.Permissions.hasLocationPermission
import com.example.gsc.Permissions.requestLocationPermission
import com.example.gsc.R
import com.example.gsc.databinding.ActivityPermissionBinding
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class PermissionActivity : AppCompatActivity() , EasyPermissions.PermissionCallbacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        val permissionBtn=findViewById<Button>(R.id.permissionBtn)
        permissionBtn.setOnClickListener {
            if (hasLocationPermission(this)){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }else{
                requestLocationPermission(this)
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