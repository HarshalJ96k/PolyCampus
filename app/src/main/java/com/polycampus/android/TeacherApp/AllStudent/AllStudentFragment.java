package com.polycampus.android.TeacherApp.AllStudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
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

public class AllStudentFragment extends Fragment {


    TextView tv_no_student_available;
    ListView lv_all_student;
    ProgressBar progressBar;
    List<POJOAllStudent> pojoAllStudents;
    AllStudentAdapter adapterClass;

    SearchView sv_my_student;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_student, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        tv_no_student_available = view.findViewById(R.id.tv_no_student_available);
        lv_all_student = view.findViewById(R.id.lv_all_student);
        progressBar = view.findViewById(R.id.progress);
        pojoAllStudents = new ArrayList<>();
        sv_my_student = view.findViewById(R.id.sv_all_student);


        sv_my_student.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStudent(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchStudent(query);
                return false;
            }
        });

        view.findViewById(R.id.fab_add_student).setVisibility(View.GONE);

        lv_all_student.setOnItemClickListener((parent, view1, position, id) -> {
            if (adapterClass == null) return;
            POJOAllStudent selectedStudent = (POJOAllStudent) adapterClass.getItem(position);
            if (selectedStudent == null) return;
            Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
            intent.putExtra("student_data", selectedStudent);
            startActivity(intent);
        });

        return view;
    }

    private void searchStudent(String query) {
        List<POJOAllStudent> temppojoclass = new ArrayList<>();
        temppojoclass.clear();

        for (POJOAllStudent pojo : pojoAllStudents) {
            if (pojo.getName().toUpperCase().contains(query.toUpperCase()) ||
                    pojo.getGender().toUpperCase().contains(query.toUpperCase()) ||
                    pojo.getBranch().toUpperCase().contains(query.toUpperCase()) ||
                    pojo.getSem().toUpperCase().contains(query.toUpperCase())) {
                temppojoclass.add(pojo);
            }
        }
        adapterClass = new AllStudentAdapter(temppojoclass, getActivity());
        lv_all_student.setAdapter(adapterClass);
    }

    @Override
    public void onStart() {
        super.onStart();
        pojoAllStudents.clear();  // Prevent duplicates on back-stack return
        progressBar.setVisibility(View.VISIBLE);
        getAllStudent();
    }

    private void getAllStudent() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // 🛡️ Absolute Data Isolation: HODs/Teachers strictly see their own department students
        String sessionBranch = preferences.getString("branch", "").trim();
        params.put("branch", sessionBranch);

        client.post(Urls.urlGetAllStudent, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = response.getJSONArray("getAllStudent");
                            if (jsonArray.length() == 0) {
                                if (getView() != null) getView().findViewById(R.id.no_records_layout).setVisibility(View.VISIBLE);
                                lv_all_student.setVisibility(View.GONE);
                            } else {
                                if (getView() != null) getView().findViewById(R.id.no_records_layout).setVisibility(View.GONE);
                                lv_all_student.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String id = jsonObject.getString("id");
                                    String image = jsonObject.getString("image");
                                    String name = jsonObject.getString("name");
                                    String mobile_no = jsonObject.getString("mobile_no");
                                    String email_id = jsonObject.getString("email");
                                    String gender = jsonObject.getString("gender");
                                    String address = jsonObject.getString("address");
                                    String branch = jsonObject.getString("branch");
                                    String sem = jsonObject.getString("sem");
                                    String subject = jsonObject.getString("subject");
                                    String username = jsonObject.getString("username");
                                    String password = jsonObject.getString("password");

                                    pojoAllStudents.add(new POJOAllStudent(id, image, name, mobile_no, email_id, gender,
                                            address, branch, sem, subject, username, password));
                                }

                                adapterClass = new AllStudentAdapter(pojoAllStudents, getActivity());
                                lv_all_student.setAdapter(adapterClass);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (isAdded() && getActivity() != null) {
                                Toast.makeText(getActivity(), "Parser Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressBar.setVisibility(View.GONE);
                        if (isAdded() && getActivity() != null) {
                            Toast.makeText(getActivity(), "Server Connection Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
