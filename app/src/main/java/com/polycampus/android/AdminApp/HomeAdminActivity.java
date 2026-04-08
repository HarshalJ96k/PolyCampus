package com.polycampus.android.AdminApp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.polycampus.android.LoginActivity;
import com.polycampus.android.R;
import com.polycampus.android.TeacherApp.AllStudent.AllStudentFragment;
import com.polycampus.android.TeacherApp.AddTimeTable.AddTimeTableFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.polycampus.android.common.Urls;

public class HomeAdminActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        
        // 1. Initialize UI Elements First
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // 2. Identify and Lock Department Dashboard
        String sessionBranch = preferences.getString("branch", "").trim();
        if (sessionBranch.isEmpty()) {
            android.widget.Toast.makeText(this, "Security Breach: Missing Department Profile. Access Denied.", android.widget.Toast.LENGTH_LONG).show();
            // Securely Log Out if no department is assigned
            editor.putBoolean("isAdminLogin", false).commit();
            finish();
        }
        setTitle(sessionBranch + " Dept Portal");

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        
        // Default fragment: Students
        bottomNavigationView.setSelectedItemId(R.id.menu_admin_students);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.home_menu_logout) {
            logout();
            return true;
        } else if (id == R.id.home_menu_add_study_material) {
            Intent intent = new Intent(HomeAdminActivity.this, com.polycampus.android.TeacherApp.AddStudyMaterial.AddStudyMaterialActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.home_menu_upload_marks) {
            Intent intent = new Intent(HomeAdminActivity.this, com.polycampus.android.TeacherApp.UploadMarksActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.home_menu_change_password) {
            showChangeDashboardPasswordDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();
        
        if (id == R.id.menu_admin_students) {
            selectedFragment = new AllStudentFragment();
        } else if (id == R.id.menu_admin_notices) {
            selectedFragment = new AddTimeTableFragment(); // Reusing notice/timetable fragment
        } else if (id == R.id.menu_admin_teachers) {
            selectedFragment = new ManageTeacherFragment();
        } else if (id == R.id.menu_admin_subjects) {
            selectedFragment = new ManageSubjectsFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, selectedFragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void showChangeDashboardPasswordDialog() {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        com.google.android.material.textfield.TextInputEditText etNewPass = dialogView.findViewById(R.id.etNewPassword);
        
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Update Dashboard Security")
                .setMessage("Enter a new password for your administrative portal account. You will need to re-login after this.")
                .setView(dialogView)
                .setPositiveButton("Update Password", (dialog, which) -> {
                    String pass = etNewPass.getText().toString();
                    if (pass.length() < 6) {
                        etNewPass.setError("Password must be at least 6 characters");
                        return;
                    }
                    performChangePassword(pass);
                })
                .setNegativeButton("Keep Current", null)
                .show();
    }

    private void performChangePassword(String newPass) {
        com.loopj.android.http.AsyncHttpClient client = new com.loopj.android.http.AsyncHttpClient();
        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
        params.put("username", preferences.getString("username", ""));
        params.put("new_password", newPass);

        client.post(Urls.urlChangeAdminPassword, params, new com.loopj.android.http.JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, org.json.JSONObject response) {
                try {
                    if (response.getInt("success") == 1) {
                        android.widget.Toast.makeText(HomeAdminActivity.this, "Security credentials updated. Logging out...", android.widget.Toast.LENGTH_LONG).show();
                        logout();
                    } else {
                        android.widget.Toast.makeText(HomeAdminActivity.this, response.optString("message", "Error updating credentials"), android.widget.Toast.LENGTH_SHORT).show();
                    }
                } catch (org.json.JSONException e) { e.printStackTrace(); }
            }
        });
    }

    private void logout() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Portal Log Out")
                .setMessage("Are you sure you want to log out from the administrative portal?")
                .setIcon(R.drawable.baseline_logout_24)
                .setPositiveButton("Logout", (dialog, which) -> {
                    editor.putBoolean("isAdminLogin", false).commit();
                    Intent intent = new Intent(HomeAdminActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
