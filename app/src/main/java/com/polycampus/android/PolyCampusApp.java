package com.polycampus.android;

import android.app.Application;
import android.os.Build;
import net.gotev.uploadservice.UploadServiceConfig;

/**
 * Main Application class for PolyCampus.
 * Initializes global services like net.gotev uploadservice with Android 12 support.
 */
public class PolyCampusApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Upload Service
        // This is critical for Android 12+ (S+) to handle PendingIntent flags correctly
        UploadServiceConfig.initialize(this, "poly_campus_notification_channel", Build.VERSION.SDK_INT >= Build.VERSION_CODES.O);
    }
}
