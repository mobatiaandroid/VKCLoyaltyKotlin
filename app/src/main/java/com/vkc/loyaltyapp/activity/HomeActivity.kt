package com.vkc.loyaltyapp.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lzyzsd.circleprogress.ArcProgress
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.activity.HomeActivity
import com.vkc.loyaltyapp.activity.common.SplashActivity
import com.vkc.loyaltyapp.activity.gifts.GiftsActivity
import com.vkc.loyaltyapp.activity.inbox.InboxActivity
import com.vkc.loyaltyapp.activity.issuepoint.IssuePointActivity
import com.vkc.loyaltyapp.activity.news.NewsListActivity
import com.vkc.loyaltyapp.activity.pointhistory.PointHistoryActivity
import com.vkc.loyaltyapp.activity.profile.ProfileActivity
import com.vkc.loyaltyapp.activity.shopimage.ShopImageActivity
import com.vkc.loyaltyapp.activity.subdealerredeem.SubdealerRedeemActivity
import com.vkc.loyaltyapp.appcontroller.AppController
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.utils.ConnectivityReceiver.ConnectivityReceiverListener
import com.vkc.loyaltyapp.utils.CustomToast
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import devlight.io.library.ArcProgressStackView
import org.json.JSONObject
import java.util.*

/**
 * Created by user2 on 26/7/17.
 */
