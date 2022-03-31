package com.vkc.loyaltyapp.activity.news.adapter

import androidx.recyclerview.widget.RecyclerView
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.vkc.loyaltyapp.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import com.vkc.loyaltyapp.activity.news.model.ProductModel
import androidx.viewpager.widget.PagerAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.vkc.loyaltyapp.activity.news.adapter.ColorAdapter
import com.vkc.loyaltyapp.activity.news.model.NewsListModel

class CustomPagerAdapter(var mContext: Activity, mProduct: ProductModel) : PagerAdapter() {
    var mLayoutInflater: LayoutInflater
    lateinit var mResources: IntArray
    var mProduct: ProductModel
    var list_Size: Int
    override fun getCount(): Int {
        return list_Size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.item_pager, container, false)
        var url = mProduct.files[position]
        url = url.replace(" ".toRegex(), "%20")
        val imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
        Glide.with(mContext).load(url).into(imageView)
        // imageView.setImageResource(mResources[position]);
        imageView.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(mContext)
            val inflater = mContext.layoutInflater
            val dialogView = inflater.inflate(R.layout.layout_image_zoom, null)
            dialogBuilder.setView(dialogView)
            val webView = dialogView.findViewById<View>(R.id.imageLarge) as WebView
            val imageClose = dialogView.findViewById<View>(R.id.imageClose) as ImageView
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            webView.setInitialScale(80)
            webView.setBackgroundColor(Color.TRANSPARENT)
            webView.isScrollbarFadingEnabled = true
            webView.loadUrl(mProduct.files[position])
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
            imageClose.setOnClickListener { // TODO Auto-generated method stub
                alertDialog.dismiss()
            }
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

    init {
        mLayoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.mProduct = mProduct
        list_Size = mProduct.files.size
        /*mResources = new int[]{
                R.drawable.footware,
                R.drawable.footware,
                R.drawable.footware
        };*/
    }
}