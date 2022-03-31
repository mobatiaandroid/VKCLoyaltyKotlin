package com.vkc.loyaltyapp.activity.news

import android.app.Activity
import android.graphics.Color
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.activity.news.model.NewsListModel
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.vkc.loyaltyapp.activity.news.model.ProductModel
import android.widget.TextView
import android.os.Bundle
import android.os.Build
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import com.vkc.loyaltyapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.vkc.loyaltyapp.activity.news.adapter.CustomPagerAdapter
import com.vkc.loyaltyapp.activity.news.adapter.ColorAdapter
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.vkc.loyaltyapp.activity.news.model.ColorModel
import java.util.ArrayList

class NewsDetailActivity : Activity(), VKCUrlConstants {
    var mContext: Activity? = null
    var listGifts: ArrayList<NewsListModel>? = null
    var recyclerColor: RecyclerView? = null
    var btn_left: ImageView? = null
    var mViewPager: ViewPager? = null
    var mProduct: ProductModel? = null
    var txtModelName: TextView? = null
    var txtSize: TextView? = null
    var txtCategory: TextView? = null
    var txtMrp: TextView? = null
    var txtDescription: TextView? = null
    var webDescription: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
        setContentView(R.layout.activity_news_details)
        mContext = this
        recyclerColor = findViewById<View>(R.id.recyclerColor) as RecyclerView
        // DividerItemDecoration itemDecorator = new DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL);
        recyclerColor!!.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true)

        // itemDecorator.setDrawable(ContextCompat.getDrawable(NewsActivity.this, R.drawable.divider));
        recyclerColor!!.itemAnimator = DefaultItemAnimator()
        val bundle = intent.extras
        if (bundle != null) {
            // listProducts = (ArrayList<ProductModel>) bundle.getSerializable("news_data");

            //  listProducts = ((ArrayList<ProductModel>) getIntent().getExtras().getSerializable("news_data"));
            mProduct = intent.getSerializableExtra("product_data") as ProductModel?

            /*NewsAdapter adapter = new NewsAdapter(mContext, listProducts);
            listViewNews.setAdapter(adapter);*/
        }
        initUI()
    }

    private fun initUI() {
        // get the reference of RecyclerView
        btn_left = findViewById<View>(R.id.btn_left) as ImageView
        btn_left!!.setOnClickListener { finish() }
        mViewPager = findViewById<View>(R.id.pager) as ViewPager
        txtModelName = findViewById<View>(R.id.txtModelName) as TextView
        txtSize = findViewById<View>(R.id.txtSize) as TextView
        txtCategory = findViewById<View>(R.id.txtCategory) as TextView
        txtMrp = findViewById<View>(R.id.txtMrp) as TextView
        txtDescription = findViewById<View>(R.id.txtDesc) as TextView
        webDescription = findViewById<View>(R.id.webDescription) as WebView
        webDescription!!.settings.builtInZoomControls = false
        webDescription!!.settings.displayZoomControls = false
        webDescription!!.setBackgroundColor(Color.TRANSPARENT)
        webDescription!!.isScrollbarFadingEnabled = true
        if (mProduct!!.description.trim { it <= ' ' }.length > 0) {
            txtDescription!!.visibility = View.VISIBLE
            val htmlData =
                "<!DOCTYPE html><html> <head><meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/></head><body style=\"margin: 0px; padding: 0px;width:100%;height:auto;\">" +
                        "<p style=\"text-align:justify;font-size:12px;\">" + mProduct!!.description + "</p></body></html>"
            webDescription!!.loadData(htmlData, "text/html", "UTF-8")
        } else {
            txtDescription!!.visibility = View.GONE
        }


        // webView.loadUrl(mProduct.getFiles().get(position));
        txtSize!!.text = mProduct!!.size
        txtModelName!!.text = "Model : " + mProduct!!.modelNo
        //webDescription.setText(mProduct.getDescription());
        txtCategory!!.text = "CATEGORY : " + mProduct!!.gender
        txtMrp!!.text = "MRP : " + mProduct!!.mrp
        val mCustomPagerAdapter = CustomPagerAdapter(this, mProduct)
        val listColors = mProduct!!.color as ArrayList<ColorModel>
        val adapter = ColorAdapter(mContext, listColors)
        recyclerColor!!.adapter = adapter
        val bookFlipPageTransformer = BookFlipPageTransformer()

// Enable / Disable scaling while flipping. If true, then next page will scale in (zoom in). By default, its true.
        bookFlipPageTransformer.isEnableScale = true

// The amount of scale the page will zoom. By default, its 5 percent.
        bookFlipPageTransformer.scaleAmountPercent = 10f

// Assign the page transformer to the ViewPager.
        mViewPager!!.setPageTransformer(true, bookFlipPageTransformer)
        val indicator = findViewById<View>(R.id.dots_indicator) as DotsIndicator
        mViewPager!!.adapter = mCustomPagerAdapter
        indicator.setViewPager(mViewPager)
    }
}