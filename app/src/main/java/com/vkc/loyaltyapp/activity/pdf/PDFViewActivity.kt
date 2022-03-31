package com.vkc.loyaltyapp.activity.pdf

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.utils.UtilityMethods
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class PDFViewActivity : AppCompatActivity() {
    var llDownload: LinearLayout? = null
    var pdf: PDFView? = null
    var url: String? = null
    var name: String? = null
    var extras: Bundle? = null
    var mContext: Context? = null
    var mProgressDialog: ProgressDialog? = null
    var mImageBack: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_pdfview)
        mContext = this
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        //  LocaleHelper.setLocale(getApplicationContext(), PrefUtils.getLanguageString(mContext));
        extras = intent.extras
        if (extras != null) {
            url = extras!!.getString("pdf")
            url = url!!.replace(" ".toRegex(), "%20")
            //  title = extras.getString("title");
            name = extras!!.getString("name")
        }
        llDownload = findViewById(R.id.llDownload)
        pdf = findViewById(R.id.pdfView)
        mImageBack = findViewById<View>(R.id.btn_left) as ImageView
        mImageBack!!.setOnClickListener { finish() }
        llDownload!!.setOnClickListener(View.OnClickListener {
            if (UtilityMethods.isNetworkConnected(mContext)) {
                mProgressDialog = ProgressDialog(this@PDFViewActivity)
                mProgressDialog!!.setMessage("Downloading..")
                mProgressDialog!!.isIndeterminate = true
                mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                mProgressDialog!!.setCancelable(true)
                DownloadPDF().execute()
                mProgressDialog!!.setOnCancelListener {
                    DownloadPDF()
                        .cancel(true) //cancel the task
                }
            } else {
                Toast.makeText(mContext, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
        val permissionListenerGallery: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                if (UtilityMethods.isNetworkConnected(mContext)) {
                    loadPDF().execute()
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
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()

        //
    }

    inner class loadPDF : AsyncTask<String?, Void?, Void?>() {
        private val exception: Exception? = null
        private var dialog: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()
            dialog = ProgressDialog(this@PDFViewActivity)
            dialog!!.setMessage("Please wait...") //Please wait...
            dialog!!.show()
        }

        protected override fun doInBackground(vararg p0: String?): Void? {
            var u: URL? = null
            try {
                val fileName = "document.pdf"
                u = URL(url)
                val c = u.openConnection() as HttpURLConnection
                c.requestMethod = "GET"
                // c.setDoOutput(true);
                val auth = "SGHCXFTPUser" + ":" + "cXFTPu$3r"
                var encodedAuth: String =
                    Base64.encodeToString(auth.toByteArray(), Base64.DEFAULT)
                encodedAuth = encodedAuth.replace("\n", "")
                c.addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                c.addRequestProperty("Authorization", "Basic $encodedAuth")
                //c.setRequestProperty("Accept", "application/json");
                // c.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                c.connect()
                val response = c.responseCode
                val PATH = Environment.getExternalStorageDirectory()
                    .toString() + "/download/"
                // Log.d("Abhan", "PATH: " + PATH);
                val file = File(PATH)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val outputFile = File(file, fileName)
                val fos = FileOutputStream(outputFile)
                val `is` = c.inputStream
                val buffer = ByteArray(1024)
                var len1 = 0
                while (`is`.read(buffer).also { len1 = it } != -1) {
                    fos.write(buffer, 0, len1)
                }
                fos.flush()
                fos.close()
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            val file =
                File(Environment.getExternalStorageDirectory().absolutePath + "/download/" + "document.pdf")
            val uri =
                Uri.parse(Environment.getExternalStorageDirectory().absolutePath + "/download/" + "document.pdf")
            // System.out.println("file.exists() = " + file.exists());
            // pdf.fromUri(uri);
            pdf!!.fromFile(file).defaultPage(0).enableSwipe(true).load()

            //web.loadUrl(Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + "test.pdf");
            // Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_LONG).show();
        }
    }

    inner class DownloadPDF : AsyncTask<String?, Void?, Void?>() {
        private val exception: Exception? = null
        private var dialog: ProgressDialog? = null
        var filename = name!!.replace(" ", "_")
        var fileName = "$filename.pdf"
        override fun onPreExecute() {
            super.onPreExecute()
            dialog = ProgressDialog(this@PDFViewActivity)
            dialog!!.setMessage("Downloading please wait...") //Please wait...
            dialog!!.show()
        }

        protected override fun doInBackground(vararg p0: String?): Void? {
            var u: URL? = null
            try {
                u = URL(url)
                val c = u.openConnection() as HttpURLConnection
                c.requestMethod = "GET"
                // c.setDoOutput(true);


                /* String auth = "SGHCXFTPUser" + ":" + "cXFTPu$3r";
                String encodedAuth = new String(Base64.encodeToString(auth.getBytes(), Base64.DEFAULT));
                encodedAuth = encodedAuth.replace("\n", "");
*/c.addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                //c.addRequestProperty("Authorization", "Basic " + encodedAuth);
                //c.setRequestProperty("Accept", "application/json");
                // c.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                c.connect()
                val response = c.responseCode
                val PATH = Environment.getExternalStorageDirectory()
                    .toString() + "/download/"
                // Log.d("Abhan", "PATH: " + PATH);
                val file = File(PATH)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val outputFile = File(file, fileName)
                val fos = FileOutputStream(outputFile)
                val `is` = c.inputStream
                val buffer = ByteArray(1024)
                var len1 = 0
                while (`is`.read(buffer).also { len1 = it } != -1) {
                    fos.write(buffer, 0, len1)
                }
                fos.flush()
                fos.close()
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            val file =
                File(Environment.getExternalStorageDirectory().absolutePath + "/download/" + fileName)
            val uri =
                Uri.parse(Environment.getExternalStorageDirectory().absolutePath + "/download/" + fileName)
            println("file.exists() = " + file.exists())
            if (file.exists()) {
                Toast.makeText(mContext, "File Downloaded to Downloads folder", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    mContext,
                    "Something Went Wrong. Download failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // pdf.fromUri(uri);

            // pdf.fromFile(file).defaultPage(1).enableSwipe(true).load();

            //web.loadUrl(Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + "test.pdf");
            // Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_LONG).show();
        }
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onStop() {
        super.onStop()
        //        stopDisconnectTimer();
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun getFilepath(filename: String): String {
        return File(
            Environment.getExternalStorageDirectory().absolutePath,
            "/Download/$filename"
        ).path
    }
}