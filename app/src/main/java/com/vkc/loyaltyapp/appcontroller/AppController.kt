package com.vkc.loyaltyapp.appcontroller

import android.text.TextUtils
import androidx.multidex.MultiDexApplication
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.vkc.loyaltyapp.appcontroller.AppController
import com.android.volley.toolbox.Volley
import com.vkc.loyaltyapp.activity.dealers.model.DealerModel
import com.vkc.loyaltyapp.utils.LruBitmapCache
import com.vkc.loyaltyapp.utils.ConnectivityReceiver.ConnectivityReceiverListener
import com.vkc.loyaltyapp.utils.ConnectivityReceiver
import com.vkc.loyaltyapp.activity.inbox.model.InboxModel
import com.vkc.loyaltyapp.activity.subdealerredeem.model.RetailerModel
import com.vkc.loyaltyapp.activity.shopimage.model.ImageListModel
import java.util.ArrayList
import kotlin.jvm.Synchronized

/**
 * Created by Bibin Johnson
 */
class AppController : MultiDexApplication() {
    private var mRequestQueue: RequestQueue? = null
    private var mImageLoader: ImageLoader? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            }
            return mRequestQueue
        }
    val imageLoader: ImageLoader?
        get() {
            requestQueue
            if (mImageLoader == null) {
                mImageLoader = ImageLoader(
                    mRequestQueue,
                    LruBitmapCache()
                )
            }
            return mImageLoader
        }

    fun <T> addToRequestQueue(req: Request<T>, tag: String?) {
        // set the default tag if tag is empty
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        requestQueue!!.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        requestQueue!!.add(req)
    }

    fun cancelPendingRequests(tag: Any?) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    fun setConnectivityListener(listener: ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    companion object {
        val TAG = AppController::class.java
            .simpleName
        var listNotification = ArrayList<InboxModel>()

        @get:Synchronized
        var instance: AppController? = null
            private set
        var listDealers = ArrayList<DealerModel>()
        var listRetailers = ArrayList<RetailerModel>()
        @JvmField
        var imageList = ArrayList<ImageListModel>()
    }
}