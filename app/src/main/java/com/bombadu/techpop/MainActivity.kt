package com.bombadu.techpop

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bombadu.techpop.tabs.*
import com.google.firebase.database.*
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var theTitle: String
    lateinit var theDescription: String
    lateinit var theImageUrl: String
    lateinit var theWebUrl: String
    lateinit var theAuthor: String
    private val newsApiKey = BuildConfig.NEWS_API_KEY
    private var rootRef = FirebaseDatabase.getInstance().reference
    private var dataUpdateRef : DatabaseReference? = null
    private var fbDataRef : DatabaseReference? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        dataUpdateRef = rootRef.child("data_update")
        fbDataRef = rootRef.child("fb_data")


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
        if (item.itemId == R.id.about) {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.setContentView(R.layout.about_custom_dialog_layout)
            dialog.show()

            val newsApi = dialog.findViewById<TextView>(R.id.about_newsApi_text_view)
            newsApi.setOnClickListener {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse("https://newsapi.org/")
                startActivity(openURL)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        checkUpdate()
        //fetchTheData()
    }

    private fun checkUpdate() {
        val currentTimeStamp = timeStamp().toLong()
        val updateListener = object  : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lastUpdateTimeStamp = dataSnapshot.value.toString().toLong()
                val diff = currentTimeStamp - lastUpdateTimeStamp
                if (diff > 43200) {
                    fetchTheData()
                }
            }
        }

        dataUpdateRef?.addValueEventListener(updateListener)

    }

    private fun timeStamp(): String {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
    }

    private fun fetchTheData(){

        val mySources  = listOf("ars-technica", "engadget", "recode", "techcrunch", "techradar", "the-next-web", "the-verge")
        //val sources = "ars-technica,engadget,recode,techcrunch,techradar,the-next-web,the-verge"
        for (element in mySources){
            val client = OkHttpClient()
            val source = element
            println("SOURCEY: $source")


            val url = "https://newsapi.org/v1/articles?source=$source&apiKey=$newsApiKey"
            val request = Request.Builder().url(url).build()



            client.newCall(request).enqueue(responseCallback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //println("Response Failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    var myResponse = response.body!!.string()
                    println("REPSPONSE: $myResponse")


                    try {
                        val jsonObject = JSONObject(myResponse)
                        val articlesJA = jsonObject.getJSONArray("articles")
                        for (i in 0 until articlesJA.length()) {
                            val jsonIndex = articlesJA.getJSONObject(i)
                            theTitle = jsonIndex.getString("title")

                            theDescription = jsonIndex.getString("description")
                            theImageUrl = jsonIndex.getString("urlToImage")
                            theWebUrl = jsonIndex.getString("url")
                            theAuthor = jsonIndex.getString("author")
                            theAuthor = if (theAuthor == "null") {
                                "by Anonymous"
                            } else {
                                "by $theAuthor"
                            }

                            fbDataRef?.removeValue() //Clears Cloud DB


                             val taskMap: MutableMap<String, Any> = HashMap()
                             taskMap["source"] = source
                             taskMap["title"] = theTitle
                             taskMap["description"] = theDescription
                             taskMap["image_url"] = theImageUrl
                             taskMap["web_url"] = theWebUrl
                             taskMap["author"] = theAuthor
                             fbDataRef?.child(source)?.push()?.updateChildren(taskMap)

                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }




                }




            })
        }
        }
}



