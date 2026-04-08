package com.polycampus.android;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.Attendance.AttendanceFragment;
import com.polycampus.android.MyProfile.MyProfileFragment;
import com.polycampus.android.StudyMaterial.StudyMaterialFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.homeBottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.homeBottomNavigationView);

    }
    StudyMaterialFragment studyMaterialFragment= new StudyMaterialFragment();
    AttendanceFragment attendanceFragment = new AttendanceFragment();
    LibraryFragment libraryFragment = new LibraryFragment();
    PlacementsFragment placementsFragment= new PlacementsFragment();
    MyProfileFragment myProfileFragment = new MyProfileFragment();





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.homeBottomNavigationView){
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout,studyMaterialFragment);
        } else if (item.getItemId()==R.id.homeBottomNavigationView) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout,attendanceFragment);
        } else if (item.getItemId()==R.id.homeBottomNavigationView) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout, libraryFragment);
        } else if (item.getItemId()==R.id.homeBottomNavigationView) {
           getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout,placementsFragment);
        } else if (item.getItemId()==R.id.homeBottomNavigationView) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout,placementsFragment);
        } else if (item.getItemId()==R.id.homeBottomNavigationView) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFramelayout,myProfileFragment);
        }


        return true;
    }
}
