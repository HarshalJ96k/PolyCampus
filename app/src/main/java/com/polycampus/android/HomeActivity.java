package com.polycampus.android;



import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.Attendance.AttendanceFragment;
import com.polycampus.android.MyProfile.MyProfileFragment;
import com.polycampus.android.StudyMaterial.StudyMaterialActivity;
import com.polycampus.android.SubjectwiseAttendance.SubjectwiseAttendanceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener
{
    BottomNavigationView bottomNavigationView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean doubletap = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        editor = preferences.edit();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firsttime = prefs.getBoolean("firsttime", true);

        if (firsttime) {
        welcome();
        }

        bottomNavigationView = findViewById(R.id.homeBottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.homeBottomNavigationAttendance);

    }


    AttendanceFragment attendanceFragment = new AttendanceFragment();
    LibraryFragment libraryFragment = new LibraryFragment();
    StudentSectionFragment studentSectionFragment = new StudentSectionFragment();

    SubjectwiseAttendanceFragment subjectwiseAttendanceFragment = new SubjectwiseAttendanceFragment();
    ApplyLeaveFragment applyLeaveFragment = new ApplyLeaveFragment();
    MyProfileFragment myProfileFragment = new MyProfileFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {

        if (item.getItemId() == R.id.homeBottomNavigationAttendance)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout, attendanceFragment).commit();
        }
        else if (item.getItemId() == R.id.homeBottomNavigationStudentSection)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout,studentSectionFragment).commit();
        }
        else if (item.getItemId() == R.id.homeBottomNavigationSubjectwiseAttendance)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout, subjectwiseAttendanceFragment).commit();
        }
        else if (item.getItemId() == R.id.homeBottomNavigationLeave)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout, applyLeaveFragment).commit();
        }
        else if (item.getItemId() == R.id.homeBottomNavigationMyProfile)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout, myProfileFragment).commit();
        }
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuhome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.menuThemeToggle) {
//            com.polycampus.android.common.ThemeHelper.toggleTheme(this);
//            recreate();
//            return true;
//        }
        if (item.getItemId() == R.id.menuHomeeNotice) {
            Intent i = new Intent(HomeActivity.this, eNoticeActivity.class);
            startActivity(i);
        }
        else if (item.getItemId() == R.id.menuStudyMaterial) {
            Intent i = new Intent(HomeActivity.this, StudyMaterialActivity.class);
            startActivity(i);
        }

        else if (item.getItemId() == R.id.menuHomeAboutUs) {
            Intent i = new Intent(HomeActivity.this, AboutusActivity.class);
            startActivity(i);
        } else if (item.getItemId() == R.id.menuHomeContactUs) {
            Intent i = new Intent(HomeActivity.this, ContactusActivity.class);
            startActivity(i);
        } else if (item.getItemId() == R.id.menuHomeLogOut) {
            logout();
        }
        return true;
    }

    private void logout() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(HomeActivity.this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to exit the PolyCampus portal? You'll need to log in again to access your account.")
                .setIcon(R.drawable.baseline_logout_24)
                .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("isLogin", false).commit();
                        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void onBackPressed() {
        logout();
        }

    private void welcome() {
        AlertDialog.Builder ad = new AlertDialog.Builder(HomeActivity.this);
        ad.setTitle("It's Our PolyCampus");
        ad.setMessage("WelCome to Our PolyCampus");
        ad.setPositiveButton("Thank You", new
                DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
        editor.putBoolean("firsttime",false).commit();
    }
}





