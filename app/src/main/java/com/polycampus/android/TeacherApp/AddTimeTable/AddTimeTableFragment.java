package com.polycampus.android.TeacherApp.AddTimeTable;

import static android.app.Activity.RESULT_OK;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polycampus.android.R;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.polycampus.android.common.Urls;
import com.polycampus.android.common.VolleyMultipartRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class AddTimeTableFragment extends Fragment {


    ImageView noticeImage;
    EditText etTitle, etDate, etTime, etDescription;
    Button btnAddImage, btnAddNotice;
    private  int pick_image_request=789;
    Bitmap bitmap;
    Uri filepath;
    String title;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_time_table, container, false);
        noticeImage = view.findViewById(R.id.noticeImage);
        etTitle = view.findViewById(R.id.etTitle);
        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        etDescription = view.findViewById(R.id.etDescription);
        btnAddImage = view.findViewById(R.id.btnAddImage);
        btnAddNotice = view.findViewById(R.id.btnAddNotice);

        btnAddImage.setOnClickListener(v -> SelectUserProfileimage());


        etDate.setOnClickListener(v -> showDatePicker());


        etTime.setOnClickListener(v -> showTimePicker());


        btnAddNotice.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle("Confirm Publish")
                .setMessage("Are you sure you want to publish this notice to all students?")
                .setPositiveButton("Publish", (dialog, which) -> submitNotice())
                .setNegativeButton("Cancel", null)
                .show();
        });

        return view;
    }
    private void SelectUserProfileimage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image For Profil"),pick_image_request);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==pick_image_request && resultCode==RESULT_OK && data!=null){
            filepath=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),filepath);
                noticeImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void UserImageSaveTodatabase(Bitmap bitmap, String struername) {
        if (bitmap == null) {
            Log.d("UserImageSaveTodatabase", "Bitmap is null, skipping image upload.");
            return;
        }
//        String url="http://192.168.1.6:80/PolyCampusAPI/NoticImg.php";

        VolleyMultipartRequest volleyMultipartRequest =  new VolleyMultipartRequest(Request.Method.POST,
                Urls.addNoticeImage, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Notice Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                String errorMsg = error.getMessage();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    errorMsg = new String(error.networkResponse.data);
                }
                Log.e("UploadError", errorMsg);
                Toast.makeText(getActivity(), "Upload Error: " + errorMsg, Toast.LENGTH_LONG).show();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parms = new HashMap<>();
                parms.put("tags",struername ); // Adjusted to match PHP parameter name
                return parms;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String,VolleyMultipartRequest.DataPart> parms = new HashMap<>();
                long imagename = System.currentTimeMillis();
                parms.put("pic",new VolleyMultipartRequest.DataPart(imagename+".jpeg",getfiledatafromBitmap(bitmap)));

                return parms;

            }

        };
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);
    }

    private byte[] getfiledatafromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) ->
                        etDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (view, selectedHour, selectedMinute) ->
                        etTime.setText(selectedHour + ":" + String.format("%02d", selectedMinute)),
                hour, minute, false);
        timePickerDialog.show();
    }

    private void submitNotice() {
        title = etTitle.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
            etTitle.setError("Fill all fields");
            return;
        }

        AddNotice(title,date,time,description);
    }

    private void AddNotice(String title, String date, String time, String description) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String teacherName = sharedPreferences.getString("name", "Unknown");
        String username = sharedPreferences.getString("username", "Unknown");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String encoded_img = "";
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            encoded_img = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        params.put("Notic_img", encoded_img);
        params.put("image", encoded_img); // Column name from Screen 5
        params.put("title", title);
        params.put("date", date);
        params.put("time", time);
        params.put("description", description); // Match Screenshot 5 column
        params.put("dis", description);         // Match older backend versions
        
        // 🛡️ Global Lockdown: Tag notice with departmental branch
        String sessionBranch = sharedPreferences.getString("branch", "All");
        params.put("branch", sessionBranch);

        final android.app.ProgressDialog pd = new android.app.ProgressDialog(getActivity());
        pd.setMessage("Publishing Notice...");
        pd.show();

        client.post(Urls.addNoticeByTeacher, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pd.dismiss();
                try {
                    String success = response.getString("success");
                    if (success.equals("1")) {
                        if (bitmap != null) {
                            UserImageSaveTodatabase(bitmap, title);
                        } else {
                            Toast.makeText(getActivity(), "Notice Published (No Image)", Toast.LENGTH_SHORT).show();
                        }
                        clearAll();
                        
                        if (isAdded() && getActivity() != null) {
                            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                                .setTitle("Success")
                                .setMessage("Notice has been published successfully!")
                                .setPositiveButton("OK", null)
                                .setIcon(R.drawable.baseline_bolt_24)
                                .show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Draft Failed: " + response.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Response Format Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                pd.dismiss();
                // If server returns array, handle if necessary
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                pd.dismiss();
                // Handled if server returns raw string
                if (responseString.contains("1") || responseString.equalsIgnoreCase("success")) {
                    if (bitmap != null) UserImageSaveTodatabase(bitmap, title);
                    clearAll();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Server Error: " + (errorResponse != null ? errorResponse.toString() : "Connection Failed"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Server Error: " + responseString, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAll() {
        noticeImage.setImageResource(R.drawable.image_not_found);
        etTitle.setText("");
        etDate.setText("");
        etTime.setText("");
        etDescription.setText("");
    }
}
