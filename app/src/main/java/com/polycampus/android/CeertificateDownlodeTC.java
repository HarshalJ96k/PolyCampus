package com.polycampus.android;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CeertificateDownlodeTC extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Button btnDownloadPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ceertificate_downlode_tc);

        btnDownloadPDF = findViewById(R.id.btn_download_pdf);
        populateFields();

        btnDownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { // For Android 9 and below
                    if (ContextCompat.checkSelfPermission(CeertificateDownlodeTC.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CeertificateDownlodeTC.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    } else {
                        generatePDF();
                    }
                } else {
                    generatePDF();
                }
            }
        });
    }

    private void populateFields() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String name = sharedPreferences.getString("Name", "Student Name");
        String year = sharedPreferences.getString("Sem", "Year");
        String course = sharedPreferences.getString("Branch", "Course Name");
        String dob = sharedPreferences.getString("DOB", "--/--/----"); // Assuming this exists

        // Date field
        TextView tvDate = findViewById(R.id.tvTcDate);
        String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        tvDate.setText("DATE: " + currentDate);

        // Name Row
        View rowName = findViewById(R.id.row_name);
        ((TextView)rowName.findViewById(R.id.tvLabel)).setText("Name of Candidate:");
        ((TextView)rowName.findViewById(R.id.tvValue)).setText(name);

        // Enrollment Row
        View rowEnrollment = findViewById(R.id.row_enrollment);
        ((TextView)rowEnrollment.findViewById(R.id.tvLabel)).setText("Enrollment Number:");
        ((TextView)rowEnrollment.findViewById(R.id.tvValue)).setText("2200000000"); // Mock or shared pref

        // Course Row
        View rowCourse = findViewById(R.id.row_course);
        ((TextView)rowCourse.findViewById(R.id.tvLabel)).setText("Course and Year:");
        ((TextView)rowCourse.findViewById(R.id.tvValue)).setText(course + " (" + year + ")");

        // DOB Row
        View rowDob = findViewById(R.id.row_dob);
        ((TextView)rowDob.findViewById(R.id.tvLabel)).setText("Date of Birth:");
        ((TextView)rowDob.findViewById(R.id.tvValue)).setText(dob);

        // Leaving Row
        View rowLeaving = findViewById(R.id.row_leaving);
        ((TextView)rowLeaving.findViewById(R.id.tvLabel)).setText("Leaving Institution On:");
        ((TextView)rowLeaving.findViewById(R.id.tvValue)).setText(currentDate);

        // Reason Row
        View rowReason = findViewById(R.id.row_reason);
        ((TextView)rowReason.findViewById(R.id.tvLabel)).setText("Reason for Leaving:");
        ((TextView)rowReason.findViewById(R.id.tvValue)).setText("Passed Final Year Diploma Exam");

        // Conduct Row
        View rowConduct = findViewById(R.id.row_conduct);
        ((TextView)rowConduct.findViewById(R.id.tvLabel)).setText("Conduct & Character:");
        ((TextView)rowConduct.findViewById(R.id.tvValue)).setText("Excellent");

        // Progress Row
        View rowProgress = findViewById(R.id.row_progress);
        ((TextView)rowProgress.findViewById(R.id.tvLabel)).setText("Academic Progress:");
        ((TextView)rowProgress.findViewById(R.id.tvValue)).setText("Satisfactory / Good");
    }

    private void generatePDF() {
        View view = findViewById(R.id.certificateLayout);

        // Define PDF page dimensions
        int width = view.getWidth();
        int height = view.getHeight();

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        view.draw(canvas);

        pdfDocument.finishPage(page);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePdfToDownloadsAndroid10(pdfDocument);
        } else {
            savePdfToDownloadsOldVersion(pdfDocument);
        }

        pdfDocument.close();
    }

    private void savePdfToDownloadsOldVersion(PdfDocument pdfDocument) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, "LeavingCertificate.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            fos.close();
            Toast.makeText(this, "PDF Saved in Downloads!\nPath: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openDownloadedFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePdfToDownloadsAndroid10(PdfDocument pdfDocument) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, "LeavingCertificate.pdf");
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

        if (uri != null) {
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream);
                    outputStream.close();
                    Toast.makeText(this, "PDF Saved in Downloads!", Toast.LENGTH_LONG).show();
                    openDownloadedFile(uri);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openDownloadedFile(File file) {
        Uri fileUri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent chooser = Intent.createChooser(intent, "Open PDF");
        startActivity(chooser);
    }

    private void openDownloadedFile(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent chooser = Intent.createChooser(intent, "Open PDF");
        startActivity(chooser);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePDF();
            } else {
                Toast.makeText(this, "Storage Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
