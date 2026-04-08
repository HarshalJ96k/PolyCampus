package com.polycampus.android.TeacherApp.SubjectwiseAttendance;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SubjectwiseAttendanceFragment extends Fragment {

    TextView tv_date;
    TextInputEditText tie_enter_subject_name;

    Button btnViewSubjectwiseAttendance;

    List<PojoClassViewStudentDatewisePresenty> list;
    ListView lv_view_datewise_attendence;
    TextView tv_no_records;
    ProgressBar pBar;
    ViewStudentDatewisePresentyAdapter adapter;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_subjectwise_attendance2, container, false);

        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor=preferences.edit();

        tv_date = view.findViewById(R.id.tv_add_presenty_date);
        tie_enter_subject_name = view.findViewById(R.id.tie_enter_subject_name);
        btnViewSubjectwiseAttendance = view.findViewById(R.id.btnViewSubjectwiseAttendance);
        list = new ArrayList<PojoClassViewStudentDatewisePresenty>();
        lv_view_datewise_attendence = view.findViewById(R.id.lv_view_student_presenty_list);
        tv_no_records = view.findViewById(R.id.tv_no_record);
        pBar = view.findViewById(R.id.progress);

        tv_no_records.setVisibility(View.VISIBLE);
        tv_no_records.setText("Select Date First");

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
                        getActivity(),
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

        btnViewSubjectwiseAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tie_enter_subject_name.getText().toString())) {
                    tie_enter_subject_name.setError("Please Enter Your Subject Name");
                } else if (tv_date.getText().toString().equals("Select Date")) {
                    Toast.makeText(getActivity(), "Select Date", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getDatewiseStudentPresenty(tv_date.getText().toString(),tie_enter_subject_name.getText().toString());
                }
            }
        });
        return view;
    }

    private void getDatewiseStudentPresenty(String date, String subject_name) {

        // Clear previous data before making a new request
        list.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        pBar.setVisibility(View.VISIBLE); // Show progress bar before request

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("date",date);
        params.put("subject_name",subject_name);

        client.post(Urls.urlGetStudentDatewiseAttendance, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pBar.setVisibility(View.GONE);
                try {
                    JSONArray jarry = response.optJSONArray("getStudentDatewiseAttendance");
                    if (jarry == null || jarry.length() == 0) {
                        tv_no_records.setVisibility(View.VISIBLE);
                        tv_no_records.setText("No student present for this date/subject");
                        lv_view_datewise_attendence.setVisibility(View.GONE);
                    } else {
                        tv_no_records.setVisibility(View.GONE);
                        lv_view_datewise_attendence.setVisibility(View.VISIBLE);
                        for (int i = 0 ; i < jarry.length(); i++) {
                            JSONObject jsonObject = jarry.getJSONObject(i);
                            list.add(new PojoClassViewStudentDatewisePresenty(
                                    jsonObject.optString("id", ""),
                                    jsonObject.optString("username", ""),
                                    jsonObject.optString("student_name", ""),
                                    jsonObject.optString("subject_name", ""),
                                    jsonObject.optString("presenty", "")
                            ));
                        }
                        adapter = new ViewStudentDatewisePresentyAdapter(list, (AppCompatActivity) getActivity(), tv_no_records);
                        lv_view_datewise_attendence.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_no_records.setVisibility(View.VISIBLE);
                    tv_no_records.setText("Error parsing response");
                    lv_view_datewise_attendence.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                pBar.setVisibility(View.GONE);
                // Handle if direct array response is expected, though not in this specific API call.
                // For now, we can treat it as an error or empty data if not handled.
                tv_no_records.setVisibility(View.VISIBLE);
                tv_no_records.setText("Unexpected array response format.");
                lv_view_datewise_attendence.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                pBar.setVisibility(View.GONE);
                // Handle if direct string response is expected, though not in this specific API call.
                tv_no_records.setVisibility(View.VISIBLE);
                tv_no_records.setText("Unexpected string response format.");
                lv_view_datewise_attendence.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                pBar.setVisibility(View.GONE);
                tv_no_records.setVisibility(View.VISIBLE);
                tv_no_records.setText("Server Error: " + (errorResponse != null ? errorResponse.toString() : "Unknown"));
                lv_view_datewise_attendence.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pBar.setVisibility(View.GONE);
                tv_no_records.setVisibility(View.VISIBLE);
                tv_no_records.setText("Error: " + (responseString != null ? responseString : "Unknown error"));
                lv_view_datewise_attendence.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + responseString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                pBar.setVisibility(View.GONE);
                tv_no_records.setVisibility(View.VISIBLE);
                tv_no_records.setText("Server Error: " + (errorResponse != null ? errorResponse.toString() : "Unknown"));
                lv_view_datewise_attendence.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

    }
}
