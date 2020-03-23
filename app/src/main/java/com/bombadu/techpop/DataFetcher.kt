package com.bombadu.techpop

import com.bombadu.techpop.model.NewsData
import com.google.firebase.database.*

class DataFetcher {

    private var listData = mutableListOf<NewsData>()
    private var rootRef = FirebaseDatabase.getInstance().reference
    private var articlesRef: DatabaseReference? = null
    private var fBDataRef: DatabaseReference? = null

    fun getSourceData(source: String): MutableList<NewsData> {

        fBDataRef = rootRef.child("fb_data")
        articlesRef = fBDataRef!!.child(source) //News Source

        val newsListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
                println("ERROR: ${dataSnapshot.toException()}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listData.clear()
                for (item in dataSnapshot.children) {
                    val key = item.key.toString()
                    val author = dataSnapshot.child(key).child("author").value.toString()
                    val description = dataSnapshot.child(key).child("description").value.toString()
                    val imageUrl = dataSnapshot.child(key).child("image_url").value.toString()
                    val title = dataSnapshot.child(key).child("title").value.toString()
                    val webUrl = dataSnapshot.child(key).child("web_url").value.toString()
                    listData.add(NewsData(title, description, imageUrl, webUrl, author))
                }

            }
        }
        articlesRef?.addValueEventListener(newsListener)
        return listData
    }

}