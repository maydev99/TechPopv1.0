package com.bombadu.techpop

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bombadu.techpop.model.NewsData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_card.view.*

class NewsAdapter(private val listData: List<NewsData>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(listData[position])


    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var webUrl: String? = null

        fun bindItems(newsData: NewsData) {
            itemView.title_text_view.text = newsData.title
            itemView.description_text_view.text = newsData.description
            itemView.byLine_text_view.text = newsData.author
            val imageUrl = newsData.imageUrl
            val cardImageView: ImageView = itemView.news_card_image_view
            if (imageUrl == "") {
                Picasso.get().load(R.drawable.noimage).resize(1280,960).centerCrop().into(cardImageView)
            } else {
                Picasso.get().load(imageUrl).into(cardImageView)
            }

            webUrl = newsData.webUrl


        }

        init {
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(webUrl)
                itemView.context.startActivity(intent)
            }

        }


    }


}