package com.bombadu.techpop

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bombadu.techpop.tabs.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {


    private var rootRef = FirebaseDatabase.getInstance().reference
    private var dataUpdateRef: DatabaseReference? = null
    private var fbDataRef: DatabaseReference? = null

    private var lastUpdate: String? = null //for update dialog testing
    private var updateCycleStr: String? = null //for update dialog testing
    private var isUpdating = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //auth = FirebaseAuth.getInstance()

        dataUpdateRef = rootRef.child("data_update")
        fbDataRef = rootRef.child("fb_data")

        //getKeyList()

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
        //Temporary Dialog for testing Shows Update Cycle in Epoch Time and Last Updated Time and Date
        if (item.itemId == R.id.data_update) {
            val timeDate = lastUpdate?.let { localDateTime(it) }
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Last News Update")
            builder.setMessage("$timeDate\nUpdate Cycle: $updateCycleStr")

            //Added Button to Manually Update the News Data, if needed
            builder.setPositiveButton("Force Update") { _, _ ->
                val myTimeStamp = timeStamp()
                rootRef.child("data_update").setValue(myTimeStamp).addOnCompleteListener {
                    isUpdating = true
                    if (isUpdating){startActivity(Intent(this, UpdateActivity::class.java))}
                }


            }
            builder.show()

        }
        return super.onOptionsItemSelected(item)
    }

    //Temporary - Takes in a timeStamp and converts to Human Time
    private fun localDateTime(timeStamp: String): Any {
        val calendar = Calendar.getInstance()
        val tz = calendar.timeZone
        val sdf = SimpleDateFormat("MM.dd.yyy hh:mm:ss a", Locale.getDefault())
        sdf.timeZone = tz
        val tsLong = timeStamp.toLong()
        return sdf.format(Date(tsLong * 1000))

    }

    override fun onStart() {
        super.onStart()


        checkUpdateCycle()
        /*val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUpdateCycle()
        } else {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("AUTH: ", "SUCCESS")
                        checkUpdateCycle()

                    } else {
                        Log.d("AUTH: ", "FAILED")
                    }

                }
        }*/


    }

        //Gets the last Update timeStamp from Firebase
        private fun checkUpdateCycle() {
            val updateCycleListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //nothing
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val updateCycle = dataSnapshot.value.toString().toLong()
                    updateCycleStr = updateCycle.toString()
                    checkUpdate(updateCycle)
                }
            }
            rootRef.child("update_cycle").addValueEventListener(updateCycleListener)
        }

        //Checks if Update is needed based on difference between current time and last updated time
        //If update is needed- calls FetchData Function
        private fun checkUpdate(updateCycle: Long) {
            var isUpdating = false
            val currentTimeStamp = timeStamp().toLong()
            val updateListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //nothing
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lastUpdateTimeStamp = dataSnapshot.value.toString().toLong()
                    lastUpdate = lastUpdateTimeStamp.toString()
                    val diff = currentTimeStamp - lastUpdateTimeStamp //Time Difference
                    if (diff > updateCycle) { //if difference is greater than update cycle, trigger update(Update Activity)
                        dataUpdateRef?.setValue(timeStamp())!!.addOnCompleteListener {
                            isUpdating = true

                        }
                    }
                }
            }

            dataUpdateRef?.addValueEventListener(updateListener)
            if (isUpdating){
                startActivity(Intent(this, UpdateActivity::class.java))

            }

        }
        //returns current timestamp when requested
        private fun timeStamp(): String {
            return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
        }
    }



