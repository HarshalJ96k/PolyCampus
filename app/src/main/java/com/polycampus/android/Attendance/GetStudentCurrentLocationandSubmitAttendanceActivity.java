package com.polycampus.android.Attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.polycampus.android.HomeActivity;
import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GetStudentCurrentLocationandSubmitAttendanceActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_student_current_locationand_submit_attendance);
        setTitle("Live Location");

        preferences = PreferenceManager.getDefaultSharedPreferences(GetStudentCurrentLocationandSubmitAttendanceActivity.this);
        editor = preferences.edit();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Toast.makeText(this, "Your Current Location Fetch Successfully", Toast.LENGTH_SHORT).show();
                addAttendance(latitude,longitude);

            } else {
                Toast.makeText(GetStudentCurrentLocationandSubmitAttendanceActivity.this, "Unable to get location. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addAttendance(double latitude, double longitude) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username",preferences.getString("username",""));
        params.put("date",preferences.getString("attendance_date",""));
        params.put("subject",preferences.getString("attendance_subject",""));
        params.put("presenty_status",preferences.getString("attendance_presenty_status",""));
        params.put("latitude",latitude);
        params.put("longitude",longitude);

        client.post(Urls.urlAddPendingAttendance, params, new JsonHttpResponseHandler(){

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    String aa = response.getString("success");

                    if (aa.equals("1")) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(GetStudentCurrentLocationandSubmitAttendanceActivity.this);
                        ad.setTitle(""+preferences.getString("name","")+"  "+preferences.getString("enrollment_no",""));
                        ad.setMessage("Your Attendance with your current location added successfully");
                        ad.setPositiveButton("Thank You", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(new Intent(GetStudentCurrentLocationandSubmitAttendanceActivity.this, HomeActivity.class));
                                startActivity(intent);
                            }
                        }).create().show();

                    } else {
                        Toast.makeText(GetStudentCurrentLocationandSubmitAttendanceActivity.this, "Already Presenty Done", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
            {
                Toast.makeText(GetStudentCurrentLocationandSubmitAttendanceActivity.this, "could not connect", Toast.LENGTH_LONG).show();

            }

        });
    }

}
