package com.polycampus.android.TeacherApp.AllStudent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.polycampus.android.R;
import com.polycampus.android.TeacherApp.HomeTeacherActivity;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AllStudentAdapter extends BaseAdapter {

    List<POJOAllStudent> list;
    Activity activity;
    TextView tv_no_records;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public AllStudentAdapter(List<POJOAllStudent> list, Activity activity) {
        this.list = list;
        this.activity = activity;

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

        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.lv_all_student, null);

            holder.tv_name = (TextView) v.findViewById(R.id.txt_my_details_student_name);
            holder.tv_mobile_no = (TextView) v.findViewById(R.id.txt_my_details_student_mobile_no);
            holder.tv_email_id = (TextView) v.findViewById(R.id.txt_my_details_student_email);
            holder.tv_gender = (TextView) v.findViewById(R.id.txt_my_details_gender);
            holder.tv_address = (TextView) v.findViewById(R.id.txt_my_details_address);
            holder.tv_branch = (TextView) v.findViewById(R.id.txt_my_details_student_branch);
            holder.tv_sem = (TextView) v.findViewById(R.id.txt_my_details_student_sem);
            holder.tv_roll_no = (TextView) v.findViewById(R.id.txt_my_details_student_roll_no);
            holder.tv_username = (TextView) v.findViewById(R.id.txt_my_details_student_username);
            holder.btn_add_attendance = (Button) v.findViewById(R.id.btn_add_attendance);
            holder.btn_deleteStudent = (android.widget.ImageButton) v.findViewById(R.id.btn_deleteStudent);
            holder.btn_deleteStudent.setVisibility(View.GONE);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        final POJOAllStudent obj = list.get(position);
        holder.tv_name.setText(obj.getName());

        // Bind new subtitle
        TextView tvSubtitle = v.findViewById(R.id.txt_student_info_subtitle);
        if (tvSubtitle != null) {
            tvSubtitle.setText(obj.getUsername() + " | " + obj.getBranch());
        }

        holder.tv_mobile_no.setText(obj.getMobile_no());
        holder.tv_email_id.setText(obj.getEmail());
        holder.tv_gender.setText(obj.getGender());
        holder.tv_address.setText(obj.getAddress());
        holder.tv_branch.setText(obj.getBranch());
        holder.tv_sem.setText("Semester " + obj.getSem());
        holder.tv_username.setText(obj.getUsername());

//        holder.btn_add_attendance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(activity, AddAttendanceActivity.class);
//                editor.putString("name",obj.getName()).commit();
//                editor.putString("roll_no",obj.getRoll_no()).commit();
//                editor.putString("student_id",obj.getId()).commit();
//                activity.startActivity(intent);
//            }
//        });

        holder.btn_deleteStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(activity)
                        .setTitle("Delete Student")
                        .setMessage("Are you sure you want to permanently remove " + obj.getName() + " from the records?")
                        .setIcon(R.drawable.baseline_delete_24)
                        .setPositiveButton("Keep Student", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setNegativeButton("Delete", (dialogInterface, i) -> deleteStudent(obj.getId(), position))
                        .show();
            }
        });

        return v;
    }

    private void deleteStudent(String strId, final int pos) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("student_id", strId);
        client.post(Urls.urldeleteStudent, params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String aa = response.getString("success");

                    if (aa.equals("1")) {
                        Toast.makeText(activity, "Student Record Removed Successfully", Toast.LENGTH_SHORT).show();
                        list.remove(pos);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(activity, "Unable to delete Student", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // progress.setVisibility(View.GONE);
                // Toast.makeText(AddEventActivity.this, "Could Not Connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ViewHolder {
        TextView tv_name, tv_mobile_no, tv_email_id,tv_gender, tv_address,tv_branch,tv_sem,tv_roll_no,tv_username;
        Button btn_add_attendance;
        android.widget.ImageButton btn_deleteStudent;
    }
}
