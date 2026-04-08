package com.polycampus.android.StudyMaterial;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.polycampus.android.R;

import java.io.OutputStream;

public class CeertificateDownlode extends AppCompatActivity {

    private View certificateLayout;
    private Button downloadButton;
    private TextView tvCertificateDate;



    private TextView studentName, studentDetails;


    private String name = "John Doe";
    private String year = "2nd";
    private String semester = "4th";
    private String course1 = "Computer Engineering";

    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceertificate_downlode);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String studentNameS = sharedPreferences.getString("Name", "Student Name");
        String year = sharedPreferences.getString("Sem", "Year");
        String course = sharedPreferences.getString("Branch", "Course Name");


        certificateLayout = findViewById(R.id.certificateLayout);
        downloadButton = findViewById(R.id.downloadButton);
        studentName = findViewById(R.id.studentName);
        studentDetails = findViewById(R.id.studentDetails);
        tvCertificateDate = findViewById(R.id.tvCertificateDate);

        // Set current date
        String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        tvCertificateDate.setText("DATE: " + currentDate);

        studentName.setText("This is to certify that, Mr. / Miss " + studentNameS + " is a bonafide student of GOVERNMENT POLYTECHNIC, MURTIZAPUR for the Academic Year 2023 - 2024.");
        studentDetails.setText("He / She is currently studying in " + year + " Semester of Diploma in " + course + " (Polytechnic Course).");

        downloadButton.setOnClickListener(v -> saveCertificate());
    }

    private void saveCertificate() {
        Bitmap bitmap = getCertificateBitmap(certificateLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToDownloads(bitmap);
            downloadButton.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Android version too low. Use API 29+ for proper saving.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getCertificateBitmap(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas(bitmap);


        canvas.drawColor(android.graphics.Color.WHITE);


        view.draw(canvas);

        return bitmap;
    }


    private void saveImageToDownloads(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "bonafide_certificate.png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Certificates");

        ContentResolver resolver = getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = resolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            Toast.makeText(this, "Certificate saved in Pictures/Certificates", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save certificate", Toast.LENGTH_SHORT).show();
        }
    }

}
