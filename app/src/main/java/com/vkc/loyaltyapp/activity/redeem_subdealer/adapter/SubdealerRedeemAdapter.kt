package com.vkc.loyaltyapp.activity.redeem_subdealer.adapter

import androidx.appcompat.app.AppCompatActivity
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.TextView
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.app.Activity
import android.view.View
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.activity.dealers.model.DealerModel
import com.vkc.loyaltyapp.activity.redeem.model.RedeemModel
import java.util.ArrayList

/**
 */
class SubdealerRedeemAdapter     // AppController.listDealers.clear();

// this.notifyDataSetChanged();
//System.out.println("Length" + listModel.size());
// mLayoutInflater = LayoutInflater.from(mActivity);
    (
    var mActivity: AppCompatActivity,
    var listModel: ArrayList<RedeemModel>
) : BaseAdapter() {
    var mLayoutInflater: LayoutInflater? = null
    private val mOriginalValues // Original Values
            : ArrayList<DealerModel>? = null
    private val mDisplayedValues: ArrayList<DealerModel>? = null
    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return listModel.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return listModel.get(position)
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    internal class ViewHolder {
        var textRetailer: TextView? = null
        var textGift: TextView? = null
        var textCoupon: TextView? = null
        var textQty: TextView? = null
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, view: View, parent: ViewGroup): View {
        var view = view
        var viewHolder: ViewHolder? = null
        val v = view
        val mInflater = mActivity
            .getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (view == null) {
            view = mInflater
                .inflate(R.layout.item_subdealer_redeem_list, null)
            viewHolder = ViewHolder()
            viewHolder.textRetailer = view.findViewById<View>(R.id.textRetailer) as TextView
            viewHolder.textGift = view.findViewById<View>(R.id.textType) as TextView
            viewHolder.textCoupon = view.findViewById<View>(R.id.textTitle) as TextView
            viewHolder.textQty = view.findViewById<View>(R.id.textStatus) as TextView


            //   viewHolder.checkBox.setOnCheckedChangeListener(null);
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.textRetailer!!.text = listModel[position].retailer_name
        viewHolder!!.textGift!!.text = listModel[position].title
        viewHolder.textCoupon!!.text = listModel[position].point
        viewHolder.textQty!!.text = listModel[position].quantity
        return view
    }
}