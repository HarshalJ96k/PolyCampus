package com.polycampus.android.TeacherApp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.AdminApp.POJOAllTeacher;
import com.polycampus.android.R;
import com.polycampus.android.TeacherApp.AllStudent.POJOAllStudent;
import com.polycampus.android.common.Urls;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UploadMarksActivity extends AppCompatActivity {

    AutoCompleteTextView spBranch, spSem, spSubject;
    MaterialButton btnFetch, btnSubmit;
    ListView lvMarks;
    List<POJOAllStudent> studentList;
    MarksAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_marks);

        spBranch = findViewById(R.id.spBranchMarks);
        spSem = findViewById(R.id.spSemMarks);
        spSubject = findViewById(R.id.spSubjectMarks);
        btnFetch = findViewById(R.id.btnFetchStudentsMarks);
        btnSubmit = findViewById(R.id.btnSubmitAllMarks);
        lvMarks = findViewById(R.id.lvMarksEntry);

        setupSpinners();
        
        btnFetch.setOnClickListener(v -> fetchStudents());
        btnSubmit.setOnClickListener(v -> submitMarks());

        // 🛡️ Absolute Departmental Isolation for Marks
        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String sessionBranch = prefs.getString("branch", "").trim();
        spBranch.setText(sessionBranch, false);
        
        // Always hide the branch container as it's now locked to the session
        View branchContainer = findViewById(R.id.spBranchMarks).getParent().getParent() instanceof View ? (View)findViewById(R.id.spBranchMarks).getParent().getParent() : null;
        if (branchContainer != null) branchContainer.setVisibility(View.GONE);
        
        if (sessionBranch.isEmpty()) {
            Toast.makeText(this, "Security Error: Missing Departmental Profile", Toast.LENGTH_LONG).show();
            finish();
        }

        findViewById(R.id.toolbarUploadMarks).setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        String[] branches = {"Computer Engg", "IT Engg", "Civil Engg", "Electrical Engg", "Mechanical Engg"};
        String[] semesters = {"Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6"};
        
        spBranch.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, branches));
        spSem.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, semesters));
        
        // Dynamic Subject Allocation
        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String allocatedSubjects = prefs.getString("subjects", "No subjects allocated");
        
        String[] subjects;
        if (!allocatedSubjects.isEmpty() && !allocatedSubjects.equals("No subjects allocated")) {
            subjects = allocatedSubjects.split(",");
        } else {
            subjects = new String[]{"Contact Admin for Subject Allocation"};
        }
        
        spSubject.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects));
    }

    private void fetchStudents() {
        studentList = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("branch", spBranch.getText().toString());
        params.put("sem", spSem.getText().toString());
        
        client.post(Urls.urlGetAllStudent, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("getAllStudent");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        studentList.add(new POJOAllStudent(
                                jsonObject.getString("id"),
                                jsonObject.getString("image"),
                                jsonObject.getString("name"),
                                "", "", "", "", "", "", "", "", ""
                        ));
                    }
                    adapter = new MarksAdapter(studentList);
                    lvMarks.setAdapter(adapter);
                    btnSubmit.setVisibility(View.VISIBLE);
                } catch (JSONException e) { e.printStackTrace(); }
            }
        });
    }

    private void submitMarks() {
        if (studentList == null || studentList.isEmpty()) {
            Toast.makeText(this, "No students to upload marks for", Toast.LENGTH_SHORT).show();
            return;
        }

        final android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setMessage("Uploading Batch Marks...");
        pd.setCancelable(false);
        pd.show();

        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String subject = spSubject.getText().toString();
        String branch = spBranch.getText().toString();
        String sem = spSem.getText().toString();

        final int totalItems = studentList.size();
        final int[] completedCount = {0};

        AsyncHttpClient client = new AsyncHttpClient();

        for (int i = 0; i < studentList.size(); i++) {
            POJOAllStudent student = studentList.get(i);
            
            // Get the view at this position to extract the marks from EditText
            View view = lvMarks.getChildAt(i - lvMarks.getFirstVisiblePosition());
            String marksValue = "0";
            if (view != null) {
                TextInputEditText et = view.findViewById(R.id.etStudentMarksValue);
                marksValue = et.getText().toString();
            }

            RequestParams params = new RequestParams();
            params.put("student_id", student.getId());
            params.put("subject_name", subject);
            params.put("marks_obtained", marksValue);
            params.put("total_marks", "100");

            client.post(Urls.urlUploadMarks, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    completedCount[0]++;
                    if (completedCount[0] == totalItems) {
                        pd.dismiss();
                        // Check for 'success' key (matches our updated PHP)
                        String suc = response.optString("success", "0");
                        String msg = response.optString("message", "Batch Marks Submitted");
                        Toast.makeText(UploadMarksActivity.this,
                                suc.equals("1") ? "Batch Marks Submitted Successfully" : "Completed: " + msg,
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    completedCount[0]++;
                    if (completedCount[0] == totalItems) {
                        pd.dismiss();
                        Toast.makeText(UploadMarksActivity.this, "Completed with some errors", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }

    class MarksAdapter extends BaseAdapter {
        List<POJOAllStudent> list;
        public MarksAdapter(List<POJOAllStudent> list) { this.list = list; }
        @Override public int getCount() { return list.size(); }
        @Override public Object getItem(int position) { return list.get(position); }
        @Override public long getItemId(int position) { return position; }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(UploadMarksActivity.this).inflate(R.layout.lv_marks_entry, parent, false);
            TextView tvName = v.findViewById(R.id.txt_student_name_marks);
            tvName.setText(list.get(position).getName());
            return v;
        }
    }
}
