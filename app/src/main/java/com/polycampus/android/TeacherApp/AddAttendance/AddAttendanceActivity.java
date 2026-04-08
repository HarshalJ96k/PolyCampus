package com.polycampus.android.TeacherApp.AddAttendance;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.polycampus.android.R;
import com.polycampus.android.TeacherApp.HomeTeacherActivity;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class AddAttendanceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spinner_select_branch,spinner_select_sem,spinner_select_subject;
    AppCompatButton btn_add_attendance;
    TextView tv_date,tv_time_from,tv_time_to;
    ProgressDialog progressDialog;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    ArrayAdapter<CharSequence> adapter, adapter1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendance);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        spinner_select_branch = findViewById(R.id.spinner_branch);
        spinner_select_sem = findViewById(R.id.spinner_teacher_semester);
        spinner_select_subject = findViewById(R.id.spinner_teacher_subject);
        tv_date = findViewById(R.id.tv_add_presenty_date);
        tv_time_from = findViewById(R.id.tv_add_presenty_time_from);
        tv_time_to = findViewById(R.id.tv_add_presenty_time_to);

        adapter = adapter.createFromResource(this,R.array.semester,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_select_sem.setAdapter(adapter);

        spinner_select_sem.setOnItemSelectedListener(this);

        btn_add_attendance = findViewById(R.id.btn_add_presenty_add_presenty);


        // on below line we are adding click listener for our pick date button
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        AddAttendanceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                tv_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        // on below line we are adding click
        // listener for our pick date button
        tv_time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting the
                // instance of our calendar.
                // Getting the current current time
                Date date = new Date();
                // set format in 12 hours
                SimpleDateFormat formatTime = new SimpleDateFormat("hh.mm aa");
                // hh = hours in 12hr format
                // mm = minutes
                // aa = am/pm
                // display time as per format
                String time = formatTime.format(
                        date); // changing the format of 'date'

                // display time as per format
                System.out.println(
                        "Current Time in AM/PM Format is : " + time);
                Toast.makeText(AddAttendanceActivity.this, ""+time, Toast.LENGTH_SHORT).show();
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddAttendanceActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.
                                tv_time_from.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();
            }
        });

        // on below line we are adding click
        // listener for our pick date button
        tv_time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting the
                // instance of our calendar.
                // Getting the current current time
                Date date = new Date();
                // set format in 12 hours
                SimpleDateFormat formatTime = new SimpleDateFormat("hh.mm aa");
                // hh = hours in 12hr format
                // mm = minutes
                // aa = am/pm
                // display time as per format
                String time = formatTime.format(
                        date); // changing the format of 'date'

                // display time as per format
                System.out.println(
                        "Current Time in AM/PM Format is : " + time);
                Toast.makeText(AddAttendanceActivity.this, ""+time, Toast.LENGTH_SHORT).show();
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddAttendanceActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.
                                tv_time_to.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();
            }
        });


        // 🛡️ Global Lockdown: Hide branch spinner for departmental staff
        String sessionBranch = preferences.getString("branch", "All").trim();
        if (!sessionBranch.equalsIgnoreCase("All")) {
            // Find and hide the Branch selection UI entirely if not an 'All' admin
            View branchContainer = findViewById(R.id.spinner_branch).getParent() instanceof View ? (View)findViewById(R.id.spinner_branch).getParent() : null;
            if (branchContainer != null) branchContainer.setVisibility(View.GONE);
            
            // Pre-select the branch for API
            // Note: We will use sessionBranch directly in addAttendance()
        }

        btn_add_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBranch = preferences.getString("branch", "All");
                
                if (tv_date.getText().toString().isEmpty()) {
                    tv_date.setError("Please Enter Date");
                } else if (tv_time_from.getText().toString().isEmpty()) {
                    tv_time_from.setError("Please Enter Start Time");
                } else if (TextUtils.isEmpty(tv_time_to.getText().toString())) {
                    tv_time_to.setError("Please Enter End Time");
                } else if (selectedBranch.equalsIgnoreCase("All") && spinner_select_branch.getSelectedItem().toString().equals("Select Your Branch")) {
                    ((TextView) spinner_select_branch.getSelectedView()).setError("Please Select Your Branch");
                } else if (spinner_select_sem.getSelectedItem().toString().equals("Select Your Sem")) {
                    ((TextView) spinner_select_sem.getSelectedView()).setError("Please Select Your Sem");
                } else if (spinner_select_subject.getSelectedItem().toString().equals("Select Your Sub")) {
                    ((TextView) spinner_select_subject.getSelectedView()).setError("Please Select Your Subject");
                } else {
                    progressDialog = new ProgressDialog(AddAttendanceActivity.this);
                    progressDialog.setTitle("Registering Attendance");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    addAttendance();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        parent.getItemAtPosition(position);

        if (position == 1) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(this, R.array.first_sem,
                            android.R.layout.simple_spinner_dropdown_item);

            spinner_select_subject.setAdapter(adapter);
        } else if (position == 2) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(this, R.array.second_sem,
                            android.R.layout.simple_spinner_dropdown_item);

            spinner_select_subject.setAdapter(adapter);
        } else if (position == 3) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(this, R.array.third_sem,
                            android.R.layout.simple_spinner_dropdown_item);

            spinner_select_subject.setAdapter(adapter);
        } else if (position == 4) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(this, R.array.fourth_sem,
                            android.R.layout.simple_spinner_dropdown_item);
            spinner_select_subject.setAdapter(adapter);
        } else if (position == 5) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(this, R.array.fifth_sem,
                            android.R.layout.simple_spinner_dropdown_item);

            spinner_select_subject.setAdapter(adapter);
        } else if (position == 6) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(this, R.array.sixth_sem,
                            android.R.layout.simple_spinner_dropdown_item);

            spinner_select_subject.setAdapter(adapter);
        }
