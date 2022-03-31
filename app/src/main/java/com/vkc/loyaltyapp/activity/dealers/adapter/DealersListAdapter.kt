package com.vkc.loyaltyapp.activity.dealers.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.activity.dealers.model.DealerModel
import com.vkc.loyaltyapp.appcontroller.AppController
import java.util.*

/**
 * Created by user2 on 8/8/17.
 */
class DealersListAdapter(
    var mActivity: AppCompatActivity,
    listModel: ArrayList<DealerModel?>?
) : BaseAdapter() {
    var mLayoutInflater: LayoutInflater? = null
    var listModel: ArrayList<DealerModel>? = null
    private val mOriginalValues // Original Values
            : ArrayList<DealerModel>? = null
    private val mDisplayedValues: ArrayList<DealerModel>? = null
    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return AppController.listDealers.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return AppController.listDealers.get(position)
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
                    AppController.listDealers[getPosition]
                        .setIsChecked(isChecked)
                })
            //   viewHolder.checkBox.setOnCheckedChangeListener(null);
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.checkBox!!.tag = position
        viewHolder!!.textName!!.text = AppController.listDealers[position]
            .name
        viewHolder.checkBox!!.isChecked = AppController.listDealers[position].isChecked
        return view
    }

    init {
        AppController.listDealers = listModel
    }
}