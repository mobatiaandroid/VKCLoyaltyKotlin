package com.vkc.loyaltyapp.activity.news

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import android.app.Activity
import android.app.Dialog
import com.vkc.loyaltyapp.activity.news.model.NewsListModel
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.os.Build
import com.vkc.loyaltyapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.vkc.loyaltyapp.manager.RecyclerViewTouchListener
import com.vkc.loyaltyapp.manager.RecyclerViewTouchListener.ClickListener
import android.content.Intent
import android.graphics.Color
import com.vkc.loyaltyapp.activity.news.NewsActivity
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import org.json.JSONArray
import com.vkc.loyaltyapp.activity.news.model.ProductModel
import com.vkc.loyaltyapp.activity.news.adapter.NewsListAdapter
import com.vkc.loyaltyapp.utils.CustomToast
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import com.vkc.loyaltyapp.activity.news.model.ColorModel
import java.io.Serializable
import java.lang.Exception
import java.util.ArrayList

class NewsListActivity : AppCompatActivity(), VKCUrlConstants {
    var mContext: Activity? = null
    var listNews: ArrayList<NewsListModel>? = null
    var listViewNews: RecyclerView? = null
    var btn_left: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
        setContentView(R.layout.activity_news)
        mContext = this
        initUI()
    }

    private fun initUI() {
        // get the reference of RecyclerView
        listNews = ArrayList()
        listViewNews = findViewById<View>(R.id.recyclerListNews) as RecyclerView
        btn_left = findViewById<View>(R.id.btn_left) as ImageView
        btn_left!!.setOnClickListener { finish() }
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@NewsListActivity)
        val itemDecorator =
            DividerItemDecoration(this@NewsListActivity, DividerItemDecoration.HORIZONTAL)
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
                            NewsActivity::class.java
                        )
                        //AppController.listProducts = listNews.get(position).getProductDetails();
                        intent.putExtra(
                            "news_data",
                            listNews!![position].productDetails as Serializable
                        )
                        intent.putExtra("news_id", "")
                        startActivity(intent)
                    }

                    override fun onLongClick(view: View, position: Int) {}
                })
        )
        news
    }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);// Toast.makeText(mContext, getResources().getString(R.string.invalid_user), Toast.LENGTH_SHORT).show();//    System.out.println("Response---Login" + successResponse);

    //AppPrefenceManager.getCustomerId(mContext),AppPrefenceManager.getUserType(mContext)
    val news: Unit
        get() {
            listNews!!.clear()
            try {
                val name = arrayOf("cust_id", "role")
                val values = arrayOf(
                    AppPrefenceManager.getCustomerId(mContext),
                    AppPrefenceManager.getUserType(mContext)
                ) //AppPrefenceManager.getCustomerId(mContext),AppPrefenceManager.getUserType(mContext)
                val manager = VolleyWrapper(VKCUrlConstants.GET_NEWS)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            //    System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val response = rootObject.optString("response")
                                    val status = rootObject.optString("statuscode")
                                    if (response.equals("success", ignoreCase = true)) {
                                        val dataArray = rootObject.optJSONArray("data")
                                        if (dataArray.length() > 0) {
                                            for (i in 0 until dataArray.length()) {
                                                val obj = dataArray.optJSONObject(i)
                                                val model = NewsListModel()
                                                model.id = obj.optString("id")
                                                model.message = obj.optString("message")
                                                val arrayProduct =
                                                    obj.optJSONArray("product_details")
                                                val listProducts = ArrayList<ProductModel>()
                                                for (j in 0 until arrayProduct.length()) {
                                                    val objProduct = arrayProduct.optJSONObject(j)
                                                    val productModel = ProductModel()
                                                    productModel.brand =
                                                        objProduct.optString("brand")
                                                    productModel.thumbImage =
                                                        objProduct.optString("thumb_image")
                                                    productModel.description =
                                                        objProduct.optString("description")
                                                    productModel.modelNo =
                                                        objProduct.optString("model_no")
                                                    productModel.gender =
                                                        objProduct.optString("gender")
                                                    productModel.mrp = objProduct.optString("mrp")
                                                    val arrayImages =
                                                        objProduct.optJSONArray("files")
                                                    val listImages: ArrayList<String> = ArrayList()
                                                    for (k in 0 until arrayImages.length()) {
                                                        listImages.add(arrayImages[k].toString())
                                                    }
                                                    productModel.setFiles(listImages!!)
                                                    val arrayColors =
                                                        objProduct.optJSONArray("color")
                                                    val listColors: ArrayList<ColorModel?> =
                                                        ArrayList()
                                                    for (k in 0 until arrayColors.length()) {
                                                        val objColor = arrayColors.getJSONObject(k)
                                                        val Color_model = ColorModel()
                                                        Color_model.code =
                                                            objColor.optString("code")
                                                        Color_model.name =
                                                            objColor.optString("name")
                                                        listColors.add(Color_model)
                                                    }
                                                    productModel.color = listColors
                                                    productModel.size = objProduct.optString("size")
                                                    productModel.title =
                                                        objProduct.optString("title")
                                                    listProducts.add(productModel)
                                                }
                                                model.productDetails = listProducts
                                                listNews!!.add(model)
                                            }
                                            val adapter = NewsListAdapter(mContext, listNews)
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

    inner class DialogData     // TODO Auto-generated constructor stub
        (var mActivity: Activity, var position: Int) : Dialog(mActivity) {
        var type: String? = null
        var message: String? = null
        override fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_history)
            init()
        }

        private fun init() {

            // Button buttonSet = (Button) findViewById(R.id.buttonOk);
            /* TextView txt_date = (TextView) findViewById(R.id.txt_date);
            TextView txt_type = (TextView) findViewById(R.id.txt_type);
            TextView txt_name = (TextView) findViewById(R.id.txt_name);
            TextView text_point = (TextView) findViewById(R.id.text_point);
            TextView text_quantity = (TextView) findViewById(R.id.text_quantity);
            TextView text_status = (TextView) findViewById(R.id.text_status);
            TextView text_dealer = (TextView) findViewById(R.id.text_dealer);

            Button buttonCancel = (Button) findViewById(R.id.btn_ok);
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            txt_date.setText(": " + listGifts.get(position).getDate());
            txt_type.setText(": " + listGifts.get(position).getGift_type());
            txt_name.setText(": " + listGifts.get(position).getTitle());
            text_point.setText(": " + listGifts.get(position).getPoint());
            text_quantity.setText(": " + listGifts.get(position).getQuantity());
            text_status.setText(": " + listGifts.get(position).getStatus());
            text_dealer.setText(": " + listGifts.get(position).getDealer());*/
        }
    }
}