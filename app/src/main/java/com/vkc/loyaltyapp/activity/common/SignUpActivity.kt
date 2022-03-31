package com.vkc.loyaltyapp.activity.common

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.utils.OtpReceivedInterface
import com.google.android.gms.common.api.GoogleApiClient
import com.vkc.loyaltyapp.activity.common.model.StateModel
import com.vkc.loyaltyapp.activity.common.model.DistrictModel
import com.vkc.loyaltyapp.utils.SMSReceiver
import android.os.Bundle
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.utils.AppSignatureHashHelper
import android.os.Build
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.InstanceIdResult
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.widget.AdapterView.OnItemSelectedListener
import android.text.TextWatcher
import android.text.Editable
import com.vkc.loyaltyapp.utils.UtilityMethods
import com.vkc.loyaltyapp.utils.CustomToast
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import org.json.JSONArray
import android.graphics.drawable.ColorDrawable
import android.content.Intent
import com.vkc.loyaltyapp.activity.HomeActivity
import com.vkc.loyaltyapp.activity.dealers.DealersActivity
import com.vkc.loyaltyapp.activity.common.SignUpActivity.DialogUpdate
import com.google.android.gms.common.ConnectionResult
import com.vkc.loyaltyapp.activity.common.SignUpActivity
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.IntentFilter
import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.auth.api.Auth
import java.lang.Exception
import java.util.ArrayList

/**
 * Created by user2 on 28/7/17.
 */
