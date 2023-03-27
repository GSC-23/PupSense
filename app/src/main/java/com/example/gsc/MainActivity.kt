package com.example.gsc

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val popupMenu=PopupMenu(this,null)
        popupMenu.inflate(R.menu.nav_menu)
        val bottomnav=findViewById<me.ibrahimsn.lib.SmoothBottomBar>(R.id.bottom_nav)
        bottomnav.setupWithNavController(popupMenu.menu,navController)
//        val bar = supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.Fav)))
//
//        val appBarConfiguration = AppBarConfiguration(setOf(R.id.MapsFragment,R.id.previousFragment,R.id.profileFragment))
//        setupActionBarWithNavController(navController,appBarConfiguration)

    }
}