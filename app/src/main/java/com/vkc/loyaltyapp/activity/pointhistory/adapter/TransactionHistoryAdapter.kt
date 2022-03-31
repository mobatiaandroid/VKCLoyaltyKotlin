package com.vkc.loyaltyapp.activity.pointhistory.adapter

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.activity.pointhistory.model.TransactionModel
import android.widget.BaseExpandableListAdapter
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.activity.pointhistory.model.HistoryModel
import android.view.ViewGroup
import android.view.LayoutInflater
import com.vkc.loyaltyapp.R
import android.widget.TextView
import android.app.Activity
import android.content.Context
import android.view.View
import java.util.ArrayList

class TransactionHistoryAdapter(
    var mContext: AppCompatActivity,
    var listTransaction: List<TransactionModel>
) : BaseExpandableListAdapter(), VKCUrlConstants {
    lateinit var productList: ArrayList<HistoryModel>
    var positionValue = 0
    override fun getGroupCount(): Int {
        return listTransaction.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        productList = listTransaction[groupPosition].listHistory
        //return listTransaction.get(positionValue).getListHistory().size();
        return productList.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return listTransaction[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return productList!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View, parent: ViewGroup
    ): View {
        var convertView = convertView
        if (convertView == null) {
            val infalInflater = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(
                R.layout.item_history_parent,
                null
            )
        }
        val textUser = convertView.findViewById<View>(R.id.textUser) as TextView
        val textPoint = convertView.findViewById<View>(R.id.textPoint) as TextView
        val textIcon = convertView.findViewById<View>(R.id.textIcon) as TextView
        textUser.text = listTransaction[groupPosition].userName
        textPoint.text = listTransaction[groupPosition].totPoints + " Coupons"
        positionValue = groupPosition
        if (isExpanded) {
            textIcon.text = "-"
        } else {
            textIcon.text = "+"
        }
        return convertView
    }

    internal class ViewHolder {
        var textType: TextView? = null
        var textPoints: TextView? = null
        var textToUser: TextView? = null
        var textDate: TextView? = null
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View, parent: ViewGroup
    ): View {
        /*System.out.println("Group Position:"+groupPosition+"Child Psoitiomn:"+childPosition);
        System.out.println("Count:"+listTransaction.get(groupPosition).getListHistory().size());*/
        var viewHolder: ViewHolder? = null
        var v = convertView
        if (v == null) {
            val inflater = mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.item_history, null)
            viewHolder = ViewHolder()
            v.tag = viewHolder
        }
        viewHolder = v.tag as ViewHolder
        viewHolder.textType = v.findViewById<View>(R.id.textType) as TextView
        viewHolder!!.textPoints = v.findViewById<View>(R.id.textPoints) as TextView
        viewHolder.textToUser = v.findViewById<View>(R.id.textToUser) as TextView
        viewHolder.textDate = v.findViewById<View>(R.id.textDate) as TextView


        //childPosition=childPosition-1;
        if (childPosition % 2 == 1) {
            // view.setBackgroundColor(Color.BLUE);
            v.setBackgroundColor(
                mContext.resources.getColor(
                    R.color.list_row_color_grey
                )
            )
        } else {
            v.setBackgroundColor(
                mContext.resources.getColor(
                    R.color.list_row_color_white
                )
            )
        }
        //	String date=productList.get(childPosition).getDateValue();
        viewHolder.textType!!.text = productList!![childPosition].type
        viewHolder.textPoints!!.text = productList!![childPosition].points
        if (productList!![childPosition].to_name.length > 0) {
            viewHolder.textToUser!!.text = (productList!![childPosition]
                .to_name
                    + " / "
                    + productList!![childPosition].to_role)
        } else {
            viewHolder.textToUser!!.text = ""
        }
        //System.out.println("Position Value:"+childPosition);
        viewHolder.textDate!!.text = productList!![childPosition].dateValue
        println("Date " + productList!![childPosition].dateValue)
        return v
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }
}