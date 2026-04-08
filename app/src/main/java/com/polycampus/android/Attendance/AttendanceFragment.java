package com.polycampus.android.Attendance;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.polycampus.android.R;
import com.polycampus.android.TeacherApp.AddStudyMaterial.AddStudyMaterialActivity;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AttendanceFragment extends Fragment {

    List<POJOAddAttendance> list;
    ListView lv_add_attendance;
    TextView tv_no_records;
    ProgressBar progress;
    AddAttendanceAdapter adapter;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String presentydoneornot;
    String subject, date1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_attendance, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        list = new ArrayList<POJOAddAttendance>();
        lv_add_attendance = (ListView) view.findViewById(R.id.lv_add_presenty);
        tv_no_records = (TextView) view.findViewById(R.id.tv_no_records);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        tv_no_records = (TextView) view.findViewById(R.id.tv_no_records);


        getPresentPending();
        return view;
    }


    private void getPresentPending() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("branch", preferences.getString("branch", ""));
        params.put("sem", preferences.getString("sem", ""));

        client.post(Urls.urlGetPendingAttendance, params, new JsonHttpResponseHandler() {

            public void onStart() {
                progress.setVisibility(View.VISIBLE);
                super.onStart();
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getActivity() == null || !isAdded()) return;
                try {
                    progress.setVisibility(View.GONE);

                    JSONArray jarry = response.getJSONArray("getPendingAttendance");
                    if (jarry.isNull(0))
                    {
                        tv_no_records.setVisibility(View.VISIBLE);
                        tv_no_records.setText("No Lecture Attendance is Going On");
                    }
//                    if (jarry.isNull(0)) {
//                        tv_no_records.setVisibility(View.VISIBLE);
//                        tv_no_records.setText("No Attendance to Submit");
//                    }
                    for (int i = 0; i < jarry.length(); i++) {
                        JSONObject jsonObject = jarry.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String branch = jsonObject.getString("branch");
                        String sem = jsonObject.getString("sem");
                        subject = jsonObject.getString("subject");
                        date1 = jsonObject.getString("date");
                        String time_from = jsonObject.getString("time_from");
                        String time_to = jsonObject.getString("time_to");

                        list.add(new POJOAddAttendance(id, branch, sem, subject, date1, time_from, time_to));
                    }
                    if (list.size() > 0) {
                        checkAttendanceDoneOrNot(preferences.getString("username", ""), date1, subject);
                    }

//                    checkAttendanceDoneOrNot  = new CheckAttendanceDoneOrNot() {
//                        @Override
//                        public void checkAttendanceStatus(String roll_no, String date, String subject) {
//
//                        }
//                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "could not connect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkAttendanceDoneOrNot(String subject, String date1, String s) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username", preferences.getString("username", ""));
        params.put("date", date1);
        params.put("subject", s);

        client.post(Urls.checkAttendanceDoneOrNot, params, new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getActivity() == null || !isAdded()) return;
                try {
                    progress.setVisibility(View.GONE);
                    JSONArray jarry = response.getJSONArray("checkAttendanceDoneOrNot");
                    int len = jarry.length();
                    // Toast.makeText(getActivity(), "" + len, Toast.LENGTH_SHORT).show(); // Removed debugging toast
                    if (jarry.isNull(0)) {
                        presentydoneornot = "Not Done";
                        adapter = new AddAttendanceAdapter(list, (AppCompatActivity) getActivity(), tv_no_records, presentydoneornot);
                        lv_add_attendance.setAdapter(adapter);

                    } else {
                        presentydoneornot = "Done";
                        adapter = new AddAttendanceAdapter(list, (AppCompatActivity) getActivity(), tv_no_records, presentydoneornot);
                        lv_add_attendance.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

}
