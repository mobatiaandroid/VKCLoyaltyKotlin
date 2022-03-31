package com.vkc.loyaltyapp.activity.shopimage

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import com.vkc.loyaltyapp.constants.VKCUrlConstants
import com.vkc.loyaltyapp.manager.HeaderManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import com.vkc.loyaltyapp.R
import androidx.recyclerview.widget.GridLayoutManager
import android.os.Build
import com.vkc.loyaltyapp.utils.CameraUtils
import com.vkc.loyaltyapp.utils.CustomToast
import com.vkc.loyaltyapp.activity.shopimage.ShopImageActivity.UploadFileToServer
import android.os.AsyncTask
import org.json.JSONObject
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.content.FileBody
import com.vkc.loyaltyapp.utils.AndroidMultiPartEntity
import org.apache.http.entity.mime.content.StringBody
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import org.apache.http.HttpEntity
import org.apache.http.util.EntityUtils
import org.apache.http.client.ClientProtocolException
import android.content.Intent
import android.provider.MediaStore
import android.content.ActivityNotFoundException
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper
import com.vkc.loyaltyapp.volleymanager.VolleyWrapper.ResponseListener
import com.squareup.picasso.Picasso
import com.vkc.loyaltyapp.appcontroller.AppController
import com.vkc.loyaltyapp.activity.shopimage.ShopImageActivity
import org.json.JSONArray
import com.vkc.loyaltyapp.activity.shopimage.model.ImageListModel
import com.vkc.loyaltyapp.activity.shopimage.adapter.ShopImageAdapter
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.http.client.HttpClient
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

/**
 */
class ShopImageActivity : AppCompatActivity(), VKCUrlConstants, View.OnClickListener {
    var imageShop: ImageView? = null

