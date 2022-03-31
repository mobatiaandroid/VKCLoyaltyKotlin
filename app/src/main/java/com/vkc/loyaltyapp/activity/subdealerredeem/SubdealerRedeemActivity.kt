package com.vkc.loyaltyapp.activity.subdealerredeem

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.activity.subdealerredeem.model.RetailerModel
import android.os.Bundle
import com.vkc.loyaltyapp.R
import android.widget.AdapterView.OnItemSelectedListener
import android.view.Gravity
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import org.json.JSONArray
import com.vkc.loyaltyapp.activity.subdealerredeem.adapter.RetailersListAdapter
import com.vkc.loyaltyapp.utils.CustomToast
import com.vkc.loyaltyapp.appcontroller.AppController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import android.content.Intent
import android.view.View
import android.widget.*
import com.vkc.loyaltyapp.activity.cart.model.DealerModel
import com.vkc.loyaltyapp.activity.redeem_subdealer.RedeemHistorySubdealerActivity
import com.vkc.loyaltyapp.manager.CustomToastMessage
import java.lang.Exception
import java.util.ArrayList

/**
 * Created by user2 on 14/3/18.
 */
class SubdealerRedeemActivity : AppCompatActivity(), VKCUrlConstants, View.OnClickListener {
    var mContext: AppCompatActivity? = null
    var editQuantity: EditText? = null
    var mImageBack: ImageView? = null
    var btn_history: ImageView? = null
    var listViewRetailer: ListView? = null
    var textCartTotal: TextView? = null
    var textBalanceCoupon: TextView? = null
    var textCartQuantity: TextView? = null
    var listRetailers: ArrayList<RetailerModel>? = null
    var spinnerDealer: Spinner? = null
    var mDealerId: String? = null
    var listDealers: ArrayList<DealerModel>? = null
    var buttonOrder: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subdealer_redeem)
        mContext = this
        initUI()
    }

    private fun initUI() {
        // get the reference of RecyclerView
        mDealerId = ""
        listDealers = ArrayList()
        listRetailers = ArrayList()
        listViewRetailer = findViewById<View>(R.id.listViewRetailers) as ListView
        buttonOrder = findViewById<View>(R.id.buttonOrder) as Button
        mImageBack = findViewById<View>(R.id.btn_left) as ImageView
        btn_history = findViewById<View>(R.id.btn_right) as ImageView
        spinnerDealer = findViewById<View>(R.id.spinnerDealer) as Spinner
        mImageBack!!.setOnClickListener(this)
        buttonOrder!!.setOnClickListener(this)
        btn_history!!.setOnClickListener(this)
        spinnerDealer!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                (parent.getChildAt(0) as TextView).gravity = Gravity.CENTER
                (parent.getChildAt(0) as TextView).textSize = 12f
                mDealerId = if (position > 0) {
                    listDealers!![position - 1].id
                } else {
                    ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        dealers
    }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);// Toast.makeText(mContext, getResources().getString(R.string.invalid_user), Toast.LENGTH_SHORT).show();

    //    System.out.println("Response---Login" + successResponse);
    val retailerList: Unit
        get() {
            listRetailers!!.clear()
            try {
                val name = arrayOf("cust_id")
                val values = arrayOf(AppPrefenceManager.getCustomerId(mContext))
                val manager = VolleyWrapper(VKCUrlConstants.GET_SUBDEALER_RETAILERS)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            //    System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val objResponse = rootObject.optJSONObject("response")
                                    val status = objResponse.optString("status")
                                    if (status.equals("Success", ignoreCase = true)) {
                                        val dataArray = objResponse.optJSONArray("data")
                                        if (dataArray.length() > 0) {
                                            for (i in 0 until dataArray.length()) {
                                                val model = RetailerModel()
                                                val obj = dataArray.optJSONObject(i)
                                                model.retailer_id = obj.optString("retailer_id")
                                                model.user_name = obj.optString("user_name")
                                                model.userID = obj.optString("userID")
                                                listRetailers!!.add(model)
                                            }
                                            val adapter =
                                                RetailersListAdapter(mContext, listRetailers)
                                            listViewRetailer!!.adapter = adapter
                                        } else {
                                            listViewRetailer!!.adapter = null
                                            val toast = CustomToast(mContext)
                                            toast.show(66)
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
            }
        }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);// Toast.makeText(mContext, getResources().getString(R.string.invalid_user), Toast.LENGTH_SHORT).show();

    //    System.out.println("Response---Login" + successResponse);
    val dealers: Unit
        get() {
            listDealers!!.clear()
            try {
                val name = arrayOf("cust_id", "role")
                val values = arrayOf(AppPrefenceManager.getCustomerId(mContext), "7")
                val manager = VolleyWrapper(VKCUrlConstants.GET_DEALERS_SUBDEALER)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            //    System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val objResponse = rootObject.optJSONObject("response")
                                    val status = objResponse.optString("status")
                                    if (status.equals("Success", ignoreCase = true)) {
                                        val dataArray = objResponse.optJSONArray("data")
                                        if (dataArray.length() > 0) {
                                            for (i in 0 until dataArray.length()) {
                                                val model = DealerModel()
                                                val obj = dataArray.optJSONObject(i)
                                                model.id = obj.optString("id")
                                                model.name = obj.optString("name")
                                                listDealers!!.add(model)
                                            }
                                            val listDealer = ArrayList<String>()
                                            listDealer.add("Select Dealer")
                                            for (i in listDealers!!.indices) {
                                                listDealer.add(listDealers!![i].name)
                                            }
                                            val adapter = ArrayAdapter(
                                                this@SubdealerRedeemActivity,
                                                android.R.layout.simple_spinner_item,
                                                listDealer
                                            )
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerDealer!!.adapter = adapter
                                        } else {
                                            val toast = CustomToast(mContext)
                                            toast.show(44)
                                        }
                                        retailerList
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
            }
        }

    override fun onClick(v: View) {
        if (v === mImageBack) {
            finish()
        } else if (v === buttonOrder) {
            if (mDealerId == "") {
                val toast = CustomToast(mContext)
                toast.show(45)
            } else if (listRetailers!!.size == 0) {
                val toast = CustomToast(mContext)
                toast.show(46)
            }

            //  else {
            val listRetailer = ArrayList<String>()
            for (i in listRetailers!!.indices) {
                if (AppController.listRetailers[i].isChecked) {
                    listRetailer.add(AppController.listRetailers[i].retailer_id)
                }
            }
            if (listRetailer.size == 0) {
                val toast = CustomToast(mContext)
                toast.show(65)
            } else if (listRetailer.size > 0 && mDealerId != "") {
                val gson = GsonBuilder().create()
                val details = gson.toJsonTree(listRetailer).asJsonArray
                placeOrder(details.toString())
            }
            //  }
        } else if (v === btn_history) {
            startActivity(
                Intent(
                    this@SubdealerRedeemActivity,
                    RedeemHistorySubdealerActivity::class.java
                )
            )
        }
    }

    override fun onRestart() {
        super.onRestart()
        //getCartItems();
    }

    override fun onResume() {
        super.onResume()
    }

    fun placeOrder(ids: String?) {
        try {
            val name = arrayOf("retailer_ids", "cust_id", "dealer_id")
            val values = arrayOf(ids, AppPrefenceManager.getCustomerId(mContext), mDealerId)
            val manager = VolleyWrapper(VKCUrlConstants.PLACE_ORDER_SUBDEALER)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                val message = objResponse.optString("message")
                                if (status.equals("Success", ignoreCase = true)) {
                                    val toast = CustomToast(mContext)
                                    toast.show(47)
                                    spinnerDealer!!.setSelection(0)
                                    mDealerId = ""
                                    retailerList
                                } else if (status.equals("scheme_error", ignoreCase = true)) {
                                    val toast = CustomToast(mContext)
                                    toast.show(64)
                                    spinnerDealer!!.setSelection(0)
                                    mDealerId = ""
                                    retailerList
                                } else {
                                    val toast = CustomToastMessage(mContext, message)
                                    //  toast.show(48);
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
        }
    }
}