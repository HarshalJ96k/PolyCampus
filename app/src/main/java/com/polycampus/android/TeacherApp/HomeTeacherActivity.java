 package com.polycampus.android.TeacherApp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.R;
import com.polycampus.android.TeacherApp.AddAttendance.ViewCurrentAttendanceFragment;
import com.polycampus.android.TeacherApp.AddStudyMaterial.AddStudyMaterialActivity;
import com.polycampus.android.TeacherApp.AddTimeTable.AddTimeTableFragment;
import com.polycampus.android.TeacherApp.AllStudent.AllStudentFragment;
import com.polycampus.android.TeacherApp.ScanStudentQRCode.ScanStudentQRCodeActivity;
import com.polycampus.android.TeacherApp.SubjectwiseAttendance.SubjectwiseAttendanceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeTeacherActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_teacher);
        preferences = PreferenceManager.getDefaultSharedPreferences(HomeTeacherActivity.this);
        editor = preferences.edit();

        setTitle("Teacher App");

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firsttime = prefs.getBoolean("firsttime", true);

        if (firsttime) {
            welcome();
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_home_all_student);
    }
    private void welcome() {
        AlertDialog.Builder ad = new AlertDialog.Builder(HomeTeacherActivity.this);
        ad.setTitle("Teacher App");
        ad.setMessage("Welcome to  Teacher App");
        ad.setPositiveButton("Thank you", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();

        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firsttime", false);
        editor.apply();

    }

    AllStudentFragment allStudentFragment = new AllStudentFragment();
    SubjectwiseAttendanceFragment subjectwiseAttendanceFragment = new SubjectwiseAttendanceFragment();
    ViewCurrentAttendanceFragment viewCurrentAttendanceFragment = new ViewCurrentAttendanceFragment();
    AddTimeTableFragment addTimeTableFragment = new AddTimeTableFragment();
    LeaveRequestsFragment leaveRequestsFragment = new LeaveRequestsFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home_all_student){
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, allStudentFragment).commit();
            return true;
        }
        else if (id == R.id.menu_home_subject_wise_attendance){
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, subjectwiseAttendanceFragment).commit();
            return true;
        }

        else if (id == R.id.menu_home_add_attendance){
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, viewCurrentAttendanceFragment).commit();
            return true;
        }
        else if (id == R.id.menu_home_add_notice){
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, addTimeTableFragment).commit();
            return true;
        }
        else if (id == R.id.menu_home_leave_requests){
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, leaveRequestsFragment).commit();
            return true;
        }
//        switch (item.getItemId()) {
//            case R.id.menu_home_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
//                return true;
//
//            case R.id.menu_home_citywise_saluna:
//                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, citywiseFragment).commit();
//                return true;
//
//            case R.id.menu_home_my_booking:
//                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, myBookingFragment).commit();
//                return true;
//
//            case R.id.menu_home_my_profile:
//                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, myProfileFragment).commit();
//                return true;
//        }
        return false;
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void logout() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(HomeTeacherActivity.this)
                .setTitle("Teacher Log Out")
                .setMessage("Are you sure you want to log out from the teacher portal?")
                .setIcon(R.drawable.baseline_logout_24)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(HomeTeacherActivity.this, com.polycampus.android.LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        editor.putBoolean("isTeacherLogin", false).commit();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.menuThemeToggle) {
//            com.polycampus.android.common.ThemeHelper.toggleTheme(this);
//            recreate();
//            return true;
//        }
//        if (item.getItemId() == R.id.home_menu_scan_student) {
//            Intent intent = new Intent(HomeTeacherActivity.this, ScanStudentQRCodeActivity.class);
//            startActivity(intent);
//        }
        if (item.getItemId() == R.id.home_menu_add_study_material) {
            Intent intent = new Intent(HomeTeacherActivity.this, AddStudyMaterialActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.home_menu_upload_marks) {
            Intent intent = new Intent(HomeTeacherActivity.this, UploadMarksActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.home_menu_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
}