    companion object{
        lateinit var mActivity: Activity
        lateinit var mContext:Context
        var filePath=""


    }
    var headermanager: HeaderManager? = null
    var relativeHeader: LinearLayout? = null
    var mImageBack: ImageView? = null
    private var mImageCaptureUri: Uri? = null
    var ACTIVITY_REQUEST_CODE = 700
    var ACTIVITY_FINISH_RESULT_CODE = 701
    var btnCapture: Button? = null
    var btnUpload: Button? = null
    private var destination: File? = null
    var image1Delete: ImageView? = null
    var image2Delete: ImageView? = null
    var relative1: RelativeLayout? = null
    var relative2: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_image)
        mContext = this
        initUI()
    }

    private fun initUI() {
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        headermanager =
            HeaderManager(this@ShopImageActivity, resources.getString(R.string.shop_image))
        headermanager!!.getHeader(relativeHeader, 1)
        mImageBack = headermanager!!.leftButton
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        imageShop = findViewById<View>(R.id.imageShop) as ImageView
        /* image1 = (ImageView) findViewById(R.id.imageOne);
        image2 = (ImageView) findViewById(R.id.imageTwo);
        image1Delete = (ImageView) findViewById(R.id.deleteImage1);
        image2Delete = (ImageView) findViewById(R.id.deleteImage2);
        image1Delete.setVisibility(View.GONE);
        image2Delete.setVisibility(View.GONE);
        relative1 = (RelativeLayout) findViewById(R.id.relative1);*/relative2 =
            findViewById<View>(R.id.relative2) as RelativeLayout
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        //  LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        recyclerView!!.layoutManager = gridLayoutManager
        recyclerView!!.setHasFixedSize(true)
        btnCapture = findViewById<View>(R.id.buttonCapture) as Button
        btnUpload = findViewById<View>(R.id.buttonUpload) as Button
        getImage()
        mImageBack!!.setOnClickListener(this)
        btnCapture!!.setOnClickListener(this)
        btnUpload!!.setOnClickListener(this)
        // image1.setOnClickListener(this);
        // image2.setOnClickListener(this);
        // image1Delete.setOnClickListener(this);
        // image2Delete.setOnClickListener(this);
    }

    override fun onClick(v: View) {
        if (v === mImageBack) {
            finish()
        } else if (v === btnCapture) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (CameraUtils.checkPermissions(applicationContext)) {
                    captureImage()
                } else {
                    requestCameraPermission(1)
                }
            } else {
                captureImage()
            }
        } else if (v === btnUpload) {
            if (filePath == "") {
                val toast = CustomToast(
                    mActivity
                )
                toast.show(21)
            } else {

                /* if(AppController.imageList.size()==Integer.parseInt(limit_value))
                {
                    CustomToast toast = new CustomToast(
                            mContext);
                    toast.show(20);
                }
                else {*/
                try {
                    val upload = UploadFileToServer()
                    upload.execute()
                } catch (e: Exception) {
                }
                //}
            }
        } /*else if (v == image1Delete) {

            DialogConfirm dialog = new DialogConfirm(mContext, imageList.get(0).getId());
            dialog.show();
            //  deleteImage(imageList.get(0).getId());
        } else if (v == image2Delete) {

            DialogConfirm dialog = new DialogConfirm(mContext, imageList.get(1).getId());
            dialog.show();

        }*/
    }

     class UploadFileToServer : AsyncTask<Void?, Int?, String?>() {
        val pDialog = ProgressDialog(mActivity)
        private var obj: JSONObject? = null
        private var responseString = ""
        override fun onPreExecute() {
            super.onPreExecute()
            pDialog.setMessage("Uploading...")
            pDialog.setCanceledOnTouchOutside(false)
            pDialog.show()
        }

         override fun onProgressUpdate(vararg values: Int?) {}
         override fun doInBackground(vararg p0: Void?): String? {
            return uploadFile()
        }

        private fun uploadFile(): String? {
            var responseString: String? = null
            try {
                val httpclient: HttpClient = DefaultHttpClient()
                val httppost = HttpPost(VKCUrlConstants.UPLOAD_IMAGE)
                val file = File(filePath)
                val bin1 = FileBody(file.absoluteFile)
                val entity: AndroidMultiPartEntity
                entity = AndroidMultiPartEntity { }
                entity.addPart("cust_id", StringBody(AppPrefenceManager.getCustomerId(mContext)))
                entity.addPart("role", StringBody(AppPrefenceManager.getUserType(mContext)))
                entity.addPart("image", bin1)
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
            try {
                obj = JSONObject(result)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val responseObj = obj!!.optJSONObject("response")
            responseString = responseObj.optString("status")
            if (responseString.equals("Success", ignoreCase = true)) {
                val toast = CustomToast(
                    mActivity
                )
                toast.show(19)
              //  GlobalScope.launch(Dispatchers.Main) {
                   // getImageHistory()

             //   }
            } else if (responseString.equals("Exceeded", ignoreCase = true)) {
                val toast = CustomToast(
                    mActivity
                )
                toast.show(20)
              //  getImage()
            } else {
                val toast = CustomToast(
                    mActivity
                )
                toast.show(0)
            }
        }

     }

    /*   public void showCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/" + getResources().getString(R.string.app_name));
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, "tmp_avatar_"
                + String.valueOf(System.currentTimeMillis()) + ".JPEG");
        if (Build.VERSION.SDK_INT >= 23) {

            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        }
            if (Build.VERSION.SDK_INT >= 27) {
            mImageCaptureUri = FileProvider.getUriForFile(
                    ShopImageActivity.this,
                    "com.vkc.loyaltyapp.provider", //(use your app signature + ".provider" )
                    file);
        } else {
            mImageCaptureUri = Uri.fromFile(file);

        }

        // cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        filePath = file.getAbsolutePath();


        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        try {
            cameraIntent.putExtra("return-data", true);
            startActivityForResult(cameraIntent, 0);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }*/
    fun showCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val root = Environment.getExternalStorageDirectory().absolutePath
        val myDir = File(root + "/" + resources.getString(R.string.app_name))
        myDir.mkdirs()
        val file = File(
            myDir, "tmp_avatar_"
                    + System.currentTimeMillis().toString() + ".JPEG"
        )
        mImageCaptureUri = Uri.fromFile(file)
        cameraIntent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            mImageCaptureUri
        )
        try {
            cameraIntent.putExtra("return-data", true)
            startActivityForResult(cameraIntent, 0)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            try {
                val selectedImage = data!!.data
                val bitmap = data.extras!!["data"] as Bitmap?
                val bytes = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, bytes)

                //Log.e("Activity", "Pick from Camera::>>> ");
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                    Date()
                )
                destination = File(
                    Environment.getExternalStorageDirectory().toString() + "/" +
                            getString(R.string.app_name), "IMG_$timeStamp.jpg"
                )
                destination!!.mkdirs()
                val fo: FileOutputStream
                try {
                    if (destination!!.exists()) destination!!.delete()
                    destination!!.createNewFile()
                    fo = FileOutputStream(destination)
                    fo.write(bytes.toByteArray())
                    fo.flush()
                    fo.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                filePath = destination!!.absolutePath
                imageShop!!.setImageBitmap(bitmap)

                //    CallForAPI(true);
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun dpToPx(dp: Float): Int {
        val density = mContext!!.resources.displayMetrics.density
        return Math.round(dp * density)
    }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);//    } else {

    //  CustomToast toast = new CustomToast(mContext);
    //    toast.show(4);
    //   }
    //    System.out.println("Response---Login" + successResponse);
       fun getImage() {
            try {
                val name = arrayOf("cust_id", "role")
                val values = arrayOf(
                    AppPrefenceManager.getCustomerId(mContext),
                    AppPrefenceManager.getUserType(mContext)
                )
                val manager = VolleyWrapper(VKCUrlConstants.UPLOADED_IMAGE)
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
                                        val imageUrl = objData.optString("image")
                                        Picasso.with(mContext).load(imageUrl)
                                            .placeholder(R.drawable.shop_image).into(imageShop)
                                        getImageHistory()

                                    }

                                    //    } else {
                                    //  CustomToast toast = new CustomToast(mContext);
                                    //    toast.show(4);
                                    //   }
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                            } else {
                                val toast = CustomToast(mActivity)
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
        }// CustomStatusDialog(RESPONSE_FAILURE);//CustomStatusDialog(RESPONSE_FAILURE);// initUI();
    /* String imageUrl = objData.optString("image");
Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.shop_image).into(imageShop);*/

    //    } else {
    //  CustomToast toast = new CustomToast(mContext);
    //    toast.show(4);
    //   }
/*      if (imageList.size() > 1) {
                                                if (!imageList.get(0).getImage().equals("")) {

                                                    relative1.setVisibility(View.VISIBLE);
                                                    image1Delete.setVisibility(View.VISIBLE);
                                                    Picasso.with(mContext).load(imageList.get(0).getImage()).resize(200, 200).centerInside().into(image1);
                                                } else {
                                                    relative1.setVisibility(View.GONE);
                                                    image1Delete.setVisibility(View.GONE);

                                                }

                                                if (!imageList.get(1).getImage().equals("")) {
                                                    relative2.setVisibility(View.VISIBLE);
                                                    image2Delete.setVisibility(View.VISIBLE);
                                                    Picasso.with(mContext).load(imageList.get(1).getImage()).resize(200, 200).centerInside().into(image2);
                                                } else {
                                                    relative2.setVisibility(View.GONE);
                                                    image2Delete.setVisibility(View.GONE);
                                                }
                                            } else {

                                                relative2.setVisibility(View.GONE);
                                                image2Delete.setVisibility(View.GONE);
                                                if (!imageList.get(0).getImage().equals("")) {

                                                    relative1.setVisibility(View.VISIBLE);
                                                    image1Delete.setVisibility(View.VISIBLE);
                                                    Picasso.with(mContext).load(imageList.get(0).getImage()).resize(200, 200).centerInside().into(image1);
                                                } else {
                                                    relative1.setVisibility(View.GONE);
                                                    image1Delete.setVisibility(View.GONE);

                                                }
                                            }*/
    //    System.out.println("Response---Login" + successResponse);
    fun  getImageHistory() {
            AppController.imageList.clear()
            try {
                val name = arrayOf("cust_id", "role")
                val values = arrayOf(
                    AppPrefenceManager.getCustomerId(mContext),
                    AppPrefenceManager.getUserType(mContext)
                )
                val manager = VolleyWrapper(VKCUrlConstants.GET_IMAGE_HISTORY)
                manager.getResponsePOST(mContext, 11, name, values,
                    object : ResponseListener {
                        override fun responseSuccess(successResponse: String) {
                            //    System.out.println("Response---Login" + successResponse);
                            if (successResponse != null) {
                                try {
                                    val rootObject = JSONObject(successResponse)
                                    val objResponse = rootObject.optJSONObject("response")
                                    val status = objResponse.optString("status")
                                    var limit_value = objResponse.optString("image_limit")
                                    if (status == "Success") {
                                        val objData = objResponse.optJSONArray("data")
                                        if (objData.length() > 0) {
                                            for (i in 0 until objData.length()) {
                                                val obj = objData.optJSONObject(i)
                                                val model = ImageListModel()
                                                model.image = obj.getString("image")
                                                model.id = obj.getString("id")
                                                AppController.imageList.add(model)
                                            }
                                            /*      if (imageList.size() > 1) {
                   if (!imageList.get(0).getImage().equals("")) {

                       relative1.setVisibility(View.VISIBLE);
                       image1Delete.setVisibility(View.VISIBLE);
                       Picasso.with(mContext).load(imageList.get(0).getImage()).resize(200, 200).centerInside().into(image1);
                   } else {
                       relative1.setVisibility(View.GONE);
                       image1Delete.setVisibility(View.GONE);

                   }

                   if (!imageList.get(1).getImage().equals("")) {
                       relative2.setVisibility(View.VISIBLE);
                       image2Delete.setVisibility(View.VISIBLE);
                       Picasso.with(mContext).load(imageList.get(1).getImage()).resize(200, 200).centerInside().into(image2);
                   } else {
                       relative2.setVisibility(View.GONE);
                       image2Delete.setVisibility(View.GONE);
                   }
               } else {

                   relative2.setVisibility(View.GONE);
                   image2Delete.setVisibility(View.GONE);
                   if (!imageList.get(0).getImage().equals("")) {

                       relative1.setVisibility(View.VISIBLE);
                       image1Delete.setVisibility(View.VISIBLE);
                       Picasso.with(mContext).load(imageList.get(0).getImage()).resize(200, 200).centerInside().into(image1);
                   } else {
                       relative1.setVisibility(View.GONE);
                       image1Delete.setVisibility(View.GONE);

                   }
               }*/
                                            val adapter = ShopImageAdapter(
                                                mActivity,
                                                AppController.imageList,
                                                recyclerView
                                            )
                                            recyclerView!!.adapter = adapter
                                        } else {
                                            relative1!!.visibility = View.GONE
                                            image1Delete!!.visibility = View.GONE
                                            relative2!!.visibility = View.GONE
                                            image2Delete!!.visibility = View.GONE
                                            // initUI();
                                            val toast = CustomToast(mActivity)
                                            toast.show(51)
                                        }
                                        /* String imageUrl = objData.optString("image");
              Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.shop_image).into(imageShop);*/
                                    }

                                    //    } else {
                                    //  CustomToast toast = new CustomToast(mContext);
                                    //    toast.show(4);
                                    //   }
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                            } else {
                                val toast = CustomToast(mActivity)
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

    fun deleteImage(id: String) {
        try {
            val name = arrayOf("id")
            val values = arrayOf(id)
            val manager = VolleyWrapper(VKCUrlConstants.DELETE_IMAGE)
            manager.getResponsePOST(mContext, 11, name, values,
                object : ResponseListener {
                    override fun responseSuccess(successResponse: String) {
                        //    System.out.println("Response---Login" + successResponse);
                        if (successResponse != null) {
                            try {
                                val rootObject = JSONObject(successResponse)
                                //JSONObject objResponse = rootObject.optJSONObject("response");
                                val status = rootObject.optString("status")
                                if (status == "Success") {
                                    val toast = CustomToast(mActivity)
                                    toast.show(52)
                                    getImageHistory()
                                } else if (status == "Error") {
                                    getImageHistory()
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        } else {
                            val toast = CustomToast(mActivity)
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

    inner class DialogConfirm     // TODO Auto-generated constructor stub
        (var mActivity: AppCompatActivity, var id: String) : Dialog(
        mActivity
    ), View.OnClickListener {
        var type: String? = null
        var message: String? = null
        override fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_delete_image)
            init()
        }

        private fun init() {

            // Button buttonSet = (Button) findViewById(R.id.buttonOk);
            val textYes = findViewById<View>(R.id.textYes) as TextView
            val textNo = findViewById<View>(R.id.textNo) as TextView
            textYes.setOnClickListener {
                deleteImage(id)
                dismiss()
            }
            textNo.setOnClickListener {
                dismiss()
                // mActivity.finish();
            }
        }

        override fun onClick(v: View) {
            dismiss()
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri?): String {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    private fun requestCameraPermission(type: Int) {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if (type == 100) {
                            // capture picture
                            captureImage()
                        } else {
                            // captureVideo();
                        }
                    } else if (report.isAnyPermissionPermanentlyDenied) {
                        showPermissionsAlert()
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

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 1)
    }

    private fun showPermissionsAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permissions required!")
            .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
            .setPositiveButton("GOTO SETTINGS") { dialog, which -> CameraUtils.openSettings(this@ShopImageActivity) }
            .setNegativeButton("CANCEL") { dialog, which -> }.show()
    }

    private fun previewCapturedImage() {
        try {
            // hide video preview
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }


}