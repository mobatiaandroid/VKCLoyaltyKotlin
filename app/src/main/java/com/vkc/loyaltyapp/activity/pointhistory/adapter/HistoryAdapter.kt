package com.vkc.loyaltyapp.activity.pointhistory.adapter

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.activity.pointhistory.model.HistoryModel
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.TextView
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.app.Activity
import android.view.View
import com.vkc.loyaltyapp.R

class HistoryAdapter     // this.notifyDataSetChanged();
// System.out.println("Length" + listModel.size());
// mLayoutInflater = LayoutInflater.from(mActivity);
    (var mActivity: AppCompatActivity, var listHistory: List<HistoryModel>) : BaseAdapter() {
    var mLayoutInflater: LayoutInflater? = null
    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return listHistory.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return null
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    internal class ViewHolder {
        var textType: TextView? = null
        var textPoints: TextView? = null
        var textToUser: TextView? = null
        var textDate: TextView? = null
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, view: View, parent: ViewGroup): View {
        var viewHolder: ViewHolder? = null
        var v = view
        if (view == null) {
            val inflater = mActivity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.item_history, null)
            viewHolder = ViewHolder()
        } else {
            viewHolder = v.tag as ViewHolder
        }
        if (position % 2 == 1) {
            // view.setBackgroundColor(Color.BLUE);
            v.setBackgroundColor(
                mActivity.resources.getColor(
                    R.color.list_row_color_grey
                )
            )
        } else {
            v.setBackgroundColor(
                mActivity.resources.getColor(
                    R.color.list_row_color_white
                )
            )
        }
        viewHolder.textType = v.findViewById<View>(R.id.textType) as TextView
        viewHolder!!.textPoints = v.findViewById<View>(R.id.textPoints) as TextView
        viewHolder.textToUser = v.findViewById<View>(R.id.textToUser) as TextView
        viewHolder.textDate = v.findViewById<View>(R.id.textDate) as TextView
        v.tag = viewHolder
        viewHolder.textType!!.text = listHistory[position].type
        viewHolder.textPoints!!.text = listHistory[position].points
        if (listHistory[position].to_name.length > 0) {
            viewHolder.textToUser!!.text = (listHistory[position]
                .to_name
                    + " / "
                    + listHistory[position].to_role)
        } else {
            viewHolder.textToUser!!.text = ""
        }
        viewHolder.textDate!!.text = listHistory[position].dateValue
        return v
    }
}