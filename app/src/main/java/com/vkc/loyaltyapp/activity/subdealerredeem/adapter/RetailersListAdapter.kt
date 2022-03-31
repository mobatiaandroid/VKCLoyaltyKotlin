package com.vkc.loyaltyapp.activity.subdealerredeem.adapter

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.activity.subdealerredeem.model.RetailerModel
import android.widget.BaseAdapter
import android.view.LayoutInflater
import com.vkc.loyaltyapp.appcontroller.AppController
import android.widget.TextView
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.app.Activity
import android.view.View
import android.widget.CheckBox
import com.vkc.loyaltyapp.R
import android.widget.CompoundButton
import com.vkc.loyaltyapp.activity.dealers.model.DealerModel
import java.util.ArrayList

/**
 * Created by user2 on 3/4/18.
 */
class RetailersListAdapter(
    var mActivity: AppCompatActivity,
    listModel: ArrayList<RetailerModel?>?
) : BaseAdapter() {
    var mLayoutInflater: LayoutInflater? = null
    var listModel: ArrayList<DealerModel>? = null
    private val mOriginalValues // Original Values
            : ArrayList<DealerModel>? = null
    private val mDisplayedValues: ArrayList<DealerModel>? = null
    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return AppController.listRetailers.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return AppController.listRetailers.get(position)
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    internal class ViewHolder {
        var textName: TextView? = null
        var checkBox: CheckBox? = null
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
                .inflate(R.layout.item_dealer_list, null)
            viewHolder = ViewHolder()
            viewHolder.textName = view.findViewById<View>(R.id.textViewName) as TextView
            viewHolder.checkBox = view.findViewById<View>(R.id.checkbox_dealer) as CheckBox
            viewHolder?.checkBox!!
                .setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> /*   String idValue = AppController.dealersModels.get(
                                    position).getId();*/
                    val getPosition = buttonView.tag as Int
                    AppController.listRetailers[getPosition]
                        .setIsChecked(isChecked)
                })
            //   viewHolder.checkBox.setOnCheckedChangeListener(null);
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.checkBox!!.tag = position
        viewHolder!!.textName!!.text = AppController.listRetailers[position]
            .user_name
        viewHolder.checkBox!!.isChecked = AppController.listRetailers[position].isChecked
        return view
    }

    init {
        // AppController.listDealers.clear();
        AppController.listRetailers = listModel
        // this.notifyDataSetChanged();
        //System.out.println("Length" + listModel.size());
        // mLayoutInflater = LayoutInflater.from(mActivity);
    }
}