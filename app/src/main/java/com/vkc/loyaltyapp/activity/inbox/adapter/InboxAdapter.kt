package com.vkc.loyaltyapp.activity.inbox.adapter

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.activity.inbox.model.InboxModel
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.LinearLayout
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.LayoutInflater
import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.vkc.loyaltyapp.R
import com.squareup.picasso.Picasso
import java.util.ArrayList

/**
 * Created by user2 on 4/12/17.
 */
class InboxAdapter(
    var mActivity: AppCompatActivity,
    var listInbox: ArrayList<InboxModel>
) : BaseAdapter() {
    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return listInbox.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return listInbox.get(position)
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    internal class ViewHolder {
        var imageTile: ImageView? = null
        var textTitle: TextView? = null
        var textDate: TextView? = null
        var linearLayout: LinearLayout? = null
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
                .inflate(R.layout.item_inbox_list, null)
            viewHolder = ViewHolder()
            viewHolder.textTitle = view.findViewById<View>(R.id.textTitle) as TextView
            viewHolder.textDate = view.findViewById<View>(R.id.textDate) as TextView
            viewHolder.imageTile = view.findViewById<View>(R.id.image_inbox) as ImageView
            viewHolder.linearLayout = view.findViewById<View>(R.id.thumbnail) as LinearLayout
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.textTitle!!.text = listInbox[position].title
        viewHolder!!.textDate!!.text = listInbox[position].createdon

        /*if (listInbox.get(position).getImage().equals("")) {
            viewHolder.imageTile.setVisibility(View.INVISIBLE);
        } else {
            // viewHolder.imageTile.setVisibility(View.VISIBLE);
            viewHolder.imageTile.setVisibility(View.VISIBLE);
            String image_url = listInbox.get(position).getImage();
           // image_url=image_url.replaceAll(" ","%20");
            Picasso.with(mActivity).load(listInbox.get(position).getImage()).into(viewHolder.imageTile);
        } */if (listInbox[position].notification_type == "2") {
            viewHolder.imageTile!!.visibility = View.VISIBLE
            Picasso.with(mActivity).load(R.drawable.pdf).into(viewHolder.imageTile)
        } else {
            // viewHolder.imageTile.setVisibility(View.VISIBLE);
            viewHolder.imageTile!!.visibility = View.VISIBLE
            val image_url = listInbox[position].image
            // image_url=image_url.replaceAll(" ","%20");
            Picasso.with(mActivity).load(R.drawable.bell).into(viewHolder.imageTile)
        }


        /*  viewHolder.textName.setText(AppController.listDealers.get(position)
                .getName());


        viewHolder.checkBox.setChecked(AppController.listDealers.get(
                position).isChecked());*/return view
    }
}