package com.vkc.loyaltyapp.activity.news.adapter

import androidx.recyclerview.widget.RecyclerView
import android.app.Activity
import android.graphics.Color
import android.widget.TextView
import com.vkc.loyaltyapp.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import com.vkc.loyaltyapp.activity.news.model.ProductModel
import androidx.viewpager.widget.PagerAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.vkc.loyaltyapp.activity.news.adapter.ColorAdapter
import com.vkc.loyaltyapp.activity.news.model.ColorModel
import com.vkc.loyaltyapp.activity.news.model.NewsListModel
import java.util.ArrayList

class ColorAdapter : RecyclerView.Adapter<ColorAdapter.MyViewHolder> {
    private var colorList: ArrayList<ColorModel>? = null
    var activity: Activity

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageColor: ImageView
        var txtColorName: TextView
        var recyclerColor: RecyclerView? = null

        init {

            /* textDate = (TextView) view.findViewById(R.id.textDate);
            textType = (TextView) view.findViewById(R.id.textType);
            textTitle = (TextView) view.findViewById(R.id.textTitle);
            textStatus = (TextView) view.findViewById(R.id.textStatus);*/txtColorName =
                view.findViewById<View>(R.id.txtColorName) as TextView
            imageColor = view.findViewById<View>(R.id.imgColor) as ImageView
        }
    }

    constructor(mActivity: Activity, colorList: ArrayList<ColorModel>?) {
        this.colorList = colorList
        activity = mActivity
    }

    constructor(mActivity: Activity) {
        activity = mActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val Color_value = colorList!![i].code
        if (Color_value.contains("#")) {
            val fillColor = Color.parseColor(Color_value)
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(fillColor)
            myViewHolder.imageColor.background = shape
            myViewHolder.txtColorName.text = colorList!![i].name
        }
        // GradientDrawable shape = drawCircle(fillColor);
        /* GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shape.setColor(fillColor);*/
    }

    override fun getItemCount(): Int {
        return colorList!!.size
    } //return newsList.size();
    /*public static GradientDrawable drawCircle(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shape.setColor(backgroundColor);
        return shape;
    }*/
}