class SignUpActivity : AppCompatActivity(), View.OnClickListener, VKCUrlConstants,
    OtpReceivedInterface, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    var buttonRegister: Button? = null
    var editMobile: EditText? = null
    var editOwner: EditText? = null
    var editShop: EditText? = null
    var editPlace: EditText? = null
    var editPin: EditText? = null
    var editCustomer: EditText? = null
    var editDoor: EditText? = null
    var editAddress: EditText? = null
    var editLandMark: EditText? = null
    var mContext: AppCompatActivity? = null
    var editOtp1: EditText? = null
    var editOtp2: EditText? = null
    var editOtp3: EditText? = null
    var editOtp4: EditText? = null
    var otpValue = ""
    var isNewReg = false
    var llCustID: LinearLayout? = null
    var llAddress: LinearLayout? = null
    var llUserType: LinearLayout? = null
    var imageSearch: ImageView? = null
    var imageGetData: ImageView? = null
    var imei_no = ""
    var mobileNo: String? = null
    var selectedState: String? = null
    var selectedDistrict: String? = null
    var stateId: String? = null
    var district: String? = null
    var spinnerState: Spinner? = null
    var spinnerDist: Spinner? = null
    var spinnerUserType: Spinner? = null
    var listState: ArrayList<String>? = null
    var listDistrict: ArrayList<String>? = null
    var userTypeList: ArrayList<String>? = null
    var stateList: ArrayList<StateModel>? = null
    var distList: ArrayList<DistrictModel>? = null
    private var userType: String? = null
    var isNewMobile = "0"
    private var smsReceiver: SMSReceiver? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    var smsKey: String? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mContext = this
        val appSignatureHashHelper = AppSignatureHashHelper(this)
        smsKey = appSignatureHashHelper.appSignatures[0]
        // Log.i("Hash Key", "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));
        smsautodetection()
        startSMSListener()
        initUI()
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    123
                )
            } else {
                val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT >= 29) {
                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            imei_no = task.result.id
                        })
                } else if (Build.VERSION.SDK_INT >= 26) {
                    //only api 21 above
                    val phoneCount = tm.phoneCount
                    imei_no = if (phoneCount > 1) {
                        tm.getImei(0)
                    } else {
                        tm.imei
                    }
                } else {
                    //only api 21 down
                    imei_no = tm.deviceId
                }
            }
        } else {
            val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            imei_no = tm.deviceId
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(
                        this,
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
                if (Build.VERSION.SDK_INT >= 29) {
                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            imei_no = task.result.id
                        })
                } else if (Build.VERSION.SDK_INT >= 26) {
                    //only api 21 above
                    val phoneCount = tm.phoneCount
                    imei_no = if (phoneCount > 1) {
                        tm.getImei(0)
                    } else {
                        tm.imei
                    }
                } else {
                    //only api 21 down
                    imei_no = tm.deviceId
                }
            } else {
            }
        }
    }

    private fun initUI() {
        listState = ArrayList()
        listDistrict = ArrayList()
        stateList = ArrayList()
        distList = ArrayList()
        userTypeList = ArrayList()
        buttonRegister = findViewById<View>(R.id.buttonRegister) as Button
        editMobile = findViewById<View>(R.id.editMobile) as EditText
        editOwner = findViewById<View>(R.id.editOwner) as EditText
        editShop = findViewById<View>(R.id.editShop) as EditText
        editDoor = findViewById<View>(R.id.editDoor) as EditText
        editAddress = findViewById<View>(R.id.editAddress) as EditText
        editLandMark = findViewById<View>(R.id.editLandMark) as EditText
        spinnerState = findViewById<View>(R.id.spinnerState) as Spinner
        spinnerUserType = findViewById<View>(R.id.spinnerUserType) as Spinner
        spinnerDist = findViewById<View>(R.id.spinnerDistrict) as Spinner
        editPlace = findViewById<View>(R.id.editPlace) as EditText
        editPin = findViewById<View>(R.id.editPin) as EditText
        editCustomer = findViewById<View>(R.id.editCustId) as EditText
        imageSearch = findViewById<View>(R.id.imageSearch) as ImageView
        imageGetData = findViewById<View>(R.id.imageFetchData) as ImageView
        llCustID = findViewById<View>(R.id.llCustId) as LinearLayout
        llAddress = findViewById<View>(R.id.llAddress) as LinearLayout
        llUserType = findViewById<View>(R.id.llUserType) as LinearLayout
        llCustID!!.visibility = View.GONE
        llAddress!!.visibility = View.GONE
        llUserType!!.visibility = View.GONE
        editOwner!!.isEnabled = false
        editShop!!.isEnabled = false
        editPlace!!.isEnabled = false
        editPin!!.isEnabled = false
        editOwner!!.filters = arrayOf<InputFilter>(AllCaps())
        editPlace!!.filters = arrayOf<InputFilter>(AllCaps())
        editShop!!.filters = arrayOf<InputFilter>(AllCaps())
        editDoor!!.filters = arrayOf<InputFilter>(AllCaps())
        editAddress!!.filters = arrayOf<InputFilter>(AllCaps())
        editLandMark!!.filters = arrayOf<InputFilter>(AllCaps())
        imageSearch!!.setOnClickListener(this)
        buttonRegister!!.setOnClickListener(this)
        editCustomer!!.setText("")
        userType = ""
        selectedState = ""
        userTypeList!!.clear()
        editMobile!!.setText("")
        userTypeList!!.add("User Type")
        userTypeList!!.add("Retailer")
        userTypeList!!.add("Subdealer")
        val adapter =
            ArrayAdapter(this@SignUpActivity, android.R.layout.simple_spinner_item, userTypeList!!)
        spinnerUserType!!.adapter = adapter
        getState()
        /*new Handler().postDelayed(new Runnable() {
            public void run() {
                // play.setText("Play");
                CustomToast toast = new CustomToast(mContext);
                toast.show(0);
            }
        }, 5000);*/editCustomer!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        getData()
                        return@OnKeyListener true
                    }
                    else -> {
                    }
                }
            }
            false
        })
        spinnerState!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    (parent.getChildAt(0) as TextView).textSize = 14f
                    (parent.getChildAt(0) as TextView).isAllCaps = true
                    stateId = stateList!![position - 1].stateId
                    selectedState = stateList!![position - 1].stateName
                    getDistrict()
                } else {
                    stateId = ""
                    selectedState = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedState = ""
                stateId = ""
            }
        }
        imageGetData!!.setOnClickListener { getData() }
        spinnerUserType!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    (parent.getChildAt(0) as TextView).textSize = 14f
                    (parent.getChildAt(0) as TextView).isAllCaps = true
                    if (userTypeList!![position] == "Retailer") {
                        userType = "5"
                    } else if (userTypeList!![position] == "Subdealer") {
                        userType = "7"
                    }
                } else {
                    userType = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                userType = ""
            }
        }
        spinnerDist!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    (parent.getChildAt(0) as TextView).isAllCaps = true
                    (parent.getChildAt(0) as TextView).textSize = 14f
                    selectedDistrict = distList!![position - 1].districtName
                } else {
                    selectedDistrict = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedDistrict = ""
            }
        }
        editMobile!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                /*if (s.length() == 10) {
                    getData();
                } else {
                    editOwner.setText("");
                    editShop.setText("");
                    spinnerState.setSelection(0);
                    // listDistrict.clear();
                    // distList.clear();
                    spinnerDist.setSelection(0);
                    editPlace.setText("");
                    editPin.setText("");
                }*/
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonRegister -> if (editMobile!!.text.toString().trim { it <= ' ' } == "") {
                UtilityMethods.setErrorForEditText(editMobile, "Mandatory Field")
            } else if (editOwner!!.text.toString().trim { it <= ' ' } == "") {
                UtilityMethods.setErrorForEditText(editOwner, "Mandatory Field")
            } else if (editShop!!.text.toString().trim { it <= ' ' } == "") {
                UtilityMethods.setErrorForEditText(editShop, "Mandatory Field")
            } else if (userType == "" && isNewReg) {
                val toast = CustomToast(mContext)
                toast.show(57)
            } else if (editDoor!!.text.toString().trim { it <= ' ' } == "" && isNewReg) {
                UtilityMethods.setErrorForEditText(editDoor, "Mandatory Field")
            } else if (editAddress!!.text.toString().trim { it <= ' ' } == "" && isNewReg) {
                UtilityMethods.setErrorForEditText(editAddress, "Mandatory Field")
            } else if (selectedState == "") {
                val toast = CustomToast(mContext)
                toast.show(32)
            } else if (selectedDistrict == "") {
                val toast = CustomToast(mContext)
                toast.show(33)
            } else if (editPlace!!.text.toString().trim { it <= ' ' } == "") {
                UtilityMethods.setErrorForEditText(editPlace, "Mandatory Field")
            } else if (editPin!!.text.toString().trim { it <= ' ' } == "") {
                UtilityMethods.setErrorForEditText(editPin, "Mandatory Field")
            } else {
                if (isNewReg) {
                    newRegisterAPI()
                } else {
                    registerAPI()
                }
            }
            R.id.imageSearch -> getData()
        }
    }
      fun  getData() {
            try {
                val name = arrayOf("mobile", "customer_id", "imei_no")
                val values = arrayOf(editMobile!!.text.toString().trim { it <= ' ' },
                    editCustomer!!.text.toString().trim { it <= ' ' },
                    imei_no
                )
                val manager = VolleyWrapper(VKCUrlConstants.GET_USER_DATA)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            //    System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val objResponse = rootObject.optJSONObject("response")
                                    val status = objResponse.optString("status")
                                    if (status.equals("Success", ignoreCase = true)) {
                                        val objData = objResponse.optJSONObject("data")
                                        val owner =
                                            objData.optString("contact_person").toUpperCase()
                                        val shop_name = objData.optString("name").toUpperCase()
                                        district = objData.optString("district")
                                        val state = objData.optString("state_name")
                                        val pin = objData.getString("pincode")
                                        val role = objData.optString("role")
                                        val cust_id = objData.optString("cust_id")
                                        val city = objData.optString("city").toUpperCase()
                                        val stateCode = objData.optString("state")

                                        //   editCustomer.setText("");
                                        if (editCustomer!!.text.toString()
                                                .trim { it <= ' ' }.length > 0
                                        ) {
                                            /*DialogConfirm dialogCon = new DialogConfirm(mContext, "2", "To register with this new mobile number please proceed with submit");
                                             dialogCon.show();*/
                                        }
                                        for (i in stateList!!.indices) {
                                            if (stateList!![i].stateId == stateCode) {
                                                spinnerState!!.setSelection(i + 1)
                                            }
                                        }
                                        val isLoggedIn = objData.optString("is_logged_in")
                                        mobileNo = objData.optString("phone")
                                        AppPrefenceManager.setMobile(mContext, mobileNo)
                                        if (isLoggedIn == "0") {
                                            val dealerCount =
                                                objData.optString("dealers_count").toInt()
                                            if (dealerCount > 0) {
                                                //  buttonRegister.setVisibility(View.GONE);
                                                AppPrefenceManager.saveDealerCount(
                                                    mContext,
                                                    dealerCount
                                                )
                                                val toast = CustomToast(mContext)
                                                toast.show(35)
                                            } else {
                                                AppPrefenceManager.saveDealerCount(mContext, 0)
                                                //   buttonRegister.setVisibility(View.VISIBLE);
                                            }
                                            AppPrefenceManager.saveCustomerId(mContext, cust_id)
                                            AppPrefenceManager.saveUserType(mContext, role)
                                            editOwner!!.setText(owner)
                                            editShop!!.setText(shop_name)
                                            editPlace!!.setText(city)
                                            editPin!!.setText(pin)
                                            editOwner!!.isEnabled = true
                                            editShop!!.isEnabled = false
                                            spinnerDist!!.isEnabled = false
                                            spinnerState!!.isEnabled = false
                                            editPlace!!.isEnabled = true
                                            editPin!!.isEnabled = false
                                            llCustID!!.visibility = View.GONE
                                            llAddress!!.visibility = View.GONE
                                            llUserType!!.visibility = View.GONE
                                            isNewReg = false
                                        } else {

                                            /*
                                              CustomToast toast = new CustomToast(mContext);
                                              toast.show(29);*/
                                            AppPrefenceManager.saveCustomerId(mContext, cust_id)
                                            AppPrefenceManager.saveUserType(mContext, role)
                                            AppPrefenceManager.saveUserId(
                                                mContext,
                                                role
                                            ) // For Dealer
                                            alertDeregister(mContext)
                                        }
                                    } else if (status == "Empty") {
                                        isNewReg = true
                                        editOwner!!.isEnabled = true
                                        editShop!!.isEnabled = true
                                        spinnerState!!.isEnabled = true
                                        spinnerDist!!.isEnabled = true
                                        editPlace!!.isEnabled = true
                                        editPin!!.isEnabled = true
                                        if (editCustomer!!.text.toString()
                                                .trim { it <= ' ' }.length > 0
                                        ) {
                                            val dialogCon: DialogConfirm = DialogConfirm(
                                                mContext,
                                                "0",
                                                "You are not a registered user proceed with new registration"
                                            )
                                            dialogCon.show()
                                        } else {
                                            val dialogCon: DialogConfirm = DialogConfirm(
                                                mContext,
                                                "1",
                                                "You are not registered with this mobile no, kindly search with CUST ID.If you don't know CUST ID please fill up the data for new registration"
                                            )
                                            dialogCon.show()
                                        }
                                        llCustID!!.visibility = View.VISIBLE
                                        llAddress!!.visibility = View.VISIBLE
                                        llUserType!!.visibility = View.VISIBLE
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
                Log.d("TAG", "Common error")
            }
        }

    fun getState() {
        stateList!!.clear()
        listState!!.clear()
        try {
            val name = arrayOf<String>()
            val values = arrayOf<String>()
            val manager = VolleyWrapper(VKCUrlConstants.GET_STATE)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objStateArray = rootObject.optJSONArray("states")
                                if (objStateArray.length() > 0) {
                                    for (i in 0 until objStateArray.length()) {
                                        val obj = objStateArray.getJSONObject(i)
                                        val model = StateModel()
                                        model.stateId = obj.getString("state")
                                        model.stateName = obj.getString("state_name")
                                        stateList!!.add(model)
                                    }
                                    listState!!.add("Select State")
                                    for (i in stateList!!.indices) {
                                        listState!!.add(stateList!![i].stateName)
                                    }
                                    val adapter = ArrayAdapter(
                                        this@SignUpActivity,
                                        android.R.layout.simple_spinner_item,
                                        listState!!
                                    )
                                    spinnerState!!.adapter = adapter
                                } else {
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
            Log.d("TAG", "Common error")
        }
    }

    fun getDistrict() {
        listDistrict!!.clear()
        distList!!.clear()
        try {
            val name = arrayOf("state")
            val values = arrayOf(stateId)
            val manager = VolleyWrapper(VKCUrlConstants.GET_DISTRICT)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objDistArray = rootObject.optJSONArray("response")
                                if (objDistArray.length() > 0) {
                                    for (i in 0 until objDistArray.length()) {
                                        val obj = objDistArray.getJSONObject(i)
                                        val model = DistrictModel()
                                        model.districtName = obj.getString("district")
                                        distList!!.add(model)
                                    }
                                    listDistrict!!.add("Select District")
                                    for (i in distList!!.indices) {
                                        listDistrict!!.add(distList!![i].districtName)
                                    }
                                    val adapter = ArrayAdapter(
                                        this@SignUpActivity,
                                        android.R.layout.simple_spinner_item,
                                        listDistrict!!
                                    )
                                    spinnerDist!!.adapter = adapter
                                    for (j in distList!!.indices) {
                                        if (distList!![j].districtName.toUpperCase() == district!!.toUpperCase()) {
                                            spinnerDist!!.setSelection(j + 1)
                                        }
                                    }
                                } else {
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
            Log.d("TAG", "Common error")
        }
    }

    private fun OTPAlert() {
        val appDeleteDialog: OTPDialog = OTPDialog(mContext!!)
        appDeleteDialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        // appDeleteDialog.setCancelable(true);
        appDeleteDialog.setCanceledOnTouchOutside(false)
        appDeleteDialog.show()
    }

    fun registerAPI() {
        try {
            val name = arrayOf("phone", "role", "cust_id", "contact_person", "city")
            val values = arrayOf(
                editMobile!!.text.toString(),
                AppPrefenceManager.getUserType(mContext),
                AppPrefenceManager.getCustomerId(mContext),
                editOwner!!.text.toString(),
                editPlace!!.text.toString()
            )
            val manager = VolleyWrapper(VKCUrlConstants.REGISTER_URL)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status.equals("Success", ignoreCase = true)) {
                                    OTPAlert()
                                    // OtpReader.bind(SignUpActivity.this, "VM-VKCGRP");
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

                    override fun responseFailure(failureResponse: String) {}
                })
        } catch (e: Exception) {
            // CustomStatusDialog(RESPONSE_FAILURE);
            e.printStackTrace()
            Log.d("TAG", "Common error")
        }
    }

    fun newRegisterAPI() {
        try {
            val name = arrayOf(
                "customer_id",
                "shop_name",
                "state_name",
                "district",
                "city",
                "pincode",
                "contact_person",
                "phone",
                "door_no",
                "address_line1",
                "landmark",
                "user_type"
            )
            val values = arrayOf(
                editCustomer!!.text.toString(),
                editShop!!.text.toString(),
                selectedState,
                selectedDistrict,
                editPlace!!.text.toString(),
                editPin!!.text.toString(),
                editOwner!!.text.toString(),
                editMobile!!.text.toString(),
                editDoor!!.text.toString(),
                editAddress!!.text.toString(),
                editLandMark!!.text.toString(),
                userType
            )
            val manager = VolleyWrapper(VKCUrlConstants.NEW_REGISTER)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status.equals("Success", ignoreCase = true)) {

                                    /* DialogConfirm dlg = new DialogConfirm(mContext);
                                        dlg.show();*/
                                    val dialogCon: DialogConfirm = DialogConfirm(
                                        mContext,
                                        "4",
                                        "Registration request submitted successfully. Please login to loyalty app after the confirmation from Walkaroo Group."
                                    )
                                    dialogCon.show()
                                } else if (status.equals("Exists", ignoreCase = true)) {

                                    /* DialogConfirm dlg = new DialogConfirm(mContext);
                                        dlg.show();*/
                                    val dialogCon: DialogConfirm = DialogConfirm(
                                        mContext,
                                        "5",
                                        "Registration request already submitted. Please contact Walkaroo Group."
                                    )
                                    dialogCon.show()
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
            Log.d("TAG", "Common error")
        }
    }

    fun verifyOTP(otp: String, mobile: String, isNewMobile: String) {
        try {
            val name = arrayOf("otp", "role", "cust_id", "phone", "isnewMobile", "imei_no")
            val values = arrayOf(
                otp,
                AppPrefenceManager.getUserType(mContext),
                AppPrefenceManager.getCustomerId(mContext),
                mobile,
                isNewMobile,
                imei_no
            )
            val manager = VolleyWrapper(VKCUrlConstants.OTP_VERIFY_URL)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status.equals("Success", ignoreCase = true)) {
                                    AppPrefenceManager.setIsVerifiedOTP(mContext, "yes")
                                    val toast = CustomToast(mContext)
                                    toast.show(8)
                                    if (AppPrefenceManager.getLoginStatusFlag(mContext) == "yes") {
                                        startActivity(
                                            Intent(
                                                this@SignUpActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        if (editCustomer!!.text.toString()
                                                .trim { it <= ' ' } == ""
                                        ) {
                                            val intent = Intent(
                                                this@SignUpActivity,
                                                DealersActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            val dialogUpdate = DialogUpdate(mContext)
                                            dialogUpdate.show()
                                        }
                                    }
                                } else {
                                    val attempts = objResponse.optString("otp_attempt").toInt()
                                    if (attempts == 3) {
                                        val dialogCon: DialogConfirm = DialogConfirm(
                                            mContext,
                                            "2",
                                            "Your OTP attemts exceeded. Kindly fill up the data for new registration"
                                        )
                                        dialogCon.show()
                                        AppPrefenceManager.setIsVerifiedOTP(mContext, "no")
                                    } else {
                                        val toast = CustomToast(mContext)
                                        toast.show(9)
                                    }

                                    /**/
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
            Log.d("TAG", "Common error")
        }
    }

    fun updatePhone(otp: String, mobile: String) {
        try {
            val name = arrayOf("otp", "role", "cust_id", "phone", "isnewMobile")
            val values = arrayOf(
                otp,
                AppPrefenceManager.getUserType(mContext),
                AppPrefenceManager.getCustomerId(mContext),
                mobile,
                "1"
            )
            val manager = VolleyWrapper(VKCUrlConstants.OTP_VERIFY_URL)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status.equals("Success", ignoreCase = true)) {
                                    val dialogCon: DialogConfirm = DialogConfirm(
                                        mContext,
                                        "3",
                                        "Mobile number updated successfully. Please login using new mobile number"
                                    )
                                    dialogCon.show()
                                    /*  CustomToast toast = new CustomToast(mContext);
                                        toast.show(30)*/startActivity(
                                        Intent(
                                            this@SignUpActivity,
                                            DealersActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    AppPrefenceManager.setIsVerifiedOTP(mContext, "no")
                                    val toast = CustomToast(mContext)
                                    toast.show(9)
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
            Log.d("TAG", "Common error")
        }
    }

    override fun onConnected(bundle: Bundle?) {}
    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onOtpReceived(otp: String) {
        val otpValue = otp.substring(45, 49)
        editOtp1!!.setText(otpValue[0].toString())
        editOtp1!!.setTextColor(Color.parseColor("#ffffff"))
        editOtp2!!.setText(otpValue[1].toString())
        editOtp2!!.setTextColor(Color.parseColor("#ffffff"))
        editOtp3!!.setText(otpValue[2].toString())
        editOtp3!!.setTextColor(Color.parseColor("#ffffff"))
        editOtp4!!.setText(otpValue[3].toString())
        editOtp4!!.setTextColor(Color.parseColor("#ffffff"))
    }

    override fun onOtpTimeout() {}
    inner class OTPDialog     // TODO Auto-generated constructor stub
        (var mActivity: AppCompatActivity) : Dialog(mActivity), View.OnClickListener {
        override fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_otp_alert)
            init()
        }

        private fun init() {
            editOtp1 = findViewById<View>(R.id.editOtp1) as EditText
            editOtp2 = findViewById<View>(R.id.editOtp2) as EditText
            editOtp3 = findViewById<View>(R.id.editOtp3) as EditText
            editOtp4 = findViewById<View>(R.id.editOtp4) as EditText
            val textResend = findViewById<View>(R.id.textResend) as TextView
            val textOtp = findViewById<View>(R.id.textOtp) as TextView
            val textCancel = findViewById<View>(R.id.textCancel) as TextView
            val mob = mobileNo!!.substring(6, 10)
            textOtp.text = "OTP has been sent to  XXXXXX$mob"
            editOtp1!!.isCursorVisible = false
            textCancel.setOnClickListener {
                editCustomer!!.setText("")
                llCustID!!.visibility = View.GONE
                dismiss()
                val intent = Intent(mActivity, SignUpActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                mActivity.finish()
            }
            textResend.setOnClickListener {
                resendOTP()
                editOtp1!!.setText("")
                editOtp2!!.setText("")
                editOtp3!!.setText("")
                editOtp4!!.setText("")
            }
            editOtp1!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    editOtp1!!.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    if (s.length == 1) {
                        editOtp1!!.clearFocus()
                        editOtp2!!.requestFocus()
                    }
                    otpValue =
                        editOtp1!!.text.toString().trim { it <= ' ' } + editOtp2!!.text.toString()
                            .trim { it <= ' ' } + editOtp3!!.text.toString()
                            .trim { it <= ' ' } + editOtp4!!.text.toString().trim { it <= ' ' }
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        editOtp1!!.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
            })
            editOtp2!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    editOtp2!!.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    if (s.length == 1) {
                        editOtp2!!.clearFocus()
                        editOtp3!!.requestFocus()
                    }
                    otpValue =
                        editOtp1!!.text.toString().trim { it <= ' ' } + editOtp2!!.text.toString()
                            .trim { it <= ' ' } + editOtp3!!.text.toString()
                            .trim { it <= ' ' } + editOtp4!!.text.toString().trim { it <= ' ' }
                    if (otpValue.length == 1) {
                        editOtp2!!.clearFocus()
                        editOtp1!!.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        editOtp2!!.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
            })
            editOtp3!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    editOtp3!!.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    if (s.length == 1) {
                        editOtp3!!.clearFocus()
                        editOtp4!!.requestFocus()
                    }
                    otpValue =
                        editOtp1!!.text.toString().trim { it <= ' ' } + editOtp2!!.text.toString()
                            .trim { it <= ' ' } + editOtp3!!.text.toString()
                            .trim { it <= ' ' } + editOtp4!!.text.toString().trim { it <= ' ' }
                    if (otpValue.length == 2) {
                        editOtp3!!.clearFocus()
                        editOtp2!!.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        editOtp3!!.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
            })
            editOtp4!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    editOtp4!!.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    otpValue =
                        editOtp1!!.text.toString().trim { it <= ' ' } + editOtp2!!.text.toString()
                            .trim { it <= ' ' } + editOtp3!!.text.toString()
                            .trim { it <= ' ' } + editOtp4!!.text.toString().trim { it <= ' ' }
                    if (otpValue.length == 3) {
                        editOtp3!!.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        editOtp4!!.setBackgroundResource(R.drawable.rounded_rect_line)
                    } else {
                        verifyOTP(
                            otpValue,
                            editMobile!!.text.toString().trim { it <= ' ' },
                            isNewMobile
                        )
                    }
                }
            })
            val buttonCancel = findViewById<View>(R.id.buttonCancel) as Button
            buttonCancel.setOnClickListener { dismiss() }
        }

        override fun onClick(v: View) {
            dismiss()
        }
    }

    fun recivedSms(message: String?) {
        try {
            //.setText(message);
        } catch (e: Exception) {
        }
    }

    fun resendOTP() {
        mGoogleApiClient!!.stopAutoManage(this@SignUpActivity)
        mGoogleApiClient!!.disconnect()
        try {
            val name = arrayOf("role", "cust_id", "sms_key")
            val values = arrayOf(
                AppPrefenceManager.getUserType(mContext),
                AppPrefenceManager.getCustomerId(mContext),
                smsKey
            )
            val manager = VolleyWrapper(VKCUrlConstants.OTP_RESEND_URL)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status.equals("Success", ignoreCase = true)) {
                                    val toast = CustomToast(mContext)
                                    toast.show(34)
                                    smsautodetection()
                                    startSMSListener()
                                } else {
                                    // AppPrefenceManager.setIsVerifiedOTP(mContext, "no");
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
            Log.d("TAG", "Common error")
        }
    }

    inner class DialogConfirm : Dialog, View.OnClickListener {
        var mActivity: AppCompatActivity? = null
        var type: String? = null
        var message: String? = null

        constructor(a: AppCompatActivity?) : super(a!!) {
            // TODO Auto-generated constructor stub
            mActivity = a
        }

        constructor(a: AppCompatActivity?, type: String?, message: String?) : super(
            a!!
        ) {
            this.type = type
            this.message = message
            // TODO Auto-generated constructor stub
        }

        override fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_confirm)
            init()
        }

        private fun init() {

            // Button buttonSet = (Button) findViewById(R.id.buttonOk);
            val textOtp = findViewById<View>(R.id.textOtp) as TextView
            val buttonCancel = findViewById<View>(R.id.buttonOk) as Button
            textOtp.text = message
            if (type == "1") {
                editMobile!!.clearFocus()
                editCustomer!!.requestFocus()
            }
            buttonCancel.setOnClickListener {
                dismiss()
                // mActivity.finish();
                if (type == "1") {
                    editCustomer!!.setText("")
                } else if (type == "2") {
                    editCustomer!!.setText("")
                    //   dismiss();
                    startActivity(Intent(mContext, SignUpActivity::class.java))
                    mContext!!.finish()
                } else if (type == "3") {
                    //  dismiss();
                    startActivity(Intent(mContext, DealersActivity::class.java))
                    mContext!!.finish()
                } else if (type == "4") {
                    // dismiss();
                    mContext!!.finish()
                } else if (type == "5") {
                    //  dismiss();
                    mContext!!.finish()
                } else {
                    llUserType!!.visibility = View.VISIBLE
                    // dismiss();
                }
            }
        }

        override fun onClick(v: View) {
            dismiss()
        }
    }

    inner class DialogUpdate : Dialog, View.OnClickListener {
        var mActivity: AppCompatActivity? = null
        var type: String? = null
        var message: String? = null

        constructor(a: AppCompatActivity?) : super(a!!) {
            // TODO Auto-generated constructor stub
            mActivity = a
        }

        constructor(a: AppCompatActivity?, type: String?, message: String?) : super(
            a!!
        ) {
            this.type = type
            this.message = message
            // TODO Auto-generated constructor stub
        }

        override fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_yes_no)
            init()
        }

        private fun init() {

            // Button buttonSet = (Button) findViewById(R.id.buttonOk);
            val textYes = findViewById<View>(R.id.textYes) as TextView
            val textNo = findViewById<View>(R.id.textNo) as TextView
            textYes.setOnClickListener {
                dismiss()
                // mActivity.finish();
                updatePhone(otpValue, editMobile!!.text.toString().trim { it <= ' ' })
            }
            textNo.setOnClickListener {
                dismiss()
                startActivity(Intent(this@SignUpActivity, DealersActivity::class.java))
                finish()
                // mActivity.finish();
                //updatePhone(otpValue, editMobile.getText().toString().trim());
            }
        }

        override fun onClick(v: View) {
            dismiss()
        }
    }

    fun alertDeregister(act: Activity?) {
        val appPackageName = act!!.packageName
        val builder = AlertDialog.Builder(act)
        builder.setTitle("Alert !") // act.getString(R.string.dialog_title_update_app)
            .setMessage("This account is already registered with a device. You have to deregister the previous device to continue login. Please click ok to proceed.") //
            .setNegativeButton(
                "Cancel"
            ) { dialog, whichButton ->
                try {
                    dialog.cancel()
                } catch (anfe: ActivityNotFoundException) {
                }
            }
            .setPositiveButton("Ok") { dialog, i ->
                try {
                    isNewMobile = "2" // For updating IMEI
                    dialog.cancel()
                    resendOTP()
                    val appDeleteDialog: OTPDialog = OTPDialog(mContext!!)
                    appDeleteDialog.window!!.setBackgroundDrawable(
                        ColorDrawable(Color.TRANSPARENT)
                    )
                    appDeleteDialog.setCanceledOnTouchOutside(false)
                    appDeleteDialog.show()
                } catch (anfe: ActivityNotFoundException) {
                }
            }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun startSMSListener() {
        try {
            smsReceiver = SMSReceiver()
            smsReceiver!!.setOnOtpListeners(this@SignUpActivity)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(this)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }
            task.addOnFailureListener {
                // Fail to start API
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun smsautodetection() {
        smsReceiver = SMSReceiver()
        mGoogleApiClient = GoogleApiClient.Builder(mContext)
            .addConnectionCallbacks(this)
            .enableAutoManage(this, this)
            .addApi(Auth.CREDENTIALS_API)
            .build()
        smsReceiver!!.setOnOtpListeners(this@SignUpActivity)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        applicationContext.registerReceiver(smsReceiver, intentFilter)
    }
}