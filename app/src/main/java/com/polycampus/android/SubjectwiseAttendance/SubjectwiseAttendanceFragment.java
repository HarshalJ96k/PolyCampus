package com.polycampus.android.SubjectwiseAttendance;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class SubjectwiseAttendanceFragment extends Fragment {

    List<PojoClassMyAttendance> list;
    Spinner spinner_subjectwise_presenty;
    RecyclerView rv_view_attendance;
    TextView tv_no_records, tv_present_in_count, tv_present_ratio;
    ProgressBar pBar;
    MyAttendanceAdapter adapter;
    SharedPreferences preferences;
    int username_count;

    int checksubjects = 0;
    ArrayList<String> arraySubjectsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subjectwise_attendance, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        list = new ArrayList<>();
        rv_view_attendance = view.findViewById(R.id.rv_view_presenty);
        rv_view_attendance.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        spinner_subjectwise_presenty = view.findViewById(R.id.spinner_select_subject);
        tv_no_records = view.findViewById(R.id.tv_no_records);
        pBar = view.findViewById(R.id.pBar);
        tv_present_in_count = view.findViewById(R.id.tv_present_in_count);
        tv_present_ratio = view.findViewById(R.id.tv_present_ration);

        arraySubjectsList = new ArrayList<>();

        getSubjects();

        return view;
    }

    private void getSubjects() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("sem", preferences.getString("sem", ""));

        client.post(Urls.urlGetSujects, params, new JsonHttpResponseHandler() {

            public void onStart() {
                pBar.setVisibility(View.VISIBLE);
                super.onStart();
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getActivity() == null || !isAdded()) return;

                try {
                    pBar.setVisibility(View.GONE);

                    JSONArray jarry = response.getJSONArray("getSubjects");
                    arraySubjectsList.clear();
                    arraySubjectsList.add("Select Your Subject");
                    for (int i = 0; i < jarry.length(); i++) {
                        JSONObject jsonObject = jarry.getJSONObject(i);
                        String subject = jsonObject.getString("subject");
                        arraySubjectsList.add(subject);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                            arraySubjectsList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_subjectwise_presenty.setAdapter(adapter);

                    spinner_subjectwise_presenty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (checksubjects++ > 0 && position > 0) {
                                try {
                                    list.clear();
                                    getSubjectwisePresenty(arraySubjectsList.get(position));
                                } catch (Exception e) {
                                    if (getActivity() != null) {
                                        Toast.makeText(getActivity(), "" + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    Log.i("Error in Subject selection", e.toString());
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    pBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "could not connect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void getSubjectwisePresenty(String subject) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username", preferences.getString("username", ""));
        params.put("subject", subject);

        client.post(Urls.urlGetSubjectwiseAttendance, params, new JsonHttpResponseHandler() {

            public void onStart() {
                pBar.setVisibility(View.VISIBLE);
                tv_no_records.setVisibility(View.GONE);
                super.onStart();
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getActivity() == null || !isAdded()) return;
                try {
                    pBar.setVisibility(View.GONE);

                    JSONArray jarry = response.getJSONArray("getMyAttendance");
                    list.clear();
                    for (int i = 0; i < jarry.length(); i++) {
                        JSONObject jsonObject = jarry.getJSONObject(i);
                        String date = jsonObject.getString("date");
                        String subject_name = jsonObject.getString("subject_name");
                        String presenty = jsonObject.getString("presenty");

                        list.add(new PojoClassMyAttendance(date, subject_name, presenty));
                    }

                    if (list.isEmpty()) {
                        tv_no_records.setVisibility(View.VISIBLE);
                    } else {
                        tv_no_records.setVisibility(View.GONE);
                    }

                    adapter = new MyAttendanceAdapter(list, getActivity());
                    rv_view_attendance.setAdapter(adapter);

                    getSubjectwisePresentyCount(subject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    pBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "could not connect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getSubjectwisePresentyCount(String subject) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("subject", subject);
        params.put("username", preferences.getString("username", ""));

        client.post(Urls.urlGetSubjectwisePresentyCount, params, new JsonHttpResponseHandler() {

            public void onStart() {
                pBar.setVisibility(View.VISIBLE);
                super.onStart();
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getActivity() == null || !isAdded()) return;
                try {
                    pBar.setVisibility(View.GONE);

                    JSONArray jarry = response.getJSONArray("getSubjectwisePresentyCount");
                    if (jarry.length() == 0 || jarry.isNull(0)) {
                        tv_present_in_count.setText("0");
                        tv_present_ratio.setText("0%");
                        return;
                    }
                    
                    JSONObject jsonObject = jarry.getJSONObject(0);
                    username_count = jsonObject.getInt("username_count");
                    tv_present_in_count.setText(String.valueOf(username_count));
                    
                    // Assuming daily ratio base is 30 as per original code
                    float ratio = (username_count / 30f) * 100;
                    tv_present_ratio.setText(String.format(Locale.getDefault(), "%.1f%%", ratio));

                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_present_in_count.setText("0");
                    tv_present_ratio.setText("0%");
                }
            }

            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    pBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
