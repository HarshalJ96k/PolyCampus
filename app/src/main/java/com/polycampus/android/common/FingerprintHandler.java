package com.polycampus.android.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.polycampus.android.Attendance.FaceVerificationActivity;
import com.polycampus.android.Attendance.GetStudentCurrentLocationandSubmitAttendanceActivity;
import com.polycampus.android.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ProgressBar progress;
    String username;

    public FingerprintHandler(Context context) {
        this.context = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There was an auth error:" + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Auth Failed", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error:" + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("You can now access the app", true);
    }

    private void update(String s, boolean b) {

        TextView txt_description = ((Activity) context).findViewById(R.id.txt_description);
        ImageView img_fingerprint_scanner = ((Activity) context).findViewById(R.id.img_fingerprint_scanner);
        progress = ((Activity) context).findViewById(R.id.progress);
        txt_description.setText(s);
        if (b == false) {
            txt_description.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else {
            txt_description.setTextColor(ContextCompat.getColor(context, R.color.blue));
            img_fingerprint_scanner.setImageResource(R.drawable.ic_done);
//            addAttendance();
            new Thread()
            {
                public void run()
                {
                    try {
                        sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        Intent intent = new Intent(context, FaceVerificationActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish(); // Finish current activity to clean up
                    }
                }
            }.start();
        }
    }

}
