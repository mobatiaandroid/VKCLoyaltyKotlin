package com.vkc.loyaltyapp.activity.news.adapter

import androidx.recyclerview.widget.RecyclerView
import android.app.Activity
import android.widget.TextView
import com.vkc.loyaltyapp.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import com.vkc.loyaltyapp.activity.news.model.ProductModel
import androidx.viewpager.widget.PagerAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.vkc.loyaltyapp.activity.news.adapter.ColorAdapter
import com.vkc.loyaltyapp.activity.news.model.ColorModel
import com.vkc.loyaltyapp.activity.news.model.NewsListModel
import java.util.ArrayList

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private var newsList: ArrayList<ProductModel>? = null
    var listColors: ArrayList<String>? = null
    var activity: Activity

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageFoot: ImageView
        var txtModelName: TextView
        var txtSize: TextView
        var txtBrand: TextView
        var recyclerColor: RecyclerView

        init {
            txtModelName = view.findViewById<View>(R.id.txtModelName) as TextView
            txtSize = view.findViewById<View>(R.id.txtSize) as TextView
            txtBrand = view.findViewById<View>(R.id.txtBrand) as TextView
            imageFoot = view.findViewById<View>(R.id.imageFoot) as ImageView
            recyclerColor = view.findViewById<View>(R.id.recyclerColor) as RecyclerView
            // DividerItemDecoration itemDecorator = new DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL);
            recyclerColor.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)

            // itemDecorator.setDrawable(ContextCompat.getDrawable(NewsActivity.this, R.drawable.divider));
            recyclerColor.itemAnimator = DefaultItemAnimator()
            //  recyclerColor.addItemDecoration(itemDecorator);
        }
    }

    constructor(mActivity: Activity, newsList: ArrayList<ProductModel>?) {
        this.newsList = newsList
        activity = mActivity
    }

    constructor(mActivity: Activity) {
        activity = mActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        //Profile pro = profiles.get(position);
        val list_item = newsList!![position]
        val listColors = newsList!![position].color as ArrayList<ColorModel>
        val adapter = ColorAdapter(activity, listColors)
        viewHolder.recyclerColor.adapter = adapter
        viewHolder.imageFoot.bringToFront()
        viewHolder.txtBrand.text = list_item.brand
        viewHolder.txtModelName.text = list_item.modelNo
        viewHolder.txtSize.text = list_item.size
        var image_url = list_item.thumbImage
        image_url = image_url.replace(" ".toRegex(), "%20")
        Glide.with(activity).load(image_url).into(viewHolder.imageFoot)
    }

    override fun getItemCount(): Int {
        return newsList!!.size
    } //return newsList.size();
}