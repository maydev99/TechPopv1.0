package com.bombadu.techpop.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bombadu.techpop.BuildConfig
import com.bombadu.techpop.NewsAdapter
import com.bombadu.techpop.R
import com.bombadu.techpop.model.NewsData
import kotlinx.android.synthetic.main.tab_1.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Tab2 : Fragment() {

    private val newsApiKey = BuildConfig.NEWS_API_KEY
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var imageUrl: String
    private lateinit var webUrl: String
    private lateinit var author: String
    private var source = ""
    private var listData = mutableListOf<NewsData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_2, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        source = "engadget"
        getSourceData(source)
    }


    private fun getSourceData(source: String){
        val client = OkHttpClient()
        val url = "https://newsapi.org/v1/articles?source=$source&apiKey=$newsApiKey"
        val request = Request.Builder().url(url).build()



        client.newCall(request).enqueue(responseCallback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //println("Response Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                var myResponse = response.body!!.string()


                try {
                    var jsonObject = JSONObject(myResponse)
                    val articlesJA = jsonObject.getJSONArray("articles")
                    for (i in 0 until articlesJA.length()) {
                        val jsonIndex = articlesJA.getJSONObject(i)
                        title = jsonIndex.getString("title")
                        description = jsonIndex.getString("description")
                        imageUrl = jsonIndex.getString("urlToImage")
                        webUrl = jsonIndex.getString("url")
                        author = jsonIndex.getString("author")
                        author = if (author == "null") {
                            "by Anonymous"
                        } else {
                            "by $author"
                        }

                        println("TITLE: $title")

                        listData.add(NewsData(title, description, imageUrl, webUrl, author))


                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }


                if (response.isSuccessful){
                    activity?.runOnUiThread {
                        val recView = view?.findViewById<RecyclerView>(R.id.recyclerView_2)
                        recView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        val newsAdapter = NewsAdapter(listData)
                        recView?.adapter = newsAdapter
                    }

                }
            }


        })


    }
}