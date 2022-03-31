package com.vkc.loyaltyapp.activity.inbox

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.manager.HeaderManager
import android.widget.LinearLayout
import android.os.Bundle
import com.vkc.loyaltyapp.R
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import com.vkc.loyaltyapp.appcontroller.AppController
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import com.vkc.loyaltyapp.activity.inbox.InboxDetailsActivity
import com.vkc.loyaltyapp.activity.pdf.PDFViewActivity
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import org.json.JSONArray
import com.vkc.loyaltyapp.activity.inbox.model.InboxModel
import com.vkc.loyaltyapp.activity.inbox.adapter.InboxAdapter
import com.vkc.loyaltyapp.utils.CustomToast
import android.widget.TextView

/**
 * Created by user2 on 4/12/17.
 */
class InboxDetailsActivity : AppCompatActivity(), View.OnClickListener, VKCUrlConstants {
    var mContext: AppCompatActivity? = null
    var headermanager: HeaderManager? = null
    var relativeHeader: LinearLayout? = null
    var mImageBack: ImageView? = null
    var imageNotification: ImageView? = null
    var title: String? = null
    var message: String? = null
    var created_on: String? = null
    var image: String? = null
    var date_from: String? = null
    var date_to: String? = null
    var textTitle: TextView? = null
    var textPinch: TextView? = null
    var webViewMessage: WebView? = null
    var webViewImage: WebView? = null
    var position = 0
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox_details)
        mContext = this
        val intent = intent
        position = intent.extras!!.getInt("position")
        initUI()
    }

    private fun initUI() {
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        headermanager =
            HeaderManager(this@InboxDetailsActivity, resources.getString(R.string.inbox))
        headermanager!!.getHeader(relativeHeader, 1)
        mImageBack = headermanager!!.leftButton
        headermanager!!.setButtonLeftSelector(
            R.drawable.left,
            R.drawable.back
        )
        textTitle = findViewById<View>(R.id.textTitle) as TextView
        textPinch = findViewById<View>(R.id.textPinch) as TextView
        imageNotification = findViewById<View>(R.id.imageNotification) as ImageView
        webViewMessage = findViewById<View>(R.id.webMessage) as WebView
        webViewImage = findViewById<View>(R.id.webImage) as WebView
        message = AppController.listNotification[position].message
        image = AppController.listNotification[position].image
        webViewImage!!.settings.builtInZoomControls = true
        webViewImage!!.settings.displayZoomControls = false
        title = AppController.listNotification[position].title
        if (image?.trim { it <= ' ' }?.length!! > 0) {
            webViewImage!!.visibility = View.VISIBLE
            textPinch!!.visibility = View.VISIBLE
            // imageNotification.setVisibility(View.VISIBLE);
            //image = image.replaceAll(" ", "%20");

            //  Picasso.with(mContext).load(image).into(imageNotification);
            val summary =
                "<html><body style=\"color:white;\"><center><img  src='$image'width='100%', height='auto'\"></center></body></html>"
            webViewImage!!.setBackgroundColor(Color.TRANSPARENT)
            webViewImage!!.loadData(summary, "text/html", null)
        } else {
            // imageNotification.setVisibility(View.GONE);
            webViewImage!!.visibility = View.GONE
            textPinch!!.visibility = View.GONE
        }
        mImageBack!!.setOnClickListener(this)
        textTitle!!.text = title
        val summary = "<html><body style=\"color:white;\">$message</body></html>"
        webViewMessage!!.setBackgroundColor(Color.TRANSPARENT)
        webViewMessage!!.loadData(summary, "text/html", null)
    }

    override fun onClick(v: View) {
        if (v === mImageBack) {
            finish()
        }
    }
}