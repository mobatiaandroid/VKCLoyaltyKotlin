package com.vkc.loyaltyapp.activity.redeem.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.activity.dealers.model.DealerModel
import com.vkc.loyaltyapp.activity.redeem.model.RedeemModel
import java.util.*

/**
 * Created by user2 on 3/8/17.
 */
class RedeemAdapter     // AppController.listDealers.clear();

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
        var textDate: TextView? = null
        var textType: TextView? = null
        var textTitle: TextView? = null
        var textStatus: TextView? = null
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
                .inflate(R.layout.item_redeem_list, null)
            viewHolder = ViewHolder()
            viewHolder.textDate = view.findViewById<View>(R.id.textDate) as TextView
            viewHolder.textType = view.findViewById<View>(R.id.textType) as TextView
            viewHolder.textTitle = view.findViewById<View>(R.id.textTitle) as TextView
            viewHolder.textStatus = view.findViewById<View>(R.id.textStatus) as TextView


            //   viewHolder.checkBox.setOnCheckedChangeListener(null);
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.textDate!!.text = listModel[position].date
        viewHolder!!.textType!!.text = listModel[position].gift_type
        viewHolder.textTitle!!.text = listModel[position].title
        viewHolder.textStatus!!.text = listModel[position].quantity
        return view
    }
}