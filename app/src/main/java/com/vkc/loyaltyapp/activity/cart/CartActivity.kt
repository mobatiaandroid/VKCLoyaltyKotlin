package com.vkc.loyaltyapp.activity.cart

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.activity.cart.model.CartModel
import android.os.Bundle
import com.vkc.loyaltyapp.R
import android.widget.AdapterView.OnItemSelectedListener
import android.view.Gravity
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import org.json.JSONArray
import com.vkc.loyaltyapp.activity.cart.adapter.CartListAdapter
import com.vkc.loyaltyapp.utils.CustomToast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import android.content.Intent
import android.view.View
import android.widget.*
import com.vkc.loyaltyapp.activity.cart.model.DealerModel
import com.vkc.loyaltyapp.activity.redeem.RedeemHistoryActivity
import com.vkc.loyaltyapp.manager.CustomToastMessage
import java.lang.Exception
import java.util.ArrayList


class CartActivity : AppCompatActivity(), VKCUrlConstants, View.OnClickListener {
    var mContext: AppCompatActivity? = null
    var editQuantity: EditText? = null
    var mImageBack: ImageView? = null
    var btn_history: ImageView? = null
    var listViewCart: ListView? = null
    var textCartTotal: TextView? = null
    var textBalanceCoupon: TextView? = null
    var textCartQuantity: TextView? = null
    var listCart: ArrayList<CartModel>? = null
    var spinnerDealer: Spinner? = null
    var mDealerId: String? = null
    var mRole: String? = null
    var listDealers: ArrayList<DealerModel>? = null
    var buttonOrder: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        mContext = this
        initUI()
    }

    private fun initUI() {
        // get the reference of RecyclerView
        mDealerId = ""
        listDealers = ArrayList()
        listCart = ArrayList()
        listViewCart = findViewById<View>(R.id.listViewCart) as ListView
        textBalanceCoupon = findViewById<View>(R.id.textBalanceCoupon) as TextView
        textCartTotal = findViewById<View>(R.id.textTotalCart) as TextView
        textCartQuantity = findViewById<View>(R.id.textCartQuantity) as TextView
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
                if (position > 0) {
                    mDealerId = listDealers!![position - 1].id
                    mRole = listDealers!![position - 1].role
                } else {
                    mDealerId = ""
                    mRole = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        dealers
        cartItems
    }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);// Toast.makeText(mContext, getResources().getString(R.string.invalid_user), Toast.LENGTH_SHORT).show();

    //    System.out.println("Response---Login" + successResponse);
    val cartItems: Unit
        get() {
            listCart!!.clear()
            try {
                val name = arrayOf("cust_id")
                val values = arrayOf(AppPrefenceManager.getCustomerId(mContext))
                val manager = VolleyWrapper(VKCUrlConstants.GET_MY_CART)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            //    System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val objResponse = rootObject.optJSONObject("response")
                                    val status = objResponse.optString("status")
                                    val balance_points = objResponse.optString("balance_points")
                                    val total_points = objResponse.optString("total_points")
                                    val total_quantity = objResponse.optString("total_quantity")
                                    textBalanceCoupon!!.text = balance_points
                                    textCartTotal!!.text = total_points
                                    textCartQuantity!!.text = total_quantity
                                    if (status.equals("Success", ignoreCase = true)) {
                                        val dataArray = objResponse.optJSONArray("data")
                                        if (dataArray.length() > 0) {
                                            for (i in 0 until dataArray.length()) {
                                                val model = CartModel()
                                                val obj = dataArray.optJSONObject(i)
                                                model.id = obj.optString("id")
                                                model.gift_id = obj.optString("gift_id")
                                                model.gift_title = obj.optString("gift_title")
                                                model.gift_type = obj.optString("gift_type")
                                                model.quantity = obj.optString("quantity")
                                                model.point = obj.optString("point")
                                                listCart!!.add(model)
                                            }
                                            val adapter = CartListAdapter(
                                                mContext,
                                                listCart,
                                                textCartTotal,
                                                textBalanceCoupon,
                                                textCartQuantity,
                                                listViewCart
                                            )
                                            listViewCart!!.adapter = adapter
                                        } else {
                                            listViewCart!!.adapter = null
                                            val toast = CustomToast(mContext)
                                            toast.show(43)
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
                val values = arrayOf(AppPrefenceManager.getCustomerId(mContext), "5")
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
                                                model.role = obj.optString("role")
                                                listDealers!!.add(model)
                                            }
                                            val listDealer = ArrayList<String>()
                                            listDealer.add("Select Dealer")
                                            for (i in listDealers!!.indices) {
                                                listDealer.add(listDealers!![i].name)
                                            }
                                            val adapter = ArrayAdapter(
                                                this@CartActivity,
                                                android.R.layout.simple_spinner_item,
                                                listDealer
                                            )
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerDealer!!.adapter = adapter
                                        } else {
                                            val toast = CustomToast(mContext)
                                            toast.show(44)
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
        }

    override fun onClick(v: View) {
        if (v === mImageBack) {
            finish()
        } else if (v === buttonOrder) {
            if (mDealerId == "") {
                val toast = CustomToast(mContext)
                toast.show(45)
            } else if (listCart!!.size == 0) {
                val toast = CustomToast(mContext)
                toast.show(46)
            } else {
                val listOrder = ArrayList<String>()
                for (i in listCart!!.indices) {
                    listOrder.add(listCart!![i].id.toString())
                }
                val gson = GsonBuilder().create()
                val details = gson.toJsonTree(listOrder).asJsonArray
                placeOrder(details.toString())
            }
        } else if (v === btn_history) {
            startActivity(Intent(this@CartActivity, RedeemHistoryActivity::class.java))
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
            val name = arrayOf("cust_id", "dealer_id", "role", "ids")
            val values = arrayOf(AppPrefenceManager.getCustomerId(mContext), mDealerId, mRole, ids)
            val manager = VolleyWrapper(VKCUrlConstants.PLACE_ORDER)
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
                                    cartItems
                                } else if (status.equals("scheme_error", ignoreCase = true)) {
                                    val toast = CustomToast(mContext)
                                    toast.show(64)
                                    spinnerDealer!!.setSelection(0)
                                    mDealerId = ""
                                    cartItems
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