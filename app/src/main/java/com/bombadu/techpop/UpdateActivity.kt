package com.bombadu.techpop

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class UpdateActivity : AppCompatActivity() {

    private lateinit var theTitle: String
    private lateinit var theDescription: String
    private lateinit var theImageUrl: String
    private lateinit var theWebUrl: String
    private lateinit var theAuthor: String
    private val newsApiKey = BuildConfig.NEWS_API_KEY
    private var rootRef = FirebaseDatabase.getInstance().reference
    private var dataUpdateRef: DatabaseReference? = null
    private var fbDataRef: DatabaseReference? = null
    private var isUpdated = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        dataUpdateRef = rootRef.child("data_update")
        fbDataRef = rootRef.child("fb_data")


        //Needed to handle delete latency
        fbDataRef?.removeValue()?.addOnCompleteListener {
            val handler = Handler()
            handler.postDelayed({
                fetchTheData()
            }, 5000)
        }
    }

    //This function gets the data from NewsApi.org and push it to Firebase
    private fun fetchTheData() {

        val mySources = listOf(
            "ars-technica",
            "engadget",
            "recode",
            "techcrunch",
            "techradar",
            "the-next-web",
            "the-verge"
        )

        //Loops though news sources and sends request from each source to NewsApi
        for (element in mySources) {
            val client = OkHttpClient()
            val url = "https://newsapi.org/v1/articles?source=$element&apiKey=$newsApiKey"
            val request = Request.Builder().url(url).build()



            client.newCall(request).enqueue(responseCallback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //println("Response Failed")
                }

                //Successful response from News Api
                override fun onResponse(call: Call, response: Response) {
                    val myResponse = response.body!!.string()


                    try {
                        val jsonObject = JSONObject(myResponse)
                        val articlesJA = jsonObject.getJSONArray("articles")

                        //Clears Firebase data for new data

                        //Loops though JSON Data and gets strings for each news article
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

                            //Puts data from each article into Firebase
                            val taskMap: MutableMap<String, Any> = HashMap()
                            taskMap["source"] = element
                            taskMap["title"] = theTitle
                            taskMap["description"] = theDescription
                            taskMap["image_url"] = theImageUrl
                            taskMap["web_url"] = theWebUrl
                            taskMap["author"] = theAuthor
                            fbDataRef?.child(element)?.push()?.updateChildren(taskMap)!!.addOnCompleteListener {

                            }

                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    if (response.isSuccessful) {
                        runOnUiThread {
                            isUpdated = true
                            closeUpdate()

                        }
                    }

                }
            })

        }

    }

    private fun closeUpdate() {
        if (isUpdated) {
            rootRef.child("data_update").setValue(timeStamp())
            finish()

        }
    }

    private fun timeStamp(): String {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
    }

}
