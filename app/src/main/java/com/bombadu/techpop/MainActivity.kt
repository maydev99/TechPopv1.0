package com.bombadu.techpop

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.bombadu.techpop.tabs.*
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //val newsApiKey = BuildConfig.NEWS_API_KEY
        //val url = "https://newsapi.org/v1/articles?source=engadget&apiKey=$newsApiKey"

        val adapter = FragmentPagerItemAdapter(
            supportFragmentManager, FragmentPagerItems.with(this)
                .add(R.string.tab_one, Tab1::class.java)
                .add(R.string.tab_two, Tab2::class.java)
                .add(R.string.tab_three, Tab3::class.java)
                .add(R.string.tab_four, Tab4::class.java)
                .add(R.string.tab_five, Tab5::class.java)
                .add("The Next Web", Tab6::class.java)
                .add("The Verge", Tab7::class.java)
                .create())
        val viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        viewPager.adapter = adapter
        val viewPagerTab = findViewById<View>(R.id.viewpagertab) as SmartTabLayout
        viewPagerTab.setViewPager(viewPager)
    }
}
