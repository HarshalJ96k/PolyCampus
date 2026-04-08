package com.polycampus.android.AdminApp;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.R;
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

import cz.msebera.android.httpclient.Header;

public class AddTeacherActivity extends AppCompatActivity {

    TextInputEditText etName, etMobile, etEmail, etUsername, etPassword;
    AutoCompleteTextView actvBranch;
    MaterialButton btnAdd, btnSelectSubjects;
    TextView tvSelectedSubjects;
    
    private String[] allSubjects;
    private boolean[] selectedSubjects;
    private ArrayList<Integer> userSelectedItems = new ArrayList<>();
    private String currentBranch = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        etName = findViewById(R.id.etTeacherName);
        etMobile = findViewById(R.id.etTeacherMobile);
        etEmail = findViewById(R.id.etTeacherEmail);
        actvBranch = findViewById(R.id.actvTeacherBranch);
        tvSelectedSubjects = findViewById(R.id.tvSelectedSubjectsCount);
        btnSelectSubjects = findViewById(R.id.btnSelectSubjects);
        etUsername = findViewById(R.id.etTeacherUsername);
        etPassword = findViewById(R.id.etTeacherPassword);
        btnAdd = findViewById(R.id.btnAddTeacher);

        // 🛡️ Data Integrity: Remove global branch selection
        // Admin (HOD) is strictly bound to their department
        
        // 🛡️ Absolute Departmental Lockdown
        android.content.SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentBranch = prefs.getString("branch", "").trim();
        actvBranch.setText(currentBranch, false);
        
        if (currentBranch.isEmpty()) {
            Toast.makeText(this, "Security Breach: No Department assigned to this account.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (currentBranch.equalsIgnoreCase("All")) {
            // Principal/Super Admin Access - Enable branch selection
            if (findViewById(R.id.tilTeacherBranch) != null) {
                findViewById(R.id.tilTeacherBranch).setVisibility(View.VISIBLE);
                // Standard Department List
                String[] branches = {"Computer Engineering", "Information Technology", "Civil Engineering", "Mechanical Engineering", "Electrical Engineering"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, branches);
                actvBranch.setAdapter(adapter);
                actvBranch.setOnItemClickListener((parent, view, position, id) -> {
                    currentBranch = branches[position];
                    loadSubjectsForBranch(currentBranch);
                    actvBranch.setError(null);
                });
            }
        } else {
            // HOD Access - Hide and lock to their department
            if (findViewById(R.id.tilTeacherBranch) != null) {
                findViewById(R.id.tilTeacherBranch).setVisibility(View.GONE);
            }
            loadSubjectsForBranch(currentBranch);
        }

        btnSelectSubjects.setOnClickListener(v -> {
            if (allSubjects == null || allSubjects.length == 0) {
                Toast.makeText(this, "Fetching departmental subjects... Please wait", Toast.LENGTH_SHORT).show();
                if (!currentBranch.isEmpty()) loadSubjectsForBranch(currentBranch);
                return;
            }
            showSubjectPicker();
        });

        btnAdd.setOnClickListener(v -> {
            if (validate()) { addTeacher(); }
        });
    }

    private void loadSubjectsForBranch(String branch) {
        RequestParams params = new RequestParams();
        params.put("branch", branch);
        new AsyncHttpClient().post(Urls.urlGetSubjectsByFilter, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = response.optJSONObject("data").optJSONArray("subjects");
                    if (array == null) array = response.optJSONArray("subjects");
                    
                    if (array != null && array.length() > 0) {
                        allSubjects = new String[array.length()];
                        selectedSubjects = new boolean[array.length()];
                        userSelectedItems.clear();
                        for (int i = 0; i < array.length(); i++) {
                            allSubjects[i] = array.getJSONObject(i).optString("subject_name");
                        }
                        Toast.makeText(AddTeacherActivity.this, "Curriculum Catalogue Loaded (" + array.length() + ")", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddTeacherActivity.this, "No subjects found for " + branch, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) { 
                    e.printStackTrace(); 
                    Toast.makeText(AddTeacherActivity.this, "Catalogue Parsing Fault", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject e) {
                Toast.makeText(AddTeacherActivity.this, "Gateway connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSubjectPicker() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Academic Portfolio Assignments")
                .setMultiChoiceItems(allSubjects, selectedSubjects, (dialog, which, isChecked) -> {
                    if (isChecked) { userSelectedItems.add(which); } 
                    else { userSelectedItems.remove((Integer) which); }
                    selectedSubjects[which] = isChecked;
                })
                .setPositiveButton("Assign", (dialog, which) -> {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < userSelectedItems.size(); i++) {
                        sb.append(allSubjects[userSelectedItems.get(i)]);
                        if (i != userSelectedItems.size() - 1) sb.append(", ");
                    }
                    tvSelectedSubjects.setText(sb.length() > 0 ? sb.toString() : "None assigned");
                })
                .setNeutralButton("Clear Access", (dialog, which) -> {
                    for (int i = 0; i < selectedSubjects.length; i++) selectedSubjects[i] = false;
                    userSelectedItems.clear();
                    tvSelectedSubjects.setText("None assigned");
                })
                .show();
    }

    private boolean validate() {
        if (etName.getText().toString().isEmpty() || etMobile.getText().toString().isEmpty() ||
            etEmail.getText().toString().isEmpty() || etUsername.getText().toString().isEmpty() ||
            etPassword.getText().toString().isEmpty() || currentBranch.isEmpty()) {
            Toast.makeText(this, "Security profile mapping incomplete", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addTeacher() {
        final android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setTitle("System Onboarding");
        pd.setMessage("Synchronizing professional credentials...");
        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", etName.getText().toString());
        params.put("mobile_no", etMobile.getText().toString());
        params.put("email_id", etEmail.getText().toString());
        params.put("branch", currentBranch);
        params.put("subjects", tvSelectedSubjects.getText().toString());
        params.put("username", etUsername.getText().toString());
        params.put("password", etPassword.getText().toString());
        
        client.post(Urls.urlAddTeacher, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pd.dismiss();
                if (response.optString("success", "").equals("1")) {
                    Toast.makeText(AddTeacherActivity.this, "Professional Credentials Authorized", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddTeacherActivity.this, response.optString("message", "System Integrity Fault"), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject e) {
                pd.dismiss();
                Toast.makeText(AddTeacherActivity.this, "Gateway Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
