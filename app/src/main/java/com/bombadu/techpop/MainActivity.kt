package com.bombadu.techpop

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.bombadu.techpop.model.NewsData
import com.bombadu.techpop.tabs.*
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT



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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about) {
            val aboutDialog = AlertDialog.Builder(this)
            aboutDialog.setTitle("TechPop: v0.5")
            aboutDialog.setMessage(
                "Build Date: 2-2-20\nby Michael May\nBombadu Apps")
            aboutDialog.setIcon(R.mipmap.ic_launcher)
            aboutDialog.show()

        }
        return super.onOptionsItemSelected(item)
    }

}
