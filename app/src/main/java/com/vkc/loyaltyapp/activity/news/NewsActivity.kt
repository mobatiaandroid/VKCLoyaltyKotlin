package com.vkc.loyaltyapp.activity.news

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import android.app.Activity
import com.vkc.loyaltyapp.activity.news.model.ProductModel
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.os.Build
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.activity.news.adapter.NewsAdapter
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.vkc.loyaltyapp.activity.HomeActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import com.vkc.loyaltyapp.manager.RecyclerViewTouchListener
import com.vkc.loyaltyapp.manager.RecyclerViewTouchListener.ClickListener
import com.vkc.loyaltyapp.activity.news.NewsDetailActivity
import com.vkc.loyaltyapp.activity.news.model.ColorModel
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import org.json.JSONArray
import com.vkc.loyaltyapp.utils.CustomToast
import java.io.Serializable
import java.lang.Exception
import java.util.ArrayList

class NewsActivity : AppCompatActivity(), VKCUrlConstants {
    var mContext: Activity? = null
    var listProducts: ArrayList<ProductModel>? = null
    var listViewNews: RecyclerView? = null
    var btn_left: ImageView? = null
    var newsId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
        setContentView(R.layout.activity_news_list)
        mContext = this
        initUI()
        val bundle = intent.extras
        if (bundle != null) {
            // listProducts = (ArrayList<ProductModel>) bundle.getSerializable("news_data");
            newsId = bundle.getString("news_id")
            //  listProducts = ((ArrayList<ProductModel>) getIntent().getExtras().getSerializable("news_data"));
            if (newsId == "") {
                listProducts = intent.getSerializableExtra("news_data") as ArrayList<ProductModel>?
                val adapter = NewsAdapter(mContext, listProducts)
                listViewNews!!.adapter = adapter
            } else {
                getNews(newsId)
            }
        }
    }

    private fun initUI() {
        // get the reference of RecyclerView
        listProducts = ArrayList()
        listViewNews = findViewById<View>(R.id.recyclerNews) as RecyclerView
        btn_left = findViewById<View>(R.id.btn_left) as ImageView
        btn_left!!.setOnClickListener {
            if (newsId == "") {
                finish()
            } else {
                val intent = Intent(this@NewsActivity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@NewsActivity)
        val itemDecorator =
            DividerItemDecoration(this@NewsActivity, DividerItemDecoration.HORIZONTAL)
        // itemDecorator.setDrawable(ContextCompat.getDrawable(NewsActivity.this, R.drawable.divider));
        listViewNews!!.layoutManager = mLayoutManager
        listViewNews!!.itemAnimator = DefaultItemAnimator()
        listViewNews!!.addItemDecoration(itemDecorator)
        listViewNews!!.addOnItemTouchListener(
            RecyclerViewTouchListener(
                applicationContext,
                listViewNews,
                object : ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val intent = Intent(
                            applicationContext,
                            NewsDetailActivity::class.java
                        )
                        intent.putExtra("product_data", listProducts!![position] as Serializable)
                        startActivity(intent)
                    }

                    override fun onLongClick(view: View, position: Int) {}
                })
        )
        //getHistory();
        val adapter = NewsAdapter(mContext, listProducts)
        listViewNews!!.adapter = adapter
    }

    fun getNews(news_id: String?) {
        listProducts!!.clear()
        try {
            val name = arrayOf("push_id")
            val values = arrayOf(news_id)
            val manager = VolleyWrapper(VKCUrlConstants.GET_NEWS_DETAIL)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val response = rootObject.optString("response")
                                val status = rootObject.optString("responsecode")
                                if (response.equals("success", ignoreCase = true)) {
                                    val dataArray = rootObject.optJSONArray("data")
                                    val objProducts = dataArray.getJSONObject(0)
                                    val arrayProduct = objProducts.optJSONArray("product_details")
                                    if (dataArray.length() > 0) {

                                        //  ArrayList<ProductModel> listProducts = new ArrayList<>();
                                        for (j in 0 until arrayProduct.length()) {
                                            val objProduct = arrayProduct.optJSONObject(j)
                                            val productModel = ProductModel()
                                            productModel.brand = objProduct.optString("brand")
                                            productModel.thumbImage =
                                                objProduct.optString("thumb_image")
                                            productModel.description =
                                                objProduct.optString("description")
                                            productModel.modelNo = objProduct.optString("model_no")
                                            productModel.gender = objProduct.optString("gender")
                                            productModel.mrp = objProduct.optString("mrp")
                                            val arrayImages = objProduct.optJSONArray("files")
                                            val listImages: ArrayList<String> = ArrayList()
                                            for (k in 0 until arrayImages.length()) {
                                                listImages.add(arrayImages[k].toString())
                                            }
                                            productModel.setFiles(listImages)
                                            val arrayColors = objProduct.optJSONArray("color")
                                            val listColors: ArrayList<ColorModel?> =
                                                ArrayList()
                                            for (k in 0 until arrayColors.length()) {
                                                val objColor = arrayColors.getJSONObject(k)
                                                val model = ColorModel()
                                                model.code = objColor.optString("code")
                                                model.name = objColor.optString("name")
                                                listColors.add(model)
                                            }
                                            productModel.color = listColors
                                            productModel.size = objProduct.optString("size")
                                            productModel.title = objProduct.optString("title")
                                            listProducts!!.add(productModel)
                                        }
                                        val adapter = NewsAdapter(mContext, listProducts)
                                        listViewNews!!.adapter = adapter
                                    } else {
                                        val toast = CustomToast(mContext)
                                        toast.show(5)
                                    }
                                } else {
                                    val toast = CustomToast(mContext)
                                    toast.show(4)
                                    // Toast.makeText(mContext, getResources().getString(R.string.invalid_user), Toast.LENGTH_SHORT).show();
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        } else {
                            val toast = CustomToast(mContext)
                            toast.show(0)
                        }
                    }

                    override fun responseFailure(failureResponse: String) {
                        //CustomStatusDialog(RESPONSE_FAILURE);
                    }
                })
        } catch (e: Exception) {
            // CustomStatusDialog(RESPONSE_FAILURE);
            e.printStackTrace()
            Log.d("TAG", "Common error")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (newsId == "") {
            finish()
        } else {
            val intent = Intent(this@NewsActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}