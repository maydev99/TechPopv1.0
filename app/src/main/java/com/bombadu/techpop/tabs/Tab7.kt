package com.bombadu.techpop.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bombadu.techpop.NewsAdapter
import com.bombadu.techpop.R
import com.bombadu.techpop.model.NewsData
import com.google.firebase.database.*

class Tab7 : Fragment() {

    private var listData = mutableListOf<NewsData>()
    private var rootRef = FirebaseDatabase.getInstance().reference
    private var articlesRef: DatabaseReference? = null
    private var fBDataRef: DatabaseReference? = null
    private lateinit var recView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_7, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fBDataRef = rootRef.child("fb_data")
        articlesRef = fBDataRef!!.child("the-verge") //News Source
        getSourceData()
    }

    private fun getSourceData() {

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

                recView = view!!.findViewById(R.id.recyclerView_7)
                recView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                newsAdapter = NewsAdapter(listData)
                recView.adapter = newsAdapter
            }


        }

        articlesRef?.addValueEventListener(newsListener)
    }


}