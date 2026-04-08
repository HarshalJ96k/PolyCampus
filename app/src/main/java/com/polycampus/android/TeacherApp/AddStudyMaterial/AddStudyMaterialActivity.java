package com.polycampus.android.TeacherApp.AddStudyMaterial;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.polycampus.android.R;
import com.polycampus.android.TeacherApp.HomeTeacherActivity;
import com.polycampus.android.common.CommonMethods;
import com.polycampus.android.common.Urls;
import com.polycampus.android.common.VolleyMultipartRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class AddStudyMaterialActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    ArrayAdapter<CharSequence> adapter;
    Spinner spinner_branch, spinner_sem;
    EditText et_study_material_title, et_study_material_description;

    TextView tv_select_pdf;
    Button btn_add_study_material;
    ProgressDialog progressDialog;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private int PICK_FILE_REQUEST = 2;
    private String selectedFilePath;

    private Uri filePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_study_material);
        preferences = PreferenceManager.getDefaultSharedPreferences(AddStudyMaterialActivity.this);
        editor = preferences.edit();

        spinner_branch = (Spinner) findViewById(R.id.spinner_branch);
        spinner_sem = (Spinner) findViewById(R.id.spinnerstandard);
        et_study_material_title = findViewById(R.id.et_study_material_title);
        et_study_material_description = findViewById(R.id.et_study_material_description);
        tv_select_pdf = findViewById(R.id.tv_study_material_select_pdf);
        btn_add_study_material = findViewById(R.id.btn_add_study_material);

        String sessionBranch = preferences.getString("branch", "").trim();
        if (sessionBranch.isEmpty()) {
            Toast.makeText(this, "Critical Profile Error: Branch missing", Toast.LENGTH_LONG).show();
            finish();
        }

        // 🛡️ Data Integrity: Force departmental context for Study Material
        spinner_branch.setVisibility(View.GONE);
        findViewById(R.id.tv_label_branch).setVisibility(View.GONE); // Hide label if exists

        adapter = adapter.createFromResource(AddStudyMaterialActivity.this, R.array.semester,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sem.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(AddStudyMaterialActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }


        tv_select_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btn_add_study_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_study_material_title.getText().toString().isEmpty()) {
                    et_study_material_title.setError("Enter Your Study Material Title");
                } else if (et_study_material_description.getText().toString().isEmpty()) {
                    et_study_material_description.setError("Enter Your Study Material Description");
                } else {
                    progressDialog = new ProgressDialog(AddStudyMaterialActivity.this);
                    progressDialog.setTitle("Adding Study Material");
                    progressDialog.setCancelable(true);
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    addStudyMaterial();
                }
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_FILE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

