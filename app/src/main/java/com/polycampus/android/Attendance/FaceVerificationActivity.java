package com.polycampus.android.Attendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.face.Face;
import com.polycampus.android.R;
import com.polycampus.android.common.OverlayView;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceVerificationActivity extends AppCompatActivity {
    private static final String TAG = "FaceVerification";
    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final double MATCH_THRESHOLD = 0.70; // High confidence threshold

    private PreviewView previewView;
    private OverlayView overlayView;
    private TextView txtStatus, txtInstruction, txtTimer;
    private ExecutorService cameraExecutor;
    private FaceVerificationHelper faceHelper;
    private float[] storedFaceEmbedding;
    private boolean isProcessing = false;
    private boolean isVerified = false;
    private int attemptsRemaining = 3;
    private CountDownTimer timeoutTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_verification);

        previewView = findViewById(R.id.previewView);
        overlayView = findViewById(R.id.overlayView);
        txtStatus = findViewById(R.id.txtFaceStatus);
        txtInstruction = findViewById(R.id.txtFaceInstruction);
        txtTimer = findViewById(R.id.txtTimer);

        cameraExecutor = Executors.newSingleThreadExecutor();

        try {
            faceHelper = new FaceVerificationHelper(this);
            loadStoredProfileImage();
        } catch (IOException e) {
            Log.e(TAG, "Initialization failed", e);
            Toast.makeText(this, "Error: TFLite model model missing. Ensure facenet.tflite is in Assets.", Toast.LENGTH_LONG).show();
            finish();
        }

        if (allPermissionsGranted()) {
            startCamera();
            startTimeoutTimer();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void loadStoredProfileImage() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String profilePath = prefs.getString("profilephoto", "");
        if (profilePath.isEmpty() || !new File(profilePath).exists()) {
            Toast.makeText(this, "Profile image missing. Please update profile first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Bitmap storedBitmap = BitmapFactory.decodeFile(profilePath);
        faceHelper.detectFaces(storedBitmap).addOnSuccessListener(faces -> {
            if (!faces.isEmpty()) {
                Face face = faces.get(0);
                storedFaceEmbedding = faceHelper.getFaceEmbedding(storedBitmap, face.getBoundingBox());
                Log.d(TAG, "Stored face embedding generated");
            } else {
                Toast.makeText(this, "Stored profile image has no clear face. Update photo.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void startTimer(int seconds) {
        if (timeoutTimer != null) timeoutTimer.cancel();
        timeoutTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Time remaining: " + (millisUntilFinished / 1000) + "s");
            }
            @Override
            public void onFinish() {
                handleVerificationFailure("Verification Timeout ⏳");
            }
        }.start();
    }

    private void startTimeoutTimer() {
        startTimer(10);
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::processImageProxy);

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera launch failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processImageProxy(ImageProxy imageProxy) {
        if (isProcessing || isVerified || storedFaceEmbedding == null) {
            imageProxy.close();
            return;
        }

        isProcessing = true;
        
        Bitmap bitmap = toBitmap(imageProxy);
        // Correct rotation for front camera
        Matrix matrix = new Matrix();
        matrix.postRotate(270); // Usually need rotation for portrait
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        faceHelper.detectFaces(rotatedBitmap).addOnSuccessListener(faces -> {
            if (faces.isEmpty()) {
                runOnUiThread(() -> {
                    overlayView.setBoundingBox(null);
                    txtInstruction.setText("No face detected");
                });
            } else if (faces.size() > 1) {
                runOnUiThread(() -> txtInstruction.setText("Multiple faces detected! Detect one face only."));
            } else {
                Face face = faces.get(0);
                Rect boundingBox = face.getBoundingBox();
                runOnUiThread(() -> {
                    overlayView.setBoundingBox(boundingBox);
                    txtInstruction.setText("Face detected. Stay still.");
                });

                // Simple Liveness Check: Verify eyes are open or small movement detected
                if (face.getLeftEyeOpenProbability() != null && face.getLeftEyeOpenProbability() < 0.2) {
                    runOnUiThread(() -> txtInstruction.setText("Liveness Check: Don't close eyes."));
                    isProcessing = false;
                    imageProxy.close();
                    return;
                }

                // Verification logic
                float[] liveEmbedding = faceHelper.getFaceEmbedding(rotatedBitmap, boundingBox);
                double similarity = FaceVerificationHelper.calculateCosineSimilarity(storedFaceEmbedding, liveEmbedding);
                
                Log.d(TAG, "Similarity Score: " + similarity);

                if (similarity >= MATCH_THRESHOLD) {
                    handleVerificationSuccess();
                } else {
                    // We donut fail immediately, wait for better frame or timeout
                    isProcessing = false; 
                }
            }
            imageProxy.close();
            isProcessing = false;
        }).addOnFailureListener(e -> {
            imageProxy.close();
            isProcessing = false;
        });
    }

    private void handleVerificationSuccess() {
        if (isVerified) return;
        isVerified = true;
        if (timeoutTimer != null) timeoutTimer.cancel();

        runOnUiThread(() -> {
            txtStatus.setText("Face Verified ✅");
            txtStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
            txtInstruction.setText("Proceeding to attendance...");
            
            new android.os.Handler().postDelayed(() -> {
                Intent intent = new Intent(this, GetStudentCurrentLocationandSubmitAttendanceActivity.class);
                startActivity(intent);
                finish();
            }, 1500);
        });
    }

    private void handleVerificationFailure(String reason) {
        attemptsRemaining--;
        if (attemptsRemaining <= 0) {
            runOnUiThread(() -> {
                txtStatus.setText("Access Denied ❌");
                txtInstruction.setText("Max attempts reached. Returning home.");
                new android.os.Handler().postDelayed(this::finish, 2000);
            });
        } else {
            runOnUiThread(() -> {
                txtStatus.setText(reason);
                Toast.makeText(this, "Retry " + (3 - attemptsRemaining) + "/3. Try better lighting.", Toast.LENGTH_SHORT).show();
                isProcessing = false;
                isVerified = false;
                startTimeoutTimer(); // Reset timer for next attempt
            });
        }
    }

    private Bitmap toBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        buffer.rewind();
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission required for face verification", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (faceHelper != null) faceHelper.close();
        if (timeoutTimer != null) timeoutTimer.cancel();
    }
}
