package com.polycampus.android.TeacherApp.AllStudent;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.material.imageview.ShapeableImageView;

public class StudentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        ShapeableImageView imgProfile = findViewById(R.id.imgStudentDetailProfile);
        TextView tvName = findViewById(R.id.tvStudentDetailName);
        TextView tvRoll = findViewById(R.id.tvStudentDetailRoll);
        TextView tvBranch = findViewById(R.id.tvStudentDetailBranch);
        TextView tvSem = findViewById(R.id.tvStudentDetailSem);
        TextView tvAttendance = findViewById(R.id.tvStudentDetailAttendance);
        TextView tvMobile = findViewById(R.id.tv_student_detail_mobile);
        TextView tvEmail = findViewById(R.id.tv_student_detail_email);
        TextView tvAddress = findViewById(R.id.tv_student_detail_address);

        // Get data from intent
        POJOAllStudent student = (POJOAllStudent) getIntent().getSerializableExtra("student_data");

        if (student != null) {
            tvName.setText(student.getName());
            tvRoll.setText("Enrollment: " + student.getId());
            tvBranch.setText("Branch: " + student.getBranch());
            tvSem.setText("Semester: " + student.getSem());
            tvMobile.setText(student.getMobile_no());
            tvEmail.setText(student.getEmail());
            tvAddress.setText(student.getAddress());
            
            // Dummy attendance for now
            tvAttendance.setText("85% (Good)");

            Glide.with(this)
                .load(Urls.IMAGE_ASSET_DIR + student.getImage())
                .placeholder(R.drawable.profileimage)
                .into(imgProfile);
        }

        findViewById(R.id.btnDetailBack).setOnClickListener(v -> finish());
    }
}
