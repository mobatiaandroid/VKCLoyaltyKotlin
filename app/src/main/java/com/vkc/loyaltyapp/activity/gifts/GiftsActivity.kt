package com.vkc.loyaltyapp.activity.gifts

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.activity.gifts.model.GiftsModel
import androidx.recyclerview.widget.RecyclerView
import com.vkc.loyaltyapp.activity.gifts.model.VoucherModel
import android.os.Bundle
import com.vkc.loyaltyapp.R
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.AdapterView.OnItemSelectedListener
import android.view.Gravity
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import org.json.JSONArray
import com.vkc.loyaltyapp.activity.gifts.adapter.GiftsAdapter
import com.vkc.loyaltyapp.utils.CustomToast
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.vkc.loyaltyapp.activity.cart.CartActivity
import java.lang.Exception
import java.util.ArrayList

/**
 * Created by user2 on 2/8/17.
 */
class GiftsActivity : AppCompatActivity(), VKCUrlConstants, View.OnClickListener {
    var mContext: AppCompatActivity? = null
    var listGifts: MutableList<GiftsModel>? = null
    var recyclerView: RecyclerView? = null
    var editQuantity: EditText? = null
    var mImageBack: ImageView? = null
    var imageCart: ImageView? = null
    var textCart: TextView? = null
    var mVoucherId: String? = null
    var llAddCart: LinearLayout? = null
    var llVoucher: LinearLayout? = null
    var spinnerVoucher: Spinner? = null
    var listVouchers: ArrayList<VoucherModel>? = null
    var textCoupon: TextView? = null
    var textCartTotal: TextView? = null
    var textBalanceCoupon: TextView? = null
    var textCartQuantity: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gifts)
        mContext = this
        initUI()
    }

    private fun initUI() {
        // get the reference of RecyclerView
        mVoucherId = ""
        listGifts = ArrayList()
        listVouchers = ArrayList()
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        mImageBack = findViewById<View>(R.id.btn_left) as ImageView
        textCoupon = findViewById<View>(R.id.textCoupon) as TextView
        textCoupon!!.visibility = View.GONE
        textBalanceCoupon = findViewById<View>(R.id.textBalanceCoupon) as TextView
        textCartTotal = findViewById<View>(R.id.textTotalCart) as TextView
        textCartQuantity = findViewById<View>(R.id.textCartQuantity) as TextView
        textCart = findViewById<View>(R.id.textCount) as TextView
        imageCart = findViewById<View>(R.id.btn_right) as ImageView
        spinnerVoucher = findViewById<View>(R.id.spinnerVoucher) as Spinner
        editQuantity = findViewById<View>(R.id.editQty) as EditText
        llAddCart = findViewById<View>(R.id.llCart) as LinearLayout
        llVoucher = findViewById<View>(R.id.llVoucher) as LinearLayout
        llVoucher!!.visibility = View.GONE
        mImageBack!!.setOnClickListener(this)
        imageCart!!.setOnClickListener(this)
        llAddCart!!.setOnClickListener(this)
        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        val gridLayoutManager = GridLayoutManager(applicationContext, 2)
        //  LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        recyclerView!!.layoutManager = gridLayoutManager
        recyclerView!!.setHasFixedSize(true)
        //  recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        /* recyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });*/
        //   recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        //  recyclerView.setItemAnimator(new DefaultItemAnimator());
        spinnerVoucher!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                //    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                (parent.getChildAt(0) as TextView).gravity = Gravity.CENTER
                (parent.getChildAt(0) as TextView).textSize = 12f
                if (position > 0) {
                    mVoucherId = listVouchers!![position - 1].id
                    textCoupon!!.text =
                        listVouchers!![position - 1].coupon_value + " Loyalty Points"
                    textCoupon!!.visibility = View.VISIBLE
                } else {
                    textCoupon!!.visibility = View.GONE
                    mVoucherId = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        gifts
        cartItems
    }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);// Toast.makeText(mContext, getResources().getString(R.string.invalid_user), Toast.LENGTH_SHORT).show();

    //    System.out.println("Response---Login" + successResponse);
    val gifts: Unit
        get() {
            listGifts!!.clear()
            listVouchers!!.clear()
            try {
                val name = arrayOf("cust_id")
                val values = arrayOf(AppPrefenceManager.getCustomerId(mContext))
                val manager = VolleyWrapper(VKCUrlConstants.GET_GIFTS)
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
                                        val voucherArray = objResponse.optJSONArray("vouchers")
                                        if (voucherArray.length() > 0) {
                                            for (i in 0 until voucherArray.length()) {
                                                val obj = voucherArray.optJSONObject(i)
                                                val model = VoucherModel()
                                                model.id = obj.optString("id")
                                                model.coupon_value = obj.optString("coupon_value")
                                                model.voucher_value = obj.optString("voucher_value")
                                                listVouchers!!.add(model)
                                            }
                                            val listVoucher = ArrayList<String>()
                                            listVoucher.add("Loyalty Points")
                                            for (i in listVouchers!!.indices) {
                                                listVoucher.add(listVouchers!![i].voucher_value + " Voucher value" + " ( " + listVouchers!![i].coupon_value + " Loyalty Points )")
                                            }
                                            if (listVoucher.size > 0) {
                                                llVoucher!!.visibility = View.VISIBLE
                                                val adapter = ArrayAdapter(
                                                    this@GiftsActivity,
                                                    android.R.layout.simple_spinner_item,
                                                    listVoucher
                                                )
                                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                spinnerVoucher!!.adapter = adapter
                                            } else {
                                                llVoucher!!.visibility = View.GONE
                                            }
                                        }
                                        if (dataArray.length() > 0) {
                                            for (i in 0 until dataArray.length()) {
                                                val obj = dataArray.optJSONObject(i)
                                                val model = GiftsModel()
                                                model.id = obj.optString("id")
                                                model.description = obj.optString("description")
                                                model.image = obj.optString("image")
                                                model.point = obj.optString("point")
                                                model.title = obj.optString("title")
                                                listGifts!!.add(model)
                                            }
                                            val adapter = GiftsAdapter(
                                                mContext,
                                                listGifts,
                                                textCart,
                                                textBalanceCoupon,
                                                textCartTotal
                                            )
                                            recyclerView!!.adapter = adapter
                                        } else {
                                            val toast = CustomToast(mContext)
                                            toast.show(5)
                                        }
                                        cartCount
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
    val cartCount: Unit
        get() {
            try {
                val name = arrayOf("cust_id")
                val values = arrayOf(AppPrefenceManager.getCustomerId(mContext))
                val manager = VolleyWrapper(VKCUrlConstants.GET_CART_COUNT)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            //    System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val status = rootObject.optString("response")
                                    if (status.equals("1", ignoreCase = true)) {
                                        textCart!!.text = rootObject.optString("cart_count")
                                    } else if (status == "5") {
                                        val toast = CustomToast(
                                            mContext
                                        )
                                        toast.show(62)
                                    } else {
                                        val toast = CustomToast(mContext)
                                        toast.show(24)
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

    fun addToCart() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editQuantity!!.windowToken, 0)
        try {
            val name = arrayOf("cust_id", "gift_id", "quantity", "gift_type")
            val values = arrayOf(
                AppPrefenceManager.getCustomerId(mContext),
                mVoucherId,
                editQuantity!!.text.toString().trim { it <= ' ' },
                "2"
            )
            val manager = VolleyWrapper(VKCUrlConstants.ADD_TO_CART)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val response = rootObject.optString("response")
                                val cart_count = rootObject.optString("cart_count")
                                if (response.equals("1", ignoreCase = true)) {
                                    val toast = CustomToast(mContext)
                                    toast.show(6)
                                    editQuantity!!.setText("")
                                    textCart!!.text = cart_count
                                    spinnerVoucher!!.setSelection(0)
                                    cartCount
                                    cartItems
                                } else if (response.equals("2", ignoreCase = true)) {
                                    val toast = CustomToast(mContext)
                                    toast.show(38)
                                    editQuantity!!.setText("")
                                } else if (response == "5") {
                                    val toast = CustomToast(
                                        mContext
                                    )
                                    toast.show(61)
                                } else {
                                    val toast = CustomToast(mContext)
                                    toast.show(39)
                                    editQuantity!!.setText("")
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
            // Log.d("TAG", "Common error");
        }
    }

    override fun onClick(v: View) {
        if (v === mImageBack) {
            finish()
        } else if (v === imageCart) {
            startActivity(Intent(this@GiftsActivity, CartActivity::class.java))
        } else if (v === llAddCart) {
            if (mVoucherId == "") {
                val toast = CustomToast(mContext)
                toast.show(41)
            } else if (editQuantity!!.text.toString().trim { it <= ' ' } == "") {
                val toast = CustomToast(mContext)
                toast.show(42)
            } else if (editQuantity!!.text.toString().trim { it <= ' ' } == "0") {
                val toast = CustomToast(mContext)
                toast.show(59)
            } else {
                addToCart()
            }
        }
    }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);

    //    System.out.println("Response---Login" + successResponse);
    val cartItems: Unit
        get() {
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

    override fun onRestart() {
        super.onRestart()
        cartCount
    }

    override fun onResume() {
        super.onResume()
        cartCount
        cartItems
    }
}