//        else if (position == 7) {
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter
//                    .createFromResource(this, R.array.VII_Standard,
//                            android.R.layout.simple_spinner_dropdown_item);
//
//            spinner_student_subject.setAdapter(adapter);
//        } else if (position == 8) {
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter
//                    .createFromResource(this, R.array.VIII_Standard,
//                            android.R.layout.simple_spinner_dropdown_item);
//            spinner_student_subject.setAdapter(adapter);
//        } else if (position == 9) {
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter
//                    .createFromResource(this, R.array.IX_Standard,
//                            android.R.layout.simple_spinner_dropdown_item);
//
//            spinner_student_subject.setAdapter(adapter);
//        } else if (position == 10) {
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter
//                    .createFromResource(this, R.array.X_Standard,
//                            android.R.layout.simple_spinner_dropdown_item);
//            spinner_student_subject.setAdapter(adapter);
//        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    private void addAttendance() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        
        // Use Session Branch if Locked
        String sessionBranch = preferences.getString("branch", "All");
        String finalBranch = sessionBranch.equalsIgnoreCase("All") ? 
                            spinner_select_branch.getSelectedItem().toString() : 
                            sessionBranch;

        params.put("branch", finalBranch);
        params.put("sem",spinner_select_sem.getSelectedItem().toString());
        params.put("subject",spinner_select_subject.getSelectedItem().toString());
        params.put("date",tv_date.getText().toString());
        params.put("time_from",tv_time_from.getText().toString());
        params.put("time_to",tv_time_to.getText().toString());

        client.post(Urls.urlAddAttendance, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                if (progressDialog != null) progressDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                try {
                    String aa = response.getString("success");
                    if (aa.equals("1")) {
                        Intent intent = new Intent(AddAttendanceActivity.this, HomeTeacherActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AddAttendanceActivity.this, "Unable to Add Attendance: " + response.optString("message", ""), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddAttendanceActivity.this, "Response Parsing Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                if (responseString.contains("1") || responseString.equalsIgnoreCase("success")) {
                    Intent intent = new Intent(AddAttendanceActivity.this, HomeTeacherActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(AddAttendanceActivity.this, "Server Error: " + (errorResponse != null ? errorResponse.toString() : "Connection Failed"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(AddAttendanceActivity.this, "Error: " + responseString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(AddAttendanceActivity.this, "Server Error (Array)", Toast.LENGTH_LONG).show();
            }
        });
    }
}
