package com.polycampus.android.AdminApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ManageTeacherFragment extends Fragment {

    ListView lvManageTeacher;
    ProgressBar progressBar;
    SearchView svManageTeacher;
    ExtendedFloatingActionButton fabAddTeacher;
    List<POJOAllTeacher> teacherList;
    TeacherAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_teacher, container, false);

        lvManageTeacher = view.findViewById(R.id.lv_manage_teacher);
        progressBar = view.findViewById(R.id.progress);
        svManageTeacher = view.findViewById(R.id.sv_manage_teacher);
        fabAddTeacher = view.findViewById(R.id.fab_add_teacher);
        
        teacherList = new ArrayList<>();

        fabAddTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddTeacherActivity.class);
            startActivity(intent);
        });

        svManageTeacher.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { filter(query); return false; }

            @Override
            public boolean onQueryTextChange(String query) { filter(query); return false; }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllTeachers();
    }

    private void getAllTeachers() {
        progressBar.setVisibility(View.VISIBLE);
        teacherList.clear();
        
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        
        // 🛡️ Absolute Data Isolation: HODs strictly see their own department teachers
        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sessionBranch = prefs.getString("branch", "").trim();
        params.put("branch", sessionBranch);
        
        client.post(Urls.urlGetAllTeacher, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    // API returns { "getAllTeacher": [...] }
                    JSONArray jsonArray = response.optJSONArray("getAllTeacher");
                    if (jsonArray == null) jsonArray = response.optJSONArray("data");
                    if (jsonArray == null) jsonArray = response.optJSONArray("teachers");
                    
                    if (jsonArray == null || jsonArray.length() == 0) {
                        showNoRecords(true);
                    } else {
                        showNoRecords(false);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            teacherList.add(new POJOAllTeacher(
                                    jsonObject.optString("id", ""),
                                    jsonObject.optString("image", "profileimage.png"),
                                    jsonObject.optString("name", "N/A"),
                                    jsonObject.optString("mobile_no", "N/A"),
                                    jsonObject.optString("email_id", jsonObject.optString("email", "N/A")),
                                    "",   // gender not in API response
                                    "",   // address not in API response
                                    jsonObject.optString("branch", jsonObject.optString("department", "N/A")),
                                    jsonObject.optString("subjects", "N/A"),
                                    jsonObject.optString("date_of_joining", "N/A"),
                                    jsonObject.optString("username", "N/A"),
                                    ""    // never expose password
                            ));
                        }
                        adapter = new TeacherAdapter(teacherList, getActivity());
                        lvManageTeacher.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showNoRecords(true);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progressBar.setVisibility(View.GONE);
                showNoRecords(false);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        teacherList.add(new POJOAllTeacher(
                                jsonObject.optString("id", ""),
                                jsonObject.optString("image", "profileimage.png"),
                                jsonObject.optString("name", "N/A"),
                                jsonObject.optString("mobile_no", "N/A"),
                                jsonObject.optString("email_id", jsonObject.optString("email", "N/A")),
                                "",   // gender
                                "",   // address
                                jsonObject.optString("branch", jsonObject.optString("department", "N/A")),
                                jsonObject.optString("subjects", "N/A"),
                                jsonObject.optString("date_of_joining", "N/A"),
                                jsonObject.optString("username", "N/A"),
                                ""    // password
                        ));
                    }
                    adapter = new TeacherAdapter(teacherList, getActivity());
                    lvManageTeacher.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressBar.setVisibility(View.GONE);
                if (isAdded() && getActivity() != null) {
                    Toast.makeText(getActivity(), "Admin Server Error: " + (errorResponse != null ? errorResponse.toString() : "Connection Failed"), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showNoRecords(boolean show) {
        if (getView() != null) {
            View noRecords = getView().findViewById(R.id.no_records_layout);
            if (noRecords != null) noRecords.setVisibility(show ? View.VISIBLE : View.GONE);
            lvManageTeacher.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void filter(String query) {
        if (teacherList == null) return;
        List<POJOAllTeacher> filteredList = new ArrayList<>();
        for (POJOAllTeacher teacher : teacherList) {
            if (teacher.getName().toLowerCase().contains(query.toLowerCase()) ||
                teacher.getSubjects().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(teacher);
            }
        }
        if (adapter != null) {
            adapter = new TeacherAdapter(filteredList, getActivity());
            lvManageTeacher.setAdapter(adapter);
        }
    }
}
