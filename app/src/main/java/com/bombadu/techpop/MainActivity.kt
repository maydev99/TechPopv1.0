package com.bombadu.techpop

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bombadu.techpop.tabs.*
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        //Manages Tabs
        val adapter = FragmentPagerItemAdapter(
            supportFragmentManager, FragmentPagerItems.with(this)
                .add(R.string.tab_one, Tab1::class.java)
                .add(R.string.tab_two, Tab2::class.java)
                .add(R.string.tab_three, Tab3::class.java)
                .add(R.string.tab_four, Tab4::class.java)
                .add(R.string.tab_five, Tab5::class.java)
                .add("The Next Web", Tab6::class.java)
                .add("The Verge", Tab7::class.java)
                .create()
        )
        val viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        viewPager.adapter = adapter
        val viewPagerTab = findViewById<View>(R.id.viewpagertab) as SmartTabLayout
        viewPagerTab.setViewPager(viewPager)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Custom About Dialog
        if (item.itemId == R.id.about) {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.setContentView(R.layout.about_custom_dialog_layout)
            dialog.show()

            //for Attrition for NewsAPi.org
            val newsApi = dialog.findViewById<TextView>(R.id.about_newsApi_text_view)
            newsApi.setOnClickListener {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse("https://newsapi.org/")
                startActivity(openURL)
            }

        }

        return super.onOptionsItemSelected(item)


    }

}




