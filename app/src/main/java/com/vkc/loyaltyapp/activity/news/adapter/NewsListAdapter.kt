package com.vkc.loyaltyapp.activity.news.adapter

import androidx.recyclerview.widget.RecyclerView
import android.app.Activity
import android.widget.TextView
import com.vkc.loyaltyapp.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.vkc.loyaltyapp.activity.news.model.ProductModel
import androidx.viewpager.widget.PagerAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.vkc.loyaltyapp.activity.news.adapter.ColorAdapter
import com.vkc.loyaltyapp.activity.news.model.NewsListModel
import java.util.ArrayList

class NewsListAdapter : RecyclerView.Adapter<NewsListAdapter.MyViewHolder> {
    private var newsList: ArrayList<NewsListModel>? = null
    var listColors: ArrayList<String>? = null
    var activity: Activity

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textTitle: TextView

        init {

            /* textDate = (TextView) view.findViewById(R.id.textDate);
            textType = (TextView) view.findViewById(R.id.textType);
            textTitle = (TextView) view.findViewById(R.id.textTitle);
            textStatus = (TextView) view.findViewById(R.id.textStatus);*/
            //imageFoot = (ImageView) view.findViewById(R.id.imageFoot);
            textTitle = view.findViewById<View>(R.id.textTitle) as TextView
        }
    }

    constructor(mActivity: Activity, newsList: ArrayList<NewsListModel>?) {
        this.newsList = newsList
        activity = mActivity
    }

    constructor(mActivity: Activity) {
        activity = mActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val listData = newsList!![i]
        myViewHolder.textTitle.text = listData.message
    }

    override fun getItemCount(): Int {
        return newsList!!.size
    } //return newsList.size();
}