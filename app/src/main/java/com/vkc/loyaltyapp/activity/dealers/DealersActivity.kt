package com.vkc.loyaltyapp.activity.dealers

import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.manager.HeaderManager
import com.vkc.loyaltyapp.activity.dealers.adapter.DealersListAdapter
import android.os.Bundle
import com.vkc.loyaltyapp.R
import android.text.TextWatcher
import android.text.Editable
import com.vkc.loyaltyapp.appcontroller.AppController
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.json.JSONObject
import org.json.JSONArray
import com.vkc.loyaltyapp.utils.CustomToast
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.vkc.loyaltyapp.activity.HomeActivity
import com.vkc.loyaltyapp.activity.dealers.model.DealerModel
import org.json.JSONException
import java.lang.Exception
import java.util.ArrayList
import java.util.Comparator

/**
 */
class DealersActivity : AppCompatActivity(), VKCUrlConstants, View.OnClickListener {
    var mContext: AppCompatActivity? = null
    var listDealers: ArrayList<DealerModel>? = null
    var listViewDealer: ListView? = null
    var textSubmit: TextView? = null

    // ArrayList<String> listIds;
    var headermanager: HeaderManager? = null
    var relativeHeader: LinearLayout? = null
    var mImageBack: ImageView? = null
    var editSearch: EditText? = null
    var tempDealer: ArrayList<DealerModel>? = null
    var adapter: DealersListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dealers_list)
        mContext = this
        ininUI()
        getDealers("")
    }

    private fun ininUI() {
        listDealers = ArrayList()
        tempDealer = ArrayList()
        listViewDealer = findViewById<View>(R.id.listViewDealer) as ListView
        // mImageSearch = (ImageView) findViewById(R.id.imageSearch);
        textSubmit = findViewById<View>(R.id.textSubmit) as TextView
        editSearch = findViewById<View>(R.id.editSearch) as EditText
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        headermanager = HeaderManager(this@DealersActivity, resources.getString(R.string.dealers))
        headermanager!!.getHeader(relativeHeader, 1)
        mImageBack = headermanager!!.leftButton
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        mImageBack!!.setOnClickListener(this)
        textSubmit!!.setOnClickListener(this)
        // mImageSearch.setOnClickListener(this);
        editSearch!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER ->                             /*if (editSearch.getText().toString().trim().length() > 0) {
                                getDealers(editSearch.getText().toString().trim());
                            } else {
                                getDealers("");
                            }*/
/*
                            for (int i = 0; i < listDealers.size(); i++) {
                                if (listDealers.get(i).getName().contains(editSearch.getText().toString().trim())) {
                                    listViewDealer.setSelection(i);
                                    break;
                                }
                            }*/return@OnKeyListener true
                    else -> {
                    }
                }
            }
            false
        })
        editSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) {
                    listViewDealer!!.setSelection(0)
                } else {
                    for (i in listDealers!!.indices) {
                        if (listDealers!![i].name.toLowerCase()
                                .startsWith(s.toString().toLowerCase())
                        ) {
                            listViewDealer!!.setSelection(i)
                            break
                        }
                    }
                }
                /*else {

                    for (int i = 0; i < listDealers.size(); i++) {
                        if (listDealers.get(i).getName().contains(s)) {
                            listViewDealer.setSelection(i);
                            break;
                        }
                    }
                }*/
                /*if (s.length() == 0) {
                    tempDealer = listDealers;
                    adapter = new DealersListAdapter(mContext, tempDealer);
                    adapter.notifyDataSetChanged();
                    listViewDealer.setAdapter(adapter);
                } else {
                    for (int i = 0; i < listDealers.size(); i++) {
                        if (listDealers.get(i).getName().contains(editSearch.getText().toString().trim())) {
                            tempDealer.add(listDealers.get(i));
                        }
                    }
                    adapter = new DealersListAdapter(mContext, tempDealer);
                    adapter.notifyDataSetChanged();
                    listViewDealer.setAdapter(adapter);
                }*/

                /* if (s.length() == 0) {
                    */
                /*tempDealer = listDealers;
                    adapter = new DealersListAdapter(mContext, tempDealer);
                    adapter.notifyDataSetChanged();
                    listViewDealer.setAdapter(adapter);*/
                /*
                    listViewDealer.setSelection(0);
                } else {
                    for (int i = 0; i < listDealers.size(); i++) {
                        if (listDealers.get(i).getName().contains(editSearch.getText().toString().trim())) {
                            listViewDealer.setSelection(i);
                            break;
                        }
                    }
                }*/
            }
        })
    }

    fun getDealers(searchKey: String) {
        try {
            AppController.listDealers.clear()
            listDealers!!.clear()
            // listIds.clear();
            val name = arrayOf("cust_id", "role", "search_key")
            val values = arrayOf(
                AppPrefenceManager.getCustomerId(mContext),
                AppPrefenceManager.getUserType(mContext),
                searchKey
            )
            val manager = VolleyWrapper(VKCUrlConstants.GET_DEALERS)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        val dealersNotAssigned = ArrayList<DealerModel>()
                        val dealersAssigned = ArrayList<DealerModel>()
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status.equals("Success", ignoreCase = true)) {
                                    val dataArray = objResponse.optJSONArray("data")
                                    if (dataArray.length() > 0) {
                                        dealersNotAssigned.clear()
                                        dealersAssigned.clear()
                                        for (i in 0 until dataArray.length()) {
                                            val obj = dataArray.optJSONObject(i)
                                            val model = DealerModel()
                                            model.id = obj.optString("id")
                                            model.name = obj.optString("name")
                                            model.role = obj.optString("role")
                                            if (obj.optString("is_assigned") == "0") {
                                                model.setIsChecked(false)
                                                dealersNotAssigned.add(model)
                                            } else {
                                                model.setIsChecked(true)
                                                dealersAssigned.add(model)
                                            }
                                        }
                                        listDealers!!.addAll(dealersAssigned)
                                        listDealers!!.addAll(dealersNotAssigned)
                                        //Sort Dealers List A - Z
                                        /* Collections.sort(listDealers, new CustomComparator() {
                                                @Override
                                                public int compare(DealerModel o1, DealerModel o2) {
                                                    return o1.getName().trim().compareToIgnoreCase(o2.getName().trim());
                                                }
                                            });*/adapter = DealersListAdapter(mContext, listDealers)
                                        //adapter.notifyDataSetChanged();
                                        listViewDealer!!.adapter = adapter
                                    } else {
                                    }
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

    override fun onClick(v: View) {
        if (v === mImageBack) {
            finish()
        }
        when (v.id) {
            R.id.textSubmit -> {
                //listIds.clear();
                val jsonObject = JSONObject()
                val jsonArray = JSONArray()
                var i = 0
                while (i < AppController.listDealers.size) {
                    if (AppController.listDealers[i].isChecked) {


                        // listIds.add(AppController.listDealers.get(i).getId())
                        val `object` = JSONObject()
                        try {
                            `object`.put(
                                "id", AppController.listDealers[i]
                                    .id
                            )
                            `object`.put(
                                "role", AppController.listDealers[i]
                                    .role
                            )

                            // object.putOpt("grid_value",cartArrayList.get(i).getProdGridValue());
                            jsonArray.put(`object`)
                        } catch (e: JSONException) {
                            // TODO Auto-generated catch block
                        }
                    }
                    i++
                }
                try {
                    jsonObject.put("dealers", jsonArray)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (jsonArray.length() > 0) {
                    if (jsonArray.length() > 10) {
                        val toast = CustomToast(mContext)
                        toast.show(11)
                    } else {
                        submitDealers(jsonArray)
                    }
                } else {
                    val toast = CustomToast(mContext)
                    toast.show(10)
                }
            }
        }
    }

    fun submitDealers(objArray: JSONArray) {
        try {
            val name = arrayOf("cust_id", "role", "dealer_id")
            val values = arrayOf(
                AppPrefenceManager.getCustomerId(mContext),
                AppPrefenceManager.getUserType(mContext),
                objArray.toString()
            )
            val manager = VolleyWrapper(VKCUrlConstants.ASSIGN_DEALERS)
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
                                    AppPrefenceManager.saveLoginStatusFlag(mContext, "yes")
                                    val toast = CustomToast(mContext)
                                    toast.show(12)
                                    startActivity(
                                        Intent(
                                            this@DealersActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    AppPrefenceManager.saveLoginStatusFlag(mContext, "no")
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

    inner class CustomComparator : Comparator<DealerModel> {
        override fun compare(o1: DealerModel, o2: DealerModel): Int {
            return o1.name.compareTo(o2.name)
        }
    }
}