class HomeActivity : AppCompatActivity(), View.OnClickListener, VKCUrlConstants,
    ConnectivityReceiverListener {
    var textPoints: TextView? = null
    var textVersion: TextView? = null
    var textNoPoint: TextView? = null
    var txtNews: TextView? = null
    var rlHide: RelativeLayout? = null
    var llPoints: LinearLayout? = null
    var llGifts: LinearLayout? = null
    var llProfile: LinearLayout? = null
    var llShop: LinearLayout? = null
    var llDescription: LinearLayout? = null
    var llInbox: LinearLayout? = null
    var mContext: AppCompatActivity? = null
    var myPoint = 0
    var arcProgress: ArcProgress? = null
    var btnIssue: Button? = null
    var mArcProgressStackView: ArcProgressStackView? = null
    var gift_status: String? = null
    private val mStartColors = IntArray(HomeActivity.MODEL_COUNT)
    private val mEndColors = IntArray(HomeActivity.MODEL_COUNT)
    var serverVersion: String? = null
    var imei_no: String? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mContext = this
        initUI()
        getMyPoints()
    }

    private fun initUI() {
        llShop = findViewById<View>(R.id.llShop) as LinearLayout
        llGifts = findViewById<View>(R.id.llGifts) as LinearLayout
        llPoints = findViewById<View>(R.id.llPoints) as LinearLayout
        llProfile = findViewById<View>(R.id.llProfile) as LinearLayout
        llInbox = findViewById<View>(R.id.llInbox) as LinearLayout
        textPoints = findViewById<View>(R.id.textPoint) as TextView
        textNoPoint = findViewById<View>(R.id.textNoPoint) as TextView
        textVersion = findViewById<View>(R.id.textVersion) as TextView
        txtNews = findViewById<View>(R.id.txtNews) as TextView
        rlHide = findViewById<View>(R.id.rlHide) as RelativeLayout
        rlHide!!.visibility = View.GONE
        textVersion!!.text = "Ver. " + getVersion()
        llDescription = findViewById<View>(R.id.llDescription) as LinearLayout
        arcProgress = findViewById<View>(R.id.arc_progress) as ArcProgress
        btnIssue = findViewById<View>(R.id.buttonIssue) as Button
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        mArcProgressStackView =
            findViewById<View>(R.id.arcProgressStackView) as ArcProgressStackView
        arcProgress!!.suffixText = ""
        arcProgress!!.strokeWidth = 15f
        arcProgress!!.max = 10000000
        arcProgress!!.bottomTextSize = 80f
        arcProgress!!.unfinishedStrokeColor = resources.getColor(R.color.white)
        arcProgress!!.textColor = resources.getColor(R.color.white)
        arcProgress!!.setBackgroundColor(resources.getColor(R.color.transparent))
        llProfile!!.setOnClickListener(this)
        llPoints!!.setOnClickListener(this)
        llGifts!!.setOnClickListener(this)
        llShop!!.setOnClickListener(this)
        btnIssue!!.setOnClickListener(this)
        llInbox!!.setOnClickListener(this)
        txtNews!!.setOnClickListener(this)
        if (AppPrefenceManager.getUserType(mContext) == "7") {
            btnIssue!!.visibility = View.VISIBLE
            mArcProgressStackView!!.visibility = View.GONE
            arcProgress!!.visibility = View.VISIBLE
            llDescription!!.visibility = View.GONE
            // textPoints.setVisibility(View.GONE);
        } else {
            btnIssue!!.visibility = View.GONE
            mArcProgressStackView!!.visibility = View.VISIBLE
            arcProgress!!.visibility = View.GONE
            // textPoints.setVisibility(View.VISIBLE);
            llDescription!!.visibility = View.VISIBLE
        }
        val startColors = resources.getStringArray(R.array.devlight)
        //  final String[] endColors = getResources().getStringArray(R.array.default_preview);
        val bgColors = resources.getStringArray(R.array.bg)

        // Parse colors
        for (i in 0 until HomeActivity.MODEL_COUNT) {
            mStartColors[i] = Color.parseColor(startColors[i])
            mEndColors[i] = Color.parseColor(bgColors[i])
        }
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                    if (report.areAllPermissionsGranted()) {
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(this@HomeActivity, ShopImageActivity::class.java))
                } else {
                    ActivityCompat.requestPermissions(
                        this@HomeActivity,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        101
                    )
                    //  Toast.makeText(mContext, "Unable to continue without granting permission for writing data to external storage", Toast.LENGTH_LONG).show();
                }
                return
            }
            101 -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(this@HomeActivity, ShopImageActivity::class.java))

                    //
                } else {
                    val myAppSettings = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
                            "package:$packageName"
                        )
                    )
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                    myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(myAppSettings)
                    Toast.makeText(
                        mContext,
                        "Unable to continue without granting permission for writing data to external storage and camera access",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llGifts -> if (AppPrefenceManager.getUserType(mContext) == "7") {
                /*CustomToast toast = new CustomToast(mContext);
                    toast.show(23);*/
                startActivity(Intent(this@HomeActivity, SubdealerRedeemActivity::class.java))
                overridePendingTransition(0, 0)
            } else {
/*
                    CustomToast toast = new CustomToast(mContext);
                    toast.show(31);*/
                startActivity(Intent(this@HomeActivity, GiftsActivity::class.java))
                overridePendingTransition(0, 0)
            }
            R.id.llPoints -> {
                startActivity(Intent(this@HomeActivity, PointHistoryActivity::class.java))
                overridePendingTransition(0, 0)
            }
            R.id.llInbox -> {
                startActivity(Intent(this@HomeActivity, NewsListActivity::class.java))
                overridePendingTransition(0, 0)
            }
            R.id.txtNews -> {
                startActivity(Intent(this@HomeActivity, InboxActivity::class.java))
                overridePendingTransition(0, 0)
            }
            R.id.llShop -> if (AppPrefenceManager.getUserType(mContext) == "7") {
                val toast = CustomToast(mContext)
                toast.show(23)
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(
                            mContext!!,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                mContext!!,
                                Manifest.permission.CAMERA
                            )
                        ) {

                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                            ActivityCompat.requestPermissions(
                                this@HomeActivity,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                101
                            )
                        } else {

                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(
                                this@HomeActivity,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                101
                            )

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        startActivity(Intent(this@HomeActivity, ShopImageActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                } else {
                    startActivity(Intent(this@HomeActivity, ShopImageActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            R.id.llProfile -> {
                startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                overridePendingTransition(0, 0)
            }
            R.id.buttonIssue -> {
                startActivity(Intent(this@HomeActivity, IssuePointActivity::class.java))
                overridePendingTransition(0, 0)
            }
        }
    }// CustomStatusDialog(RESPONSE_FAILURE);
    // Log.d("TAG", "Common error");
//CustomStatusDialog(RESPONSE_FAILURE);// 352711095056053 /*else {
///only api 21 down// TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.

    //only api 21 above
// 352711095056053// Get new Instance ID token//   models.add(new ArcProgressStackView.Model("Coupon Target : 1600", 110, mEndColors[0], mStartColors[0]));
//  System.out.println("imei_number condition" + imei_number);
// System.out.println("imei_number condition imei_no" + imei_no);
//    Log.i("percent ", "" + percent_value);
//  System.out.println("Response---Login" + successResponse);
    fun getMyPoints() {
        try {
            val name = arrayOf("cust_id", "role")
            val values = arrayOf(
                AppPrefenceManager.getCustomerId(mContext),
                AppPrefenceManager.getUserType(mContext)
            )
            val manager = VolleyWrapper(VKCUrlConstants.GET_LOYALTY_POINTS)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //  System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                val imei_number = objResponse.optString("imei_no")
                                if (status == "Success") {
                                    val points = objResponse.optString("loyality_point")
                                    gift_status = objResponse.optString("gift_status")
                                    myPoint = points.toInt()
                                    if (points == "0") {
                                        textNoPoint?.setVisibility(View.VISIBLE)
                                        textPoints?.setVisibility(View.GONE)
                                    } else {
                                        textPoints?.setText("$points Coupons")
                                        textNoPoint?.setVisibility(View.GONE)
                                    }
                                    val mul_val: Int = myPoint * 100
                                    val percent_value = mul_val / 3000
                                    //    Log.i("percent ", "" + percent_value);
                                    mArcProgressStackView?.setTextColor(Color.parseColor("#000000"))
                                    mArcProgressStackView?.setDrawWidthDimension(150f)
                                    if (AppPrefenceManager.getUserType(mContext) == "7") {
                                        btnIssue?.setVisibility(View.VISIBLE)
                                        mArcProgressStackView?.setVisibility(View.GONE)
                                        arcProgress?.setVisibility(View.VISIBLE)
                                        llDescription?.setVisibility(View.GONE)
                                        arcProgress?.setProgress(myPoint)
                                    } else {
                                        btnIssue?.setVisibility(View.GONE)
                                        mArcProgressStackView?.setVisibility(View.VISIBLE)
                                        arcProgress?.setVisibility(View.GONE)
                                        llDescription?.setVisibility(View.VISIBLE)
                                        val models = ArrayList<ArcProgressStackView.Model>()
                                        //   models.add(new ArcProgressStackView.Model("Coupon Target : 1600", 110, mEndColors[0], mStartColors[0]));
                                        models.add(
                                            ArcProgressStackView.Model(
                                                "",
                                                110F,
                                                mEndColors.get(0),
                                                mStartColors.get(0)
                                            )
                                        )
                                        models.add(
                                            ArcProgressStackView.Model(
                                                "",
                                                percent_value.toFloat(),
                                                mEndColors.get(1),
                                                mStartColors.get(1)
                                            )
                                        )
                                        mArcProgressStackView?.setModels(models)
                                    }
                                    //  System.out.println("imei_number condition" + imei_number);
                                    // System.out.println("imei_number condition imei_no" + imei_no);
                                    if (Build.VERSION.SDK_INT >= 29) {
                                        FirebaseInstanceId.getInstance().instanceId
                                            .addOnCompleteListener(OnCompleteListener { task ->
                                                if (!task.isSuccessful) {
                                                    return@OnCompleteListener
                                                }

                                                // Get new Instance ID token
                                                imei_no = task.result.id
                                            })
                                        if (imei_no != imei_number) {
                                            // 352711095056053
                                            if (imei_no == "web") {
                                            } else {
                                                alertSignout(mContext)
                                            }
                                        }
                                    } else if (Build.VERSION.SDK_INT >= 26) {
                                        val phoneCount = tm.phoneCount
                                        if (ActivityCompat.checkSelfPermission(
                                                mContext!!,
                                                Manifest.permission.READ_PHONE_STATE
                                            ) != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            return
                                        }

                                        //only api 21 above
                                        if (phoneCount > 1) {
                                            imei_no = tm.getImei(0)
                                        } else {
                                            imei_no = tm.imei
                                        }
                                    } else {
                                        //only api 21 down
                                        imei_no = tm.deviceId
                                    }
                                }
                                if (imei_no != imei_number) {
                                    // 352711095056053
                                    if (imei_no == "web") {
                                    } else {
                                        alertSignout(mContext)
                                    }
                                } /*else {

                                        CustomToast toast = new CustomToast(mContext);
                                        toast.show(0);
                                    }*/
                                getAppVesion()
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        } else {
                            val toast = CustomToast(mContext)
                            toast.show(0)
                        }
                    }

                    override fun responseFailure(failureResponse: String) {
                        //CustomStatusDialog(RESPONSE_FAILURE);
                    }
                })
        } catch (e: Exception) {
            // CustomStatusDialog(RESPONSE_FAILURE);
            e.printStackTrace()
            // Log.d("TAG", "Common error");
        }
    }

    override fun onRestart() {
        super.onRestart()
        getMyPoints()
    }

    private fun getVersion(): String {
        var packageinfo: PackageInfo? = null
        try {
            packageinfo = getPackageManager().getPackageInfo(
                getPackageName(), 0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return packageinfo!!.versionName.toString()
    }

    fun getAppVesion() {
        try {
            val name = arrayOf<String>()
            val values = arrayOf<String>()
            val manager = VolleyWrapper(VKCUrlConstants.GET_APP_VERSION_URL)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status == "Success") {
                                    serverVersion = objResponse.optString("appversion")
                                    if (serverVersion == getVersion()) {
                                        deviceRegister()
                                        rlHide?.setVisibility(View.GONE)
                                    } else {
                                        rlHide?.setVisibility(View.VISIBLE)
                                        HomeActivity.Companion.updateApp(mContext)
                                    }
                                } else {
                                    val toast = CustomToast(mContext)
                                    toast.show(0)
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        } else {
                            val toast = CustomToast(mContext)
                            toast.show(0)
                        }
                    }

                    override fun responseFailure(failureResponse: String) {
                        //CustomStatusDialog(RESPONSE_FAILURE);
                    }
                })
        } catch (e: Exception) {
            // CustomStatusDialog(RESPONSE_FAILURE);
            e.printStackTrace()
            //Log.d("TAG", "Common error");
        }
    }

    fun deviceRegister() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result.token
                AppPrefenceManager.saveToken(mContext, token)
            })
        try {
            val name = arrayOf("cust_id", "role", "device_id")
            val values = arrayOf(
                AppPrefenceManager.getCustomerId(mContext),
                AppPrefenceManager.getUserType(mContext),
                AppPrefenceManager.getToken(mContext)
            )
            val manager = VolleyWrapper(VKCUrlConstants.DEVICE_REGISTRATION_API)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status == "Success") {
                                } else if (status == "Empty") {
                                    deviceRegister()
                                } else {
                                    val toast = CustomToast(mContext)
                                    toast.show(0)
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        } else {
                            val toast = CustomToast(mContext)
                            toast.show(0)
                        }
                    }

                    override fun responseFailure(failureResponse: String) {
                        //CustomStatusDialog(RESPONSE_FAILURE);
                    }
                })
        } catch (e: Exception) {
            // CustomStatusDialog(RESPONSE_FAILURE);
            e.printStackTrace()
            //Log.d("TAG", "Common error");
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        AppController.instance.setConnectivityListener(this@HomeActivity)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
        } else {
            val toast = CustomToast(mContext)
            toast.show(58)
        }
    }

    fun alertSignout(act: Activity?) {
        val builder = AlertDialog.Builder(act)
        builder.setTitle("Alert !") // act.getString(R.string.dialog_title_update_app)
            .setMessage("You are not allowed to use the VKC Loyalty on multiple device . The system will logout from this device now.") //
            .setNegativeButton(
                "Ok"
            ) { dialog, whichButton ->
                try {
                    dialog.cancel()
                    AppPrefenceManager.saveLoginStatusFlag(mContext, "no")
                    AppPrefenceManager.setIsVerifiedOTP(mContext, "no")
                    //AppPrefenceManager.saveUserType(mContext, "");
                    AppPrefenceManager.saveDealerCount(mContext, 0)
                    val intent = Intent(this@HomeActivity, SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } catch (anfe: ActivityNotFoundException) {
                }
            }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    companion object {
        const val MODEL_COUNT = 2
        fun updateApp(act: AppCompatActivity?) {
            val appPackageName = act!!.packageName
            val builder = androidx.appcompat.app.AlertDialog.Builder(
                act
            )
            builder.setTitle("New Update Available !") // act.getString(R.string.dialog_title_update_app)
                .setMessage("Please update the app to avail new features") //
                /*.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        act.startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("market://details?id="
                                                        + appPackageName)));
                                    } catch (ActivityNotFoundException anfe) {
                                        act.startActivity(new Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://play.google.com/store/apps/details?id="
                                                        + appPackageName)));
                                    }
                                }
                            }
                    )*/
                .setPositiveButton("Ok") { dialogInterface, i ->
                    try {
                        act.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "market://details?id="
                                            + appPackageName
                                )
                            )
                        )
                    } catch (anfe: ActivityNotFoundException) {
                        act.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "https://play.google.com/store/apps/details?id="
                                            + appPackageName
                                )
                            )
                        )
                    }
                }
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }
}