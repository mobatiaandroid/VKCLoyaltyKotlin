package com.vkc.loyaltyapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSReceiver extends BroadcastReceiver {private static final String TAG = "SmsBroadcastReceiver";
    OtpReceivedInterface otpReceiveInterface = null;

    public void setOnOtpListeners(OtpReceivedInterface otpReceiveInterface) {
        this.otpReceiveInterface = otpReceiveInterface;
    }

    @Override public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            assert extras != null;
            Status mStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            assert mStatus != null;
            switch (mStatus.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents'
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.d(TAG, "onReceive: failure " + message);
                    if (otpReceiveInterface != null) {
                        assert message != null;
                        String otpMessage = message.replace("<#> Your otp code is: ", "");
                        String otp = otpMessage.split("\n")[0];
                        otpReceiveInterface.onOtpReceived(otp);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    Log.d(TAG, "onReceive: failure");
                    if (otpReceiveInterface != null) {
                        otpReceiveInterface.onOtpTimeout();
                    }
                    break;
            }
        }
    }
}
