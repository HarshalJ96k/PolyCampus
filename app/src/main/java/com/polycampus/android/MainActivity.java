package com.polycampus.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100; // Unique request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);

        ImageView ivLogo;
        Animation animtranslate;

        ivLogo = findViewById(R.id.ivsplashscreen);
        animtranslate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.lefttorighttranslate);
        ivLogo.startAnimation(animtranslate);
        ivLogo.setAnimation(animtranslate);

        proceedToNextScreen();

//        // Check and request permissions
//        if (!hasAllPermissions()) {
//            requestPermissions();
//        } else {
//            proceedToNextScreen();
//        }
    }

//    private boolean hasAllPermissions() {
//        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//    }

//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(this, new String[]{
//                Manifest.permission.INTERNET,
//                Manifest.permission.ACCESS_NETWORK_STATE,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.USE_BIOMETRIC,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//        }, PERMISSION_REQUEST_CODE);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            boolean allGranted = true;
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    allGranted = false;
//                    break;
//                }
//            }
//
//            if (allGranted) {
//                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
//                proceedToNextScreen();
//            } else {
//                Toast.makeText(this, "Permissions denied. The app may not work correctly.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    private void proceedToNextScreen() {
        new Handler().postDelayed(() -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }, 3000);
    }
}
