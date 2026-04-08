package com.polycampus.android.TeacherApp.SubjectwiseAttendance;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.R;

import java.util.List;

public class ViewStudentDatewisePresentyAdapter extends BaseAdapter {

    List<PojoClassViewStudentDatewisePresenty> list;
    AppCompatActivity activity;
    TextView tv_no_records;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public ViewStudentDatewisePresentyAdapter(List<PojoClassViewStudentDatewisePresenty> list, AppCompatActivity activity, TextView tv_no_records) {
        this.list = list;
        this.activity = activity;
        this.tv_no_records = tv_no_records;

        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        editor = preferences.edit();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (v == null)
        {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.list_datwise_attendance, null);

            holder.enrollment_no = (TextView) v.findViewById(R.id.tvstudentEnrollmentNumber);
            holder.student_name= (TextView) v.findViewById(R.id.tvstudentName);
            holder.subjectname= (TextView) v.findViewById(R.id.tvSubjectName);
            holder.presenty_status = (TextView)v.findViewById(R.id.tvstudentPresentyStatus);

            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) v.getTag();
        }

        final PojoClassViewStudentDatewisePresenty obj = list.get(position);
        holder.enrollment_no.setText(obj.getEnrollmentno());
        holder.student_name.setText(obj.getStudent_name());
        holder.subjectname.setText(obj.getsubject_name());
        holder.presenty_status.setText(obj.getPresenty());

        return v;
    }

    class ViewHolder
    {
        TextView enrollment_no,subjectname,student_name,presenty_status;
    }
}
