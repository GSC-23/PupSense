package com.example.gsc

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gsc.DataClass.MarkerDataClass
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var data:ArrayList<MarkerDataClass>
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val popupMenu=PopupMenu(this,null)
        popupMenu.inflate(R.menu.nav_menu)
//        val bar = supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.black)))
//
//        val appBarConfiguration = AppBarConfiguration(setOf(R.id.MapsFragment,R.id.previousFragment,R.id.profileFragment))
//        setupActionBarWithNavController(navController,appBarConfiguration)

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
//        val navController = navHostFragment.navController
//        when (item.itemId) {
//            R.id.previousFragment -> {
//                navController.navigate(R.id.previousFragment)
//                return true
//            }
//            R.id.profileFragment -> {
//                navController.navigate(R.id.profileFragment)
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }


    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_container)
        if(navController.currentDestination?.id == R.id.profileFragment || navController.currentDestination?.id == R.id.previousFragment){
            navController.navigate(R.id.MapsFragment)
        }
        else {
            super.onBackPressed()
        }
    }
    fun onSubmenuButtonClick(view: View) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val popup = PopupMenu(this, view)


        popup.menuInflater.inflate(R.menu.nav_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.previousFragment -> {
                    navController.navigate(R.id.previousFragment)
                    true
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

}