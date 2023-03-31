package com.example.gsc.onBoarding.carouselView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.gsc.R
import com.example.gsc.onBoarding.LoginScreen
import com.example.gsc.onBoarding.PermissionActivity
import me.relex.circleindicator.CircleIndicator3

class CarouselView : AppCompatActivity(), btnClicked {
    private var text= mutableListOf<String>()
    private var images= mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel_view)
        fillData()
        val view_pager2=findViewById<ViewPager2>(R.id.view_pager2)
        view_pager2.adapter=ViewPagerAdapter(text,images,this)
        view_pager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator3 = findViewById<CircleIndicator3>(R.id.indicator)
        indicator3.setViewPager(view_pager2)
    }

    private fun fillData(){
        text.addAll(listOf("Humanity is not a choice but a duty",
            "Homelessness is hard for street dogs, let's not leave them unattended.",
        "We have the power to better the lives of all creatures."
            )
        )
        images.addAll(listOf(
            R.drawable.carousel_1,
            R.drawable.carousel_2,
            R.drawable.carousel_3
        )
        )
    }

    override fun onBtnCLicked() {
        startActivity(Intent(this,LoginScreen::class.java))
        finish()
    }


}