//    @

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            if (filePath != null) {
                try {
                    selectedFilePath = CommonMethods.getPath(AddStudyMaterialActivity.this, filePath);
                    tv_select_pdf.setText("File Attached");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error selecting file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void addStudyMaterial() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String sessionBranch = preferences.getString("branch", "").trim();
        params.put("branch", sessionBranch);
        params.put("sem", spinner_sem.getSelectedItem().toString());
        params.put("title", et_study_material_title.getText().toString());
        params.put("description", et_study_material_description.getText().toString());

        client.post(Urls.urlAddStudyMaterial, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    String aa = response.getString("success");
                    if (aa.equals("1")) {
                        uploadMultipart();
                    } else {
                        Toast.makeText(AddStudyMaterialActivity.this, "Please Select all fields or correct title", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddStudyMaterialActivity.this, "Response Format Error", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (responseString.contains("1") || responseString.equalsIgnoreCase("success")) {
                    try {
                        uploadMultipart();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(AddStudyMaterialActivity.this, "Could Not Connect: " + (errorResponse != null ? errorResponse.toString() : "Server Error"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(AddStudyMaterialActivity.this, "Server Error: " + responseString, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadMultipart() throws IOException {
        //getting name for the pdf
        Random random = new Random();
        String name = String.format("%04d", random.nextInt(1000000));

        //getting the actual path of the pdf
        String absolutePath = FileUtil.from(AddStudyMaterialActivity.this, filePath);

        if (absolutePath == null) {
            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                // Modern 4.x API - Handles Android 12 (S+) PendingIntent flags internally
                new MultipartUploadRequest(this, Urls.urlAddStudyMaterialDoc)
                        .setMethod("POST")
                        .addFileToUpload(absolutePath, "pdf") //Adding file
                        .addParameter("name", name) //Adding text parameter to the request
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

                Toast.makeText(this, "Study Material Upload Started", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddStudyMaterialActivity.this, HomeTeacherActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception exc) {
                Log.e("UploadError", "Error during multipart setup", exc);
                Toast.makeText(this, "Upload Error: " + exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static class FileUtil {
        private static final int EOF = -1;
        private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

        private FileUtil() {

        }

        public static String from(Context context, Uri uri) throws IOException {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String fileName = getFileName(context, uri);
            String[] splitName = splitFileName(fileName);
            File tempFile = File.createTempFile(splitName[0], splitName[1]);
            tempFile = rename(tempFile, fileName);
            tempFile.deleteOnExit();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (inputStream != null) {
                copy(inputStream, out);
                inputStream.close();
            }

            if (out != null) {
                out.close();
            }
            return tempFile.toString();
        }

        private static String[] splitFileName(String fileName) {
            String name = fileName;
            String extension = "";
            int i = fileName.lastIndexOf(".");
            if (i != -1) {
                name = fileName.substring(0, i);
                extension = fileName.substring(i);
            }

            return new String[]{name, extension};
        }

        private static String getFileName(Context context, Uri uri) {
            String result = null;
            if (uri.getScheme().equals("content")) {
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf(File.separator);
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            return result;
        }

        private static File rename(File file, String newName) {
            File newFile = new File(file.getParent(), newName);
            if (!newFile.equals(file)) {
                if (newFile.exists() && newFile.delete()) {
                    Log.d("FileUtil", "Delete old " + newName + " file");
                }
                if (file.renameTo(newFile)) {
                    Log.d("FileUtil", "Rename file to " + newName);
                }
            }
            return newFile;
        }

        private static long copy(InputStream input, OutputStream output) throws IOException {
            long count = 0;
            int n;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while (EOF != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}



//
//    private void uploadMultipart() throws IOException {
//        Random random = new Random();
//        String name = String.format("%04d", random.nextInt(1000000));
//
//        if (filePath == null) {
//            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        File file = new File(FileUtil.from(this, filePath));
//
//        if (!file.exists()) {
//            Toast.makeText(this, "File error", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            String uploadId = UUID.randomUUID().toString();
//
//            new MultipartUploadRequest(this, uploadId, Urls.urlAddStudyMaterialDoc)
//                    .addFileToUpload(file.getAbsolutePath(), "pdf")
//                    .addParameter("name", name)
//                    .setNotificationConfig(
//                            new UploadNotificationConfig()
//                                    .setTitle("Uploading Study Material")
//                                    .setRingToneEnabled(false)
//                    )
//                    .setMaxRetries(2)
//                    .startUpload();
//        } catch (Exception exc) {
//            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {}
//
//    public static class FileUtil {
//        private static final int EOF = -1;
//        private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
//
//        private FileUtil() {}
//
//        public static String from(Context context, Uri uri) throws IOException {
//            InputStream inputStream = context.getContentResolver().openInputStream(uri);
//            String fileName = getFileName(context, uri);
//            String[] splitName = splitFileName(fileName);
//            File tempFile = File.createTempFile(splitName[0], splitName[1]);
//            tempFile = rename(tempFile, fileName);
//            tempFile.deleteOnExit();
//
//            try (OutputStream out = new FileOutputStream(tempFile)) {
//                if (inputStream != null) {
//                    copy(inputStream, out);
//                    inputStream.close();
//                }
//            }
//
//            return tempFile.getAbsolutePath();
//        }
//
//        private static String[] splitFileName(String fileName) {
//            String name = fileName;
//            String extension = "";
//            int i = fileName.lastIndexOf(".");
//            if (i != -1) {
//                name = fileName.substring(0, i);
//                extension = fileName.substring(i);
//            }
//            return new String[]{name, extension};
//        }
//
//        private static String getFileName(Context context, Uri uri) {
//            String result = null;
//            if (uri.getScheme().equals("content")) {
//                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
//                    if (cursor != null && cursor.moveToFirst()) {
//                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                    }
//                }
//            }
//            if (result == null) {
//                result = uri.getPath();
//                int cut = result.lastIndexOf(File.separator);
//                if (cut != -1) {
//                    result = result.substring(cut + 1);
//                }
//            }
//            return result;
//        }
//
//        private static File rename(File file, String newName) {
//            File newFile = new File(file.getParent(), newName);
//            if (!newFile.equals(file)) {
//                if (newFile.exists() && newFile.delete()) {
//                    Log.d("FileUtil", "Deleted old file");
//                }
//                if (file.renameTo(newFile)) {
//                    Log.d("FileUtil", "Renamed file");
//                }
//            }
//            return newFile;
//        }
//
//        private static long copy(InputStream input, OutputStream output) throws IOException {
//            long count = 0;
//            int n;
//            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
//            while (EOF != (n = input.read(buffer))) {
//                output.write(buffer, 0, n);
//                count += n;
//            }
//            return count;
//        }
//    }

//    private void uploadMultipart() throws IOException {
//        //getting name for the pdf
//
//        Random random = new Random();
//        String name = String.format("%04d", random.nextInt(1000000));
//
//        if (filePath == null) {
//            return;
//        }
//        //getting the actual path of the pdf
//        File path = new File(FileUtil.from(AddStudyMaterialActivity.this, filePath));
//
//        if (path == null) {
//            Toast.makeText(AddStudyMaterialActivity.this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
//        } else {
//            //Uploading code
//            try {
//                String uploadId = UUID.randomUUID().toString();
//
//                //Creating a multi part request
//                new MultipartUploadRequest(AddStudyMaterialActivity.this, uploadId, Urls.urlAddStudyMaterialDoc)
//                        .addFileToUpload(String.valueOf(path), "pdf") //Adding file
//                        .addParameter("name", name) //Adding text parameter to the request
//                        .setNotificationConfig(new UploadNotificationConfig())
//                        .setMaxRetries(2)
//                        .startUpload(); //Starting the upload
//            } catch (Exception exc) {
//                Toast.makeText(AddStudyMaterialActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//
//
//    public static class FileUtil {
//        private static final int EOF = -1;
//        private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
//
//        private FileUtil() {
//
//        }
//
//        public static String from(Context context, Uri uri) throws IOException {
//            InputStream inputStream = context.getContentResolver().openInputStream(uri);
//            String fileName = getFileName(context, uri);
//            String[] splitName = splitFileName(fileName);
//            File tempFile = File.createTempFile(splitName[0], splitName[1]);
//            tempFile = rename(tempFile, fileName);
//            tempFile.deleteOnExit();
//            FileOutputStream out = null;
//            try {
//                out = new FileOutputStream(tempFile);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            if (inputStream != null) {
//                copy(inputStream, out);
//                inputStream.close();
//            }
//
//            if (out != null) {
//                out.close();
//            }
//            return tempFile.toString();
//        }
//
//        private static String[] splitFileName(String fileName) {
//            String name = fileName;
//            String extension = "";
//            int i = fileName.lastIndexOf(".");
//            if (i != -1) {
//                name = fileName.substring(0, i);
//                extension = fileName.substring(i);
//            }
//
//            return new String[]{name, extension};
//        }
//
//        private static String getFileName(Context context, Uri uri) {
//            String result = null;
//            if (uri.getScheme().equals("content")) {
//                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//                try {
//                    if (cursor != null && cursor.moveToFirst()) {
//                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//            }
//            if (result == null) {
//                result = uri.getPath();
//                int cut = result.lastIndexOf(File.separator);
//                if (cut != -1) {
//                    result = result.substring(cut + 1);
//                }
//            }
//            return result;
//        }
//
//        private static File rename(File file, String newName) {
//            File newFile = new File(file.getParent(), newName);
//            if (!newFile.equals(file)) {
//                if (newFile.exists() && newFile.delete()) {
//                    Log.d("FileUtil", "Delete old " + newName + " file");
//                }
//                if (file.renameTo(newFile)) {
//                    Log.d("FileUtil", "Rename file to " + newName);
//                }
//            }
//            return newFile;
//        }
//
//        private static long copy(InputStream input, OutputStream output) throws IOException {
//            long count = 0;
//            int n;
//            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
//            while (EOF != (n = input.read(buffer))) {
//                output.write(buffer, 0, n);
//                count += n;
//            }
//            return count;
//        }
//    }
}
