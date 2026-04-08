package com.polycampus.android.TeacherApp.AddAttendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ViewCurrentAttendanceFragment extends Fragment {

    List<POJOViewCurrentAttendance> list;
    ListView lv_view_attendance;
    TextView tv_no_records;
    ProgressBar progress;
    ViewCurrentAttendanceAdapter adapter;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String presentydoneornot;
    String subject,date1;

    FloatingActionButton fab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_current_attendance, container, false);
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor=preferences.edit();

        list = new ArrayList<POJOViewCurrentAttendance>();
        lv_view_attendance = (ListView) view.findViewById(R.id.lv_all_branches);
        tv_no_records = (TextView) view.findViewById(R.id.tv_no_records);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);
        tv_no_records = (TextView) view.findViewById(R.id.tv_no_records);
        fab = view.findViewById(R.id.fab1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getActivity(), AddAttendanceActivity.class);
                startActivity(intent1);
            }
        });

        getPresentPending();

        return view;
    }

    private void getPresentPending() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("branch",preferences.getString("branch",""));
        params.put("sem",preferences.getString("sem",""));

        client.post(Urls.urlGetPendingAttendance, params, new JsonHttpResponseHandler(){

            public void onStart()
            {
                progress.setVisibility(View.VISIBLE);
                super.onStart();
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    progress.setVisibility(View.GONE);

                    JSONArray jarry = response.getJSONArray("getPendingAttendance");
                    if (jarry.isNull(0))
                    {
                        tv_no_records.setVisibility(View.VISIBLE);
                        tv_no_records.setText("No Lecture Attendance is Going On");
                    }

                    for (int i = 0 ; i < jarry.length();i++)
                    {
                        JSONObject jsonObject = jarry.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String branch = jsonObject.getString("branch");
                        String sem = jsonObject.getString("sem");
                        subject = jsonObject.getString("subject");
                        date1 = jsonObject.getString("date");
                        String time_from = jsonObject.getString("time_from");
                        String time_to = jsonObject.getString("time_to");

                        list.add(new POJOViewCurrentAttendance(id,branch,sem,subject,date1,time_from,time_to));
                    }

                    adapter = new ViewCurrentAttendanceAdapter(list, (AppCompatActivity) getActivity(),tv_no_records);
                    lv_view_attendance.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
            {
                progress.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "could not connect", Toast.LENGTH_LONG).show();

            }

        });
    }
}
