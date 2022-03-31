package com.vkc.loyaltyapp.fcmservice

import com.google.firebase.messaging.FirebaseMessagingService
import android.content.Intent
import android.graphics.Bitmap
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import com.vkc.loyaltyapp.fcmservice.MyFirebaseMessagingService
import android.annotation.TargetApi
import com.vkc.loyaltyapp.activity.HomeActivity
import android.app.PendingIntent
import com.vkc.loyaltyapp.activity.news.NewsActivity
import android.media.RingtoneManager
import com.vkc.loyaltyapp.R
import android.graphics.BitmapFactory
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import android.util.Log
import androidx.core.app.NotificationCompat
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import org.json.JSONException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Created by Bibin Johnson
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    var intent: Intent? = null
    var bitmap: Bitmap? = null
    var mType: String? = null

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //   Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            // Log.d(TAG, "Message data payload: " + remoteMessage.getData().toString());
//            String questionTitle = data.get("questionTitle").toString();
            try {
                val json = JSONObject(remoteMessage.data.toString().replace("=".toRegex(), ":"))
                handleDataMessage(json)
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }
        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
////            sendNotification(remoteMessage.getNotification().getBody());
//
//            sendNotification(remoteMessage.getNotification().getBody());
//            // Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
    }

    @TargetApi(26)
    private fun sendNotification(messageBody: String, news_id: String) {
        val Number = Random()
        val Rnumber = Number.nextInt(100)
        // if (mType.equals("Text")) {
        var intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        if (news_id == "") {
            intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
        } else {
            intent = Intent(this, NewsActivity::class.java)
            intent.putExtra("news_id", news_id)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.notifi_vkc)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
            ) // .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.notifi_vkc)
        if (Build.VERSION.SDK_INT >= 23) {
//            notificationBuilder.setSmallIcon(R.drawable.notifyicons);
            notificationBuilder.color = resources.getColor(R.color.split_bg)
            //    notificationBuilder.setSmallIcon(R.drawable.not_large);
            notificationBuilder.setLargeIcon(largeIcon)
        } else {
            notificationBuilder.setSmallIcon(R.drawable.notifi_vkc)
            //            notificationBuilder.setSmallIcon(R.drawable.notifyicons);
//            notificationBuilder.setColor(getResources().getColor(R.color.tictapHeader));
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = getString(R.string.app_name) + "_01" // The id of the channel.
            val name: CharSequence =
                getString(R.string.app_name) // The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationBuilder.setChannelId(mChannel.id)
            mChannel.setShowBadge(true)
            mChannel.canShowBadge()
            mChannel.enableLights(true)
            mChannel.lightColor = resources.getColor(R.color.colorPrimaryDark)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            assert(notificationManager != null)
            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(Rnumber, notificationBuilder.build())
    }

    private fun handleDataMessage(json: JSONObject) {
        //   Log.e(TAG, "push json: " + json.toString());
        var news_id = ""
        try {
            val data = json.getJSONObject("body")
            val message = data.optString("message")
            news_id = data.optString("newsid")
            val title = data.optString("title")
            val image = data.optString("image")
            if (image.length > 0) {
                bitmap = getBitmapfromUrl(image)
            }
            sendNotification(message, news_id)
            // sendNotification(message);
        } catch (e: JSONException) {
            Log.e(TAG, "Json Exception: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }
    }

    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        AppPrefenceManager.saveToken(applicationContext, s)
    } /*  public void getNotificationType(JSONObject json) {
        try {
            mType = json.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }*/

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        const val FCM_PARAM = "picture"
        private const val CHANNEL_NAME = "FCM"
        private const val CHANNEL_DESC = "Firebase Cloud Messaging"
    }
}