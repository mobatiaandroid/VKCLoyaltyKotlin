package com.vkc.loyaltyapp.activity.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.squareup.picasso.Picasso
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.activity.common.SignUpActivity
import com.vkc.loyaltyapp.activity.dealers.DealersActivity
import com.vkc.loyaltyapp.activity.profile.ProfileActivity.OTPDialog
import com.vkc.loyaltyapp.activity.shopimage.ShopImageActivity.Companion.filePath
import com.vkc.loyaltyapp.activity.shopimage.ShopImageActivity.Companion.mActivity
import com.vkc.loyaltyapp.activity.shopimage.ShopImageActivity.Companion.mContext
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import com.vkc.loyaltyapp.manager.HeaderManager
import com.vkc.loyaltyapp.utils.AndroidMultiPartEntity
import com.vkc.loyaltyapp.utils.CustomToast
import com.vkc.loyaltyapp.utils.UtilityMethods
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import org.apache.http.HttpEntity
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

/**
 * Created by user2 on 9/8/17.
 */
class ProfileActivity : AppCompatActivity(), View.OnClickListener,
    VKCUrlConstants {
    var mContext: AppCompatActivity? = null
    var headermanager: HeaderManager? = null
    var relativeHeader: LinearLayout? = null
    var mImageBack: ImageView? = null
    var imageProfile: ImageView? = null
    var buttonUpdate: Button? = null
  companion object{
      var editMobile: EditText? = null
      var editOwner: EditText? = null
      var editShop: EditText? = null
      var editState: EditText? = null
      var editDist: EditText? = null
      var editPlace: EditText? = null
      var editPin: EditText? = null
      var editAddress: EditText? = null
      var editMobile2: EditText? = null
      var editEmail: EditText? = null
  }
    var textCustId: TextView? = null
    var textMydealers: TextView? = null
    var textUpdate: TextView? = null
    var filePath = ""
    var ACTIVITY_REQUEST_CODE = 700
    var ACTIVITY_FINISH_RESULT_CODE = 701
    private val mImageCaptureUri: Uri? = null
    var otpValue = ""
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mContext = this
        initUI()
        profile
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
                    showCameraGalleryChoice()
                } else {
                    Toast.makeText(
                        mContext,
                        "Unable to continue without granting permission for camera and writing data to external storage",
                        Toast.LENGTH_LONG
                    ).show()
                    val myAppSettings = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
                            "package:$packageName"
                        )
                    )
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                    myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(myAppSettings)
                }
                return
            }
            101 -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    showCameraGalleryChoice()

                    //
                } else {
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

    private fun initUI() {
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        headermanager = HeaderManager(this@ProfileActivity, resources.getString(R.string.profile))
        headermanager!!.getHeader(relativeHeader, 1)
        mImageBack = headermanager!!.leftButton
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        textCustId = findViewById<View>(R.id.textCustId) as TextView
        textUpdate = findViewById<View>(R.id.textUpdate) as TextView
        textMydealers = findViewById<View>(R.id.textMydealers) as TextView
        mImageBack!!.setOnClickListener(this)
        imageProfile = findViewById<View>(R.id.imageProfile) as ImageView
        buttonUpdate = findViewById<View>(R.id.buttonUpdate) as Button
        editMobile = findViewById<View>(R.id.editMobile) as EditText
        editOwner = findViewById<View>(R.id.editOwner) as EditText
        editShop = findViewById<View>(R.id.editShop) as EditText
        editState = findViewById<View>(R.id.editState) as EditText
        editDist = findViewById<View>(R.id.editDistrict) as EditText
        editPlace = findViewById<View>(R.id.editPlace) as EditText
        editPin = findViewById<View>(R.id.editPin) as EditText
        editAddress = findViewById<View>(R.id.editAddress) as EditText
        editMobile2 = findViewById<View>(R.id.editMobile2) as EditText
        editEmail = findViewById<View>(R.id.editEmail) as EditText
        // editMobile.setEnabled(false);
        editOwner!!.isEnabled = true
        editShop!!.isEnabled = false
        editState!!.isEnabled = false
        editDist!!.isEnabled = false
        editPlace!!.isEnabled = true
        editPin!!.isEnabled = false
        editAddress!!.isEnabled = false
        buttonUpdate!!.setOnClickListener(this)
        textMydealers!!.setOnClickListener(this)
        imageProfile!!.setOnClickListener(this)
        textUpdate!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v === mImageBack) {
            finish()
        } else if (v === buttonUpdate) {

            /*    if (filePath.equals("")) {
                CustomToast toast = new CustomToast(
                        mContext);
                toast.show(21);
            } else {
                UploadFileToServer upload = new UploadFileToServer();
                upload.execute();
            }*/
            if (filePath == "") {
                val toast = CustomToast(
                    mContext
                )
                toast.show(21)
            } else {
                val upload: UpdateProfile = UpdateProfile()
                upload.execute()
            }
        } else if (v === imageProfile) {
            if (Build.VERSION.SDK_INT >= 23) {
                val permissionListenerGallery: PermissionListener = object : PermissionListener {
                    override fun onPermissionGranted() {
                        if (UtilityMethods.isNetworkConnected(mContext)) {
                            // new PDFViewActivity.loadPDF().execute();
                            showCameraGalleryChoice()
                        } else {
                            Toast.makeText(mContext, "Network error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {
                        Toast.makeText(mContext, "Permission Denied\n", Toast.LENGTH_SHORT).show()
                    }
                }
                TedPermission.with(mContext)
                    .setPermissionListener(permissionListenerGallery)
                    .setDeniedMessage("If you reject permission,you cannot use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                    )
                    .check()
            } else {
                showCameraGalleryChoice()
            }
        } else if (v === textMydealers) {
            startActivity(Intent(this@ProfileActivity, DealersActivity::class.java))
        } else if (v === textUpdate) {
            if (editMobile!!.text.toString().trim { it <= ' ' }.length > 0) {
                if (editMobile!!.text.toString().trim { it <= ' ' }.length == 10) {

                    //Update mobile dialog
                    if (AppPrefenceManager.getMobile(mContext) == editMobile!!.text.toString()
                            .trim { it <= ' ' }
                    ) {
                    } else {
                        val dialog: DialogUpdateMobile = DialogUpdateMobile(mContext)
                        dialog.show()
                    }
                } else {
                    val toast = CustomToast(mContext)
                    toast.show(54)
                }
            } else {
                val toast = CustomToast(mContext)
                toast.show(54)
            }
        }
    }// CustomStatusDialog(RESPONSE_FAILURE);

    //Log.d("TAG", "Common error");
//CustomStatusDialog(RESPONSE_FAILURE);//.transform(new CircleTransform())
    //    System.out.println("Response---Login" + successResponse);
    val profile: Unit
        get() {
            try {
                val name = arrayOf("cust_id", "role")
                val values = arrayOf(
                    AppPrefenceManager.getCustomerId(mContext),
                    AppPrefenceManager.getUserType(mContext)
                )
                val manager = VolleyWrapper(VKCUrlConstants.GET_PROFILE)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            //    System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val objResponse = rootObject.optJSONObject("response")
                                    val status = objResponse.optString("status")
                                    if (status == "Success") {
                                        val objData = objResponse.optJSONObject("data")
                                        val name = objData.optString("name")
                                        val contact_person = objData.optString("contact_person")
                                        val district = objData.optString("district")
                                        val city = objData.optString("city")
                                        val state_name = objData.optString("state_name")
                                        val pincode = objData.optString("pincode")
                                        val phone = objData.optString("phone")
                                        val url = objData.optString("image")
                                        val mobile2 = objData.optString("phone2")
                                        val email = objData.optString("email")
                                        editMobile2!!.setText(mobile2)
                                        editEmail!!.setText(email)
                                        editShop!!.setText(name)
                                        editOwner!!.setText(contact_person)
                                        editDist!!.setText(district)
                                        editMobile!!.setText(phone)
                                        editPlace!!.setText(city)
                                        editState!!.setText(state_name)
                                        editPin!!.setText(pincode)
                                        editAddress!!.setText(objData.optString("address"))
                                        textCustId!!.text =
                                            "CUST_ID: - " + objData.optString("customer_id")
                                        Picasso.with(mContext).load(url)
                                            .placeholder(R.drawable.profile_image)
                                            .into(imageProfile) //.transform(new CircleTransform())
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
 class UpdateProfile : AsyncTask<Void?, Int?, String?>() {
        val pDialog = ProgressDialog(mContext)
        private var obj: JSONObject? = null
        private var responseString = ""
        override fun onPreExecute() {
            super.onPreExecute()
            pDialog.show()
        }

        protected override fun onProgressUpdate(vararg values: Int?) {}
        protected override fun doInBackground(vararg p0: Void?): String? {
            return uploadFile()
        }

        private fun uploadFile(): String? {
            var responseString: String? = null
            try {
                val httpclient: HttpClient = DefaultHttpClient()
                val httppost = HttpPost(VKCUrlConstants.UPDATE_PROFILE)
                val file = File(filePath)
                val bin1 = FileBody(file.absoluteFile)
                val entity: AndroidMultiPartEntity
                entity = AndroidMultiPartEntity { }
                entity.addPart("cust_id", StringBody(AppPrefenceManager.getCustomerId(mContext)))
                entity.addPart("role", StringBody(AppPrefenceManager.getUserType(mContext)))
                entity.addPart("phone", StringBody(editMobile!!.text.toString().trim { it <= ' ' }))
                entity.addPart(
                    "contact_person",
                    StringBody(editOwner!!.text.toString().trim())
                )
                entity.addPart("city", StringBody(editPlace!!.text.toString().trim { it <= ' ' }))
                entity.addPart(
                    "phone2",
                    StringBody(editMobile2!!.text.toString().trim { it <= ' ' })
                )
                entity.addPart("email", StringBody(editEmail!!.text.toString().trim { it <= ' ' }))
                if (filePath == "") {
                } else {
                    entity.addPart("image", bin1)
                }
                httppost.entity = entity
                val response = httpclient.execute(httppost)
                val r_entity = response.entity
                val statusCode = response.statusLine.statusCode
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity)
                } else {
                    responseString = EntityUtils.toString(r_entity)
                    responseString = ("Error occurred! Http Status Code: "
                            + statusCode)
                }
            } catch (e: ClientProtocolException) {
                responseString = e.toString()
                Log.e("UploadApp", "exception: $responseString")
            } catch (e: IOException) {
                responseString = e.toString()
                Log.e("UploadApp", "exception: $responseString")
            }
            return responseString
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (pDialog.isShowing) pDialog.dismiss()
            print("Result $result")
            try {
                obj = JSONObject(result)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            try {
                val responseObj = obj!!.optJSONObject("response")
                responseString = responseObj.optString("status")
                if (responseString == "Success") {
                    val toast = CustomToast(mActivity)
                    toast.show(26)
                    //getProfile()
                } else {
                    val toast = CustomToast(mActivity)
                    toast.show(27)
                }
            } catch (e: Exception) {
                val error = e.toString()
                println("Error $e")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (resultCode == ACTIVITY_FINISH_RESULT_CODE) {
                finish()
            }
        } else {
            var bitmap: Bitmap? = null
            var selectedImage: Uri? = null
            if (resultCode != RESULT_OK) return
            when (requestCode) {
                0 -> {
                    selectedImage = data!!.data
                    val root = Environment.getExternalStorageDirectory().absolutePath
                    bitmap = data.extras!!["data"] as Bitmap?
                    val bytes = ByteArrayOutputStream()
                    val myDir = File(root + "/" + resources.getString(R.string.app_name))
                    if (!myDir.exists()) {
                        myDir.mkdirs()
                    }
                    val file = File(
                        myDir, "tmp_avatar_"
                                + System.currentTimeMillis().toString() + ".JPEG"
                    )
                    filePath = file.absolutePath


                    //  imgPath = file.getAbsolutePath();
                    selectedImage = if (Build.VERSION.SDK_INT >= 27) {
                        FileProvider.getUriForFile(
                            this@ProfileActivity,
                            "com.vkc.loyaltyapp.provider",  //(use your app signature + ".provider" )
                            file
                        )
                    } else {
                        Uri.fromFile(file)
                    }
                    // filePath=file.getAbsolutePath();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(
                            mContext!!.contentResolver,
                            selectedImage
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                1 -> {
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = mContext!!.contentResolver.query(
                        data!!.data!!, filePathColumn, null, null, null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath = cursor.getString(columnIndex)
                        //                        File tempFile = new File(imageUri.getPath());
                        val tempFile = File(picturePath)
                        val root1 = Environment.getExternalStorageDirectory().absolutePath
                        val myDir1 = File(root1 + "/" + resources.getString(R.string.app_name))
                        if (!myDir1.exists()) {
                            myDir1.mkdirs()
                        }
                        val file1 = File(
                            myDir1, "tmp_avatar_"
                                    + System.currentTimeMillis().toString() + ".JPEG"
                        )
                        filePath = file1.absolutePath
                        bitmap = BitmapFactory.decodeFile(picturePath)
                        selectedImage = if (Build.VERSION.SDK_INT >= 27) {
                            FileProvider.getUriForFile(
                                this@ProfileActivity,
                                "com.vkc.loyaltyapp.provider",  //(use your app signature + ".provider" )
                                tempFile
                            )
                        } else {
                            Uri.parse(picturePath)
                        }

                        // filePath = selectedImage.getPath();
                        cursor.close()
                    }
                }
            }
            if (bitmap != null) {
                try {
                    val tempFile = File(filePath)
                    val size = tempFile.length()
                    val byteArrayOutputStream: ByteArrayOutputStream
                    Log.e("Size image:", "" + size)
                    val minSize = 600
                    val widthOfImage = bitmap.width
                    val heightOfImage = bitmap.height
                    Log.e("Width", "" + widthOfImage)
                    Log.e("Height", "" + heightOfImage)
                    var newHeight = 600
                    var newWidht = 600
                    if (widthOfImage < minSize && heightOfImage < minSize) {
                        newWidht = widthOfImage
                        newHeight = heightOfImage
                    } else {
                        if (widthOfImage >= heightOfImage) {
                            //int newheght = heightOfImage/600;
                            val ratio = minSize.toFloat() / widthOfImage
                            Log.e(
                                "Multi width greater",
                                "$minSize/$widthOfImage=$ratio"
                            )
                            newHeight = (heightOfImage * ratio).toInt()
                            newWidht = minSize
                        } else if (heightOfImage > widthOfImage) {
                            val ratio = minSize.toFloat() / heightOfImage
                            newWidht = (widthOfImage * ratio).toInt()
                            newHeight = minSize
                        }
                    }
                    if (size > 1024 * 1024) {
                        byteArrayOutputStream = ByteArrayOutputStream()
                        val b: Bitmap
                        b = Bitmap.createScaledBitmap(bitmap, newWidht, newHeight, true)
                        b.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                    } else {
                        byteArrayOutputStream = ByteArrayOutputStream()
                        val b: Bitmap
                        b = Bitmap.createScaledBitmap(bitmap, newWidht, newHeight, true)
                        b.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    }
                    if (size > 4 * 1024 * 1024) {
                        //CustomStatusDialog(RESPONSE_LARGE_IMAGE);
                        if (bitmap != null && !bitmap.isRecycled) {
                            bitmap.recycle()
                            bitmap = null
                        }
                    } else {
                        val width = bitmap.width
                        val height = bitmap.height
                        val bounding = dpToPx(mContext!!.resources.displayMetrics.density)
                        val xScale = 10 * bounding.toFloat() / width
                        val yScale = 100 * bounding.toFloat() / height
                        val scale = if (xScale <= yScale) xScale else yScale
                        val matrix = Matrix()
                        matrix.postScale(scale, scale)
                        val scaledBitmap =
                            Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
                        val result = BitmapDrawable(scaledBitmap)
                        imageProfile!!.setImageDrawable(result)
                        val byteArray = byteArrayOutputStream.toByteArray()
                        val f = File(filePath)
                        try {
                            f.createNewFile()
                            var fos: FileOutputStream? = null
                            fos = FileOutputStream(f)
                            fos.write(byteArray)
                            fos.close()
                            filePath = f.absolutePath
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (bitmap != null && !bitmap.isRecycled) {
                            bitmap.recycle()
                            bitmap = null
                        }
                    }
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                    if (bitmap != null && !bitmap.isRecycled) {
                        bitmap.recycle()
                        bitmap = null
                    }

                    // CustomStatusDialog(RESPONSE_OUT_OF_MEMORY);
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (bitmap != null && !bitmap.isRecycled) {
                        bitmap.recycle()
                        bitmap = null
                    }
                }
            }
        }
    }

    private fun dpToPx(dp: Float): Int {
        val density = mContext!!.resources.displayMetrics.density
        return Math.round(dp * density)
    }

    private fun showCameraGalleryChoice() {
        // imageProfile = imgView;
        val getImageFrom = AlertDialog.Builder(
            mContext!!
        )
        getImageFrom.setTitle(resources.getString(R.string.select_item))
        val opsChars = arrayOf<CharSequence>(
            mContext!!.resources.getString(R.string.take_picture),
            mContext!!.resources.getString(R.string.open_gallery)
        )
        getImageFrom.setItems(
            opsChars
        ) { dialog, which ->
            if (which == 0) {
                /* if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
    */
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val root = Environment.getExternalStorageDirectory().absolutePath
                val myDir = File(root + "/" + resources.getString(R.string.app_name))
                if (!myDir.exists()) {
                    myDir.mkdirs()
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                //   mImageCaptureUri);
                try {
                    cameraIntent.putExtra("return-data", true)
                    startActivityForResult(cameraIntent, 0)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            } else if (which == 1) {
                /*if (alertDialog != null) {
                            alertDialog.dismiss();
                        }*/
                if (Build.VERSION.SDK_INT < 19) {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(
                        Intent.createChooser(
                            intent,
                            resources.getString(
                                R.string.select_item
                            )
                        ), 1
                    )
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        val intent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        intent.type = "image/*"
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        startActivityForResult(
                            Intent.createChooser(
                                intent,
                                resources.getString(
                                    R.string.select_item
                                )
                            ), 1
                        )
                    } else {
                        val intent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        intent.type = "image/*"
                        startActivityForResult(
                            Intent.createChooser(
                                intent,
                                resources.getString(
                                    R.string.select_item
                                )
                            ), 1
                        )
                    }
                }
            }
        }
        getImageFrom.show()
    }

    fun updateMobile() {
        try {
            val name = arrayOf("cust_id", "role", "phone")
            val values = arrayOf(
                AppPrefenceManager.getCustomerId(mContext),
                AppPrefenceManager.getUserType(mContext),
                editMobile!!.text.toString().trim { it <= ' ' })
            val manager = VolleyWrapper(VKCUrlConstants.UPDATE_MOBILE)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                val objResponse = rootObject.optJSONObject("response")
                                val status = objResponse.optString("status")
                                if (status == "Success") {
                                    val dialog = OTPDialog(mContext)
                                    dialog.show()
                                } else {
                                    val toast = CustomToast(mContext)
                                    toast.show(56)
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
            //  Log.d("TAG", "Common error");
        }
    }

    inner class DialogUpdateMobile : Dialog, View.OnClickListener {
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
            setContentView(R.layout.dialog_update_mobile)
            init()
        }

        private fun init() {

            // Button buttonSet = (Button) findViewById(R.id.buttonOk);
            val textYes = findViewById<View>(R.id.textYes) as TextView
            val textNo = findViewById<View>(R.id.textNo) as TextView
            textYes.setOnClickListener {
                dismiss()
                // mActivity.finish();
                updateMobile()
            }
            textNo.setOnClickListener { dismiss() }
        }

        override fun onClick(v: View) {
            dismiss()
        }
    }

    inner class OTPDialog     // TODO Auto-generated constructor stub
        (var mActivity: AppCompatActivity?) : Dialog(mActivity!!),
        View.OnClickListener {
        override fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_otp_mobile)
            init()
        }

        private fun init() {
            val editOtp1 = findViewById<View>(R.id.editOtp1) as EditText
            val editOtp2 = findViewById<View>(R.id.editOtp2) as EditText
            val editOtp3 = findViewById<View>(R.id.editOtp3) as EditText
            val editOtp4 = findViewById<View>(R.id.editOtp4) as EditText
            val textOtp = findViewById<View>(R.id.textOtp) as TextView
            val textCancel = findViewById<View>(R.id.textCancel) as TextView
            val mob = AppPrefenceManager.getMobile(mContext).substring(6, 10)
            textOtp.text = "OTP has been sent to  XXXXXX$mob"
            editOtp1.isCursorVisible = false
            sendOTP()
            textCancel.setOnClickListener { dismiss() }
            /*textResend.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        editOtp1.setText("");
        editOtp2.setText("");
        editOtp3.setText("");
        editOtp4.setText("");
    }
});*/editOtp1.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    editOtp1.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    if (s.length == 1) {
                        editOtp1.clearFocus()
                        editOtp2.requestFocus()
                    }
                    otpValue =
                        editOtp1.text.toString().trim { it <= ' ' } + editOtp2.text.toString()
                            .trim { it <= ' ' } + editOtp3.text.toString()
                            .trim { it <= ' ' } + editOtp4.text.toString().trim { it <= ' ' }
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        editOtp1.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
            })
            editOtp2.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    editOtp2.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    if (s.length == 1) {
                        editOtp2.clearFocus()
                        editOtp3.requestFocus()
                    }
                    otpValue =
                        editOtp1.text.toString().trim { it <= ' ' } + editOtp2.text.toString()
                            .trim { it <= ' ' } + editOtp3.text.toString()
                            .trim { it <= ' ' } + editOtp4.text.toString().trim { it <= ' ' }
                    if (otpValue.length == 1) {
                        editOtp2.clearFocus()
                        editOtp1.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        editOtp2.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
            })
            editOtp3.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    editOtp3.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    if (s.length == 1) {
                        editOtp3.clearFocus()
                        editOtp4.requestFocus()
                    }
                    otpValue =
                        editOtp1.text.toString().trim { it <= ' ' } + editOtp2.text.toString()
                            .trim { it <= ' ' } + editOtp3.text.toString()
                            .trim { it <= ' ' } + editOtp4.text.toString().trim { it <= ' ' }
                    if (otpValue.length == 2) {
                        editOtp3.clearFocus()
                        editOtp2.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        editOtp3.setBackgroundResource(R.drawable.rounded_rect_line)
                    }
                }
            })
            editOtp4.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    editOtp4.setBackgroundResource(R.drawable.rounded_rect_full_white)
                    otpValue =
                        editOtp1.text.toString().trim { it <= ' ' } + editOtp2.text.toString()
                            .trim { it <= ' ' } + editOtp3.text.toString()
                            .trim { it <= ' ' } + editOtp4.text.toString().trim { it <= ' ' }
                    if (otpValue.length == 3) {
                        editOtp3.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.length == 0) {
                        editOtp4.setBackgroundResource(R.drawable.rounded_rect_line)
                    } else {
                        verifyOTP(otpValue, editMobile!!.text.toString().trim { it <= ' ' })
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

    fun verifyOTP(otp: String, mobile: String) {
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


                                    //    AppPrefenceManager.setIsVerifiedOTP(mContext, "yes");
                                    val toast = CustomToast(mContext)
                                    toast.show(55)
                                    val intent =
                                        Intent(this@ProfileActivity, SignUpActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
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

    fun sendOTP() {
        try {
            val name = arrayOf("role", "cust_id", "sms_key")
            val values = arrayOf(
                AppPrefenceManager.getUserType(mContext),
                AppPrefenceManager.getCustomerId(mContext),
                ""
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
}