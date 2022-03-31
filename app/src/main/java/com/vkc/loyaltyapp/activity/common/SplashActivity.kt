package com.vkc.loyaltyapp.activity.common

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vkc.loyaltyapp.R
import com.vkc.loyaltyapp.manager.AppPrefenceManager
import android.content.Intent
import com.vkc.loyaltyapp.activity.HomeActivity
import com.vkc.loyaltyapp.activity.dealers.DealersActivity
import com.vkc.loyaltyapp.activity.common.SignUpActivity
import android.os.CountDownTimer
import com.vkc.loyaltyapp.activity.common.TermsandConditionActivity

class SplashActivity : AppCompatActivity() {
    //VideoView videoView;
    var mContext: Context? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mContext = this
        /* videoView = (VideoView) findViewById(R.id.videoView);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        videoView.setVideoURI(video);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                startNextActivity();
            }
        });

        videoView.start();*/loadSplash()
    }

    private fun startNextActivity() {
        if (isFinishing) return
        if (AppPrefenceManager.getLoginStatusFlag(mContext) == "yes") {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else if (AppPrefenceManager.getIsVerifiedOTP(mContext) == "yes") {
            if (AppPrefenceManager.getDealerCount(mContext) > 0) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, DealersActivity::class.java))
                finish()
            }
        } else {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun loadSplash() {
        val countDownTimer: CountDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (AppPrefenceManager.getAgreeTerms(mContext)) {
                    startNextActivity()
                } else {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            TermsandConditionActivity::class.java
                        )
                    )
                    finish()
                }

                //
            }
        }
        countDownTimer.start()
    }
}