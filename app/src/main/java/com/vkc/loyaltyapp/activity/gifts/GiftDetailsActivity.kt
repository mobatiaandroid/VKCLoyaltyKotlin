package com.vkc.loyaltyapp.activity.gifts

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import android.widget.TextView
import android.widget.EditText
import com.vkc.loyaltyapp.activity.issuepoint.model.UserModel
import android.widget.LinearLayout
import android.os.Bundle
import com.vkc.loyaltyapp.R
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import com.vkc.loyaltyapp.utils.CustomToast
import java.lang.Exception
import java.util.ArrayList

/**
 * Created by user2 on 2/8/17.
 */
class GiftDetailsActivity : AppCompatActivity(), VKCUrlConstants, View.OnClickListener {
    var mContext: AppCompatActivity? = null
    var imageThumb: ImageView? = null
    var textDescription: TextView? = null
    var textPoints: TextView? = null
    var textAddCart: TextView? = null
    var textCart: TextView? = null
    var mImageUrl: String? = null
    var mDescription: String? = null
    var mPoints: String? = null
    var mGiftId: String? = null
    var mDealerId = ""
    var editQty: EditText? = null
    var mImageBack: ImageView? = null
    var listUsers: ArrayList<UserModel>? = null
    var llDistributor: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_detail)
        mContext = this
        val intent = intent
        mImageUrl = intent.extras!!.getString("image")
        mDescription = intent.extras!!.getString("description")
        mPoints = intent.extras!!.getString("point")
        mGiftId = intent.extras!!.getString("id")
        initUI()
    }

    private fun initUI() {
        // get the reference of RecyclerView
        listUsers = ArrayList()
        imageThumb = findViewById<View>(R.id.imageThumb) as ImageView
        textDescription = findViewById<View>(R.id.textDescription) as TextView
        textPoints = findViewById<View>(R.id.textPoints) as TextView
        textAddCart = findViewById<View>(R.id.textAddCart) as TextView
        editQty = findViewById<View>(R.id.editQuantity) as EditText
        textCart = findViewById<View>(R.id.textCount) as TextView
        llDistributor = findViewById<View>(R.id.llDistributor) as LinearLayout
        mImageBack = findViewById<View>(R.id.btn_left) as ImageView
        mImageBack!!.setOnClickListener(this)
        textAddCart!!.setOnClickListener(this)
        Picasso.with(this@GiftDetailsActivity)
            .load(mImageUrl)
            .into(imageThumb, object : Callback {
                override fun onSuccess() {
                    //do smth when picture is loaded successfully
                }

                override fun onError() {
                    //do smth when there is picture loading error
                }
            })
        textDescription!!.text = mDescription
        textPoints!!.text = mPoints
        if (AppPrefenceManager.getUserType(mContext) == "7") {
            textAddCart!!.visibility = View.GONE
            llDistributor!!.visibility = View.GONE
        } else {
            textAddCart!!.visibility = View.VISIBLE
            llDistributor!!.visibility = View.VISIBLE
        }
    }

    fun addToCart() {
        try {
            val name = arrayOf("cust_id", "gift_id", "quantity")
            val values = arrayOf(
                AppPrefenceManager.getCustomerId(mContext),
                mGiftId,
                editQty!!.text.toString().trim { it <= ' ' })
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
                                    editQty!!.setText("")
                                    textCart!!.text = cart_count
                                } else if (response.equals("2", ignoreCase = true)) {
                                    val toast = CustomToast(mContext)
                                    toast.show(38)
                                } else {
                                    val toast = CustomToast(mContext)
                                    toast.show(39)
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
        } else if (v === textAddCart) {
            if (editQty!!.text.toString().trim { it <= ' ' } == "") {
                val toast = CustomToast(mContext)
                toast.show(40)
            } else {
                addToCart()
            }
        }
    }
}