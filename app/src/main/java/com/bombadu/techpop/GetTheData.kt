package com.bombadu.techpop

import okhttp3.OkHttpClient
import okhttp3.Request

class GetTheData(source: String)  {

    val newsApiKey = BuildConfig.NEWS_API_KEY
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var imageUrls: String
    private lateinit var webUrl: String
    private lateinit var author: String


    val client = OkHttpClient()
    val url = "https://newsapi.org/v1/articles?source=engadget&apiKey=$newsApiKey"
    val request = Request.Builder().url(url).build()
    val call = client.newCall(request)




}