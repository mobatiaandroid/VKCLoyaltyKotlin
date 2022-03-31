package com.vkc.loyaltyapp.activity.gifts.adapter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.activity.gifts.model.GiftsModel
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.vkc.loyaltyapp.R
import com.squareup.picasso.Picasso
import com.vkc.loyaltyapp.utils.CustomToast
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.ImageView
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList

/**
 * Created by user2 on 2/8/17.
 */
class GiftsAdapter(
    var mContext: AppCompatActivity,
    var listGifts: List<GiftsModel>,
    var textCart: TextView,
    var textBalance: TextView,
    var textTotal: TextView
) : RecyclerView.Adapter<GiftsAdapter.MyViewHolder>(), VKCUrlConstants {
    var personImages: ArrayList<*>? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // infalte the item Layout
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(v) // pass the view to View Holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = listGifts[position].title
        holder.points.text = "Coupons :" + listGifts[position].point
        val gift_name = listGifts[position].image.replace(" ".toRegex(), "%20")
        Picasso.with(mContext).load(gift_name).resize(300, 300).centerInside().into(holder.image)
        // implement setOnClickListener event on item view.
        holder.llCart.setOnClickListener {
            if (holder.editQty.text.toString().trim { it <= ' ' } == "") {
                val toast = CustomToast(mContext)
                toast.show(40)
            } else if (holder.editQty.text.toString().trim { it <= ' ' } == "0") {
                val toast = CustomToast(mContext)
                toast.show(59)
            } else {
                addToCart(holder.editQty, listGifts[position].id)
            }
        }
    }

    override fun getItemCount(): Int {
        return listGifts.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // init the item view's
        var name: TextView
        var points: TextView
        var image: ImageView
        var llCart: LinearLayout
        var editQty: EditText

        init {
            // get the reference of item view's
            name = itemView.findViewById<View>(R.id.name) as TextView
            image = itemView.findViewById<View>(R.id.image) as ImageView
            points = itemView.findViewById<View>(R.id.point) as TextView
            llCart = itemView.findViewById<View>(R.id.llCart) as LinearLayout
            editQty = itemView.findViewById<View>(R.id.editQty) as EditText
        }
    }

    fun addToCart(editQty: EditText, mGiftId: String) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editQty.windowToken, 0)
        try {
            val name = arrayOf("cust_id", "gift_id", "quantity", "gift_type")
            val values = arrayOf(
                AppPrefenceManager.getCustomerId(mContext),
                mGiftId,
                editQty.text.toString().trim { it <= ' ' },
                "1"
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
                                    cartItems
                                    editQty.setText("")
                                    textCart.text = cart_count
                                } else if (response.equals("2", ignoreCase = true)) {
                                    val toast = CustomToast(mContext)
                                    toast.show(38)
                                    editQty.setText("")
                                } else if (response == "5") {
                                    val toast = CustomToast(
                                        mContext
                                    )
                                    toast.show(61)
                                    editQty.setText("")
                                } else {
                                    val toast = CustomToast(mContext)
                                    toast.show(39)
                                    editQty.setText("")
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
                                    textBalance.text = balance_points
                                    textTotal.text = total_points
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