package com.vkc.loyaltyapp.activity.issuepoint

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.github.lzyzsd.circleprogress.ArcProgress
import com.vkc.loyaltyapp.manager.HeaderManager
import com.vkc.loyaltyapp.activity.issuepoint.model.UserModel
import android.os.Bundle
import com.vkc.loyaltyapp.R
import android.widget.AdapterView.OnItemClickListener
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.vkc.loyaltyapp.utils.CustomToast
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception
import java.util.ArrayList

/**
 * Created by user2 on 10/8/17.
 */
class IssuePointActivity : AppCompatActivity(), View.OnClickListener, VKCUrlConstants {
    var btnLogin: Button? = null
    var textSignup: TextView? = null
    var mContext: AppCompatActivity? = null
    var mEditPoint: EditText? = null
    var imei_no: String? = null
    var arcProgress: ArcProgress? = null
    var btnIssue: ImageView? = null
    var myPoint = 0
    var headermanager: HeaderManager? = null
    var relativeHeader: LinearLayout? = null
    var mImageBack: ImageView? = null
    var selectedId: String? = null
    var listUsers: ArrayList<UserModel>? = null
    var edtSearch: AutoCompleteTextView? = null
    var textId: TextView? = null
    var textName: TextView? = null
    var textAddress: TextView? = null
    var textPhone: TextView? = null
    var llData: LinearLayout? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_points)
        mContext = this
        initUI()
        myPoints
    }

    private fun initUI() {
        listUsers = ArrayList()
        arcProgress = findViewById<View>(R.id.arc_progress) as ArcProgress
        btnIssue = findViewById<View>(R.id.buttonIssue) as ImageView
        edtSearch = findViewById<View>(R.id.autoSearch) as AutoCompleteTextView
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        selectedId = ""
        headermanager =
            HeaderManager(this@IssuePointActivity, resources.getString(R.string.issue_point))
        headermanager!!.getHeader(relativeHeader, 1)
        mImageBack = headermanager!!.leftButton
        llData = findViewById<View>(R.id.llData) as LinearLayout
        llData!!.visibility = View.GONE
        textId = findViewById<View>(R.id.textViewId) as TextView
        textName = findViewById<View>(R.id.textViewName) as TextView
        textAddress = findViewById<View>(R.id.textViewAddress) as TextView
        textPhone = findViewById<View>(R.id.textViewPhone) as TextView
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        mEditPoint = findViewById<View>(R.id.editPoints) as EditText
        mImageBack!!.setOnClickListener(this)
        btnIssue!!.setOnClickListener(this)
        arcProgress!!.suffixText = ""
        arcProgress!!.strokeWidth = 15f
        arcProgress!!.bottomTextSize = 50f
        arcProgress!!.max = 10000000
        arcProgress!!.textColor = resources.getColor(R.color.white)
        arcProgress!!.setBackgroundColor(resources.getColor(R.color.transparent))
        arcProgress!!.unfinishedStrokeColor = resources.getColor(R.color.white)
        edtSearch!!.onItemClickListener = OnItemClickListener { arg0, arg1, arg2, arg3 ->
            // TODO Auto-generated method stub
            val selectedData = edtSearch!!.text.toString()
            for (i in listUsers!!.indices) {
                if (listUsers!![i].userName == selectedData) {
                    selectedId = listUsers!![i].userId
                    userData
                    //   System.out.println("Selected Id : " + selectedId);
                    break
                } else {
                    selectedId = ""
                }
            }
        }
        edtSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 0) {
                    if (selectedId!!.length > 0) {
                        llData!!.visibility = View.VISIBLE
                    }
                } else {
                    selectedId = ""
                    llData!!.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        edtSearch!!.setOnTouchListener { v, event ->
            edtSearch!!.showDropDown()
            false
        }
    }

    override fun onClick(v: View) {
        if (v === mImageBack) {
            finish()
        } else if (v === btnIssue) {
            if (edtSearch!!.text.toString().trim { it <= ' ' } == "") {
                val toast = CustomToast(mContext)
                toast.show(14)
            } else if (mEditPoint!!.text.toString().trim { it <= ' ' } == "") {
                //VKCUtils.textWatcherForEditText(mEditPoint, "Mandatory field");
                val toast = CustomToast(mContext)
                toast.show(17)
            } else if (mEditPoint!!.text.toString().trim { it <= ' ' }.toInt() > myPoint) {
                // FeedbackSubmitApi();
                val toast = CustomToast(mContext)
                toast.show(16)
            } else {
                submitPoints()
            }
        }
    }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);

    // System.out.println("Response---Login" + successResponse);
    val userData: Unit
        get() {
            try {
                val name = arrayOf("cust_id", "role")
                val values = arrayOf(selectedId, "5")
                val manager = VolleyWrapper(VKCUrlConstants.GET_DATA)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            // System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val objResponse = rootObject.optJSONObject("response")
                                    val status = objResponse.optString("status")
                                    if (status == "Success") {
                                        val objData = objResponse.optJSONObject("data")
                                        val cust_id = objData.optString("customer_id")
                                        val address = objData.optString("address")
                                        val name = objData.optString("name")
                                        val phone = objData.optString("phone")
                                        textId!!.text = ": $cust_id"
                                        textName!!.text = ": $name"
                                        textAddress!!.text = ": $address"
                                        textPhone!!.text = ": $phone"
                                        llData!!.visibility = View.VISIBLE
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
        }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);

    //      arcProgress.setBottomText("Points");
    //  arcProgress.setSuffixText(points);
    //mTxtPoint.setText(points);
    val myPoints: Unit
        get() {
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
                            println("Response---Login$successResponse")
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val objResponse = rootObject.optJSONObject("response")
                                    val status = objResponse.optString("status")
                                    if (status == "Success") {
                                        val points = objResponse.optString("loyality_point")
                                        myPoint = points.toInt()
                                        //      arcProgress.setBottomText("Points");
                                        arcProgress!!.progress = myPoint
                                        //  arcProgress.setSuffixText(points);
                                        //mTxtPoint.setText(points);
                                        users
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

    fun submitPoints() {
        val name = arrayOf("userid", "to_user_id", "to_role", "points", "role")
        val values = arrayOf(
            AppPrefenceManager.getCustomerId(mContext),
            selectedId, "5", mEditPoint!!.text.toString(), "7"
        )
        val manager = VolleyWrapper(VKCUrlConstants.SUBMIT_POINTS)
        manager.getResponsePOST(mContext, 11, name, values,
            object : ResponseListener {
                override fun responseSuccess(successResponse: String) {
                    // TODO Auto-generated method stub
                    Log.v("LOG", "18022015 success$successResponse")
                    try {
                        val objResponse = JSONObject(
                            successResponse
                        )
                        val status = objResponse.optString("response")
                        if (status == "1") {
                            val toast = CustomToast(
                                mContext
                            )
                            toast.show(18)
                            edtSearch!!.setText("")
                            mEditPoint!!.setText("")
                            myPoints
                        } else if (status == "5") {
                            val toast = CustomToast(
                                mContext
                            )
                            toast.show(61)
                        } else {
                            val toast = CustomToast(
                                mContext
                            )
                            toast.show(13)
                        }
                    } catch (e: JSONException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    }

                    // parseResponse(successResponse);
                }

                override fun responseFailure(failureResponse: String) {
                    // TODO Auto-generated method stub
                    Log.v("LOG", "18022015 Errror$failureResponse")
                }
            })
    }// TODO

    // Auto-generated method stub
// TODO Auto-generated catch block
    // listArticle[i]=articleArray.getString(i);
    private val users:
    // model.setCity(obj.getString("city"));
            Unit
        private get() {
            val name = arrayOf("cust_id")
            val value = arrayOf(AppPrefenceManager.getCustomerId(mContext))
            val manager = VolleyWrapper(VKCUrlConstants.GET_RETAILERS)
            manager.getResponsePOST(mContext, 11, name, value,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        try {
                            val responseObj = JSONObject(
                                successResponse
                            )
                            val response = responseObj
                                .getJSONObject("response")
                            val status = response.getString("status")
                            if (status == "Success") {
                                val dataArray = response
                                    .optJSONArray("data")
                                if (dataArray.length() > 0) {
                                    for (i in 0 until dataArray.length()) {
                                        // listArticle[i]=articleArray.getString(i);
                                        val obj = dataArray
                                            .getJSONObject(i)
                                        val model = UserModel()
                                        model.userId = obj.getString("id")
                                        model.userName = obj.getString("name")
                                        // model.setCity(obj.getString("city"));
                                        listUsers!!.add(model)
                                    }
                                    val listUser = ArrayList<String>()
                                    for (i in listUsers!!.indices) {
                                        listUser.add(
                                            listUsers!![i]
                                                .userName
                                        )
                                    }
                                    val adapter = ArrayAdapter(
                                        mContext!!,
                                        android.R.layout.simple_list_item_1,
                                        listUser
                                    )
                                    edtSearch!!.threshold = 1
                                    edtSearch!!.setAdapter(adapter)
                                } else {
                                    val toast = CustomToast(
                                        mContext
                                    )
                                    toast.show(5)
                                }
                            }
                        } catch (e: JSONException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                    }

                    override fun responseFailure(failureResponse: String) { // TODO
                        // Auto-generated method stub
                    }
                })
        }
}