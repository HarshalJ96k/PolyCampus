package com.polycampus.android.Attendance;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddAttendanceAdapter extends BaseAdapter {

    List<POJOAddAttendance> list;
    AppCompatActivity activity;
    TextView tv_no_records;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String presentydoneornot;

    public AddAttendanceAdapter(List<POJOAddAttendance> list,
                                AppCompatActivity activity,
                                TextView tv_no_records,
                                String presentydoneornot) {
        this.list = list;
        this.activity = activity;
        this.tv_no_records = tv_no_records;
        this.presentydoneornot = presentydoneornot;

        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        editor = preferences.edit();
    }

    public AddAttendanceAdapter(String presentydoneornot) {
        this.presentydoneornot = presentydoneornot;
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

        final AddAttendanceAdapter.ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (v == null) {
            holder = new AddAttendanceAdapter.ViewHolder();
            v = inflater.inflate(R.layout.lv_add_presenty, null);

            holder.branch = (TextView) v.findViewById(R.id.tv_add_presenty_branch);
            holder.sem = (TextView) v.findViewById(R.id.tv_add_presenty_sem);
            holder.subject = (TextView) v.findViewById(R.id.tv_add_presenty_subject);
            holder.date = (TextView) v.findViewById(R.id.tv_add_presenty_date);
            holder.time_from = (TextView) v.findViewById(R.id.tv_add_presenty_time_from);
            holder.time_to = (TextView) v.findViewById(R.id.tv_add_presenty_time_to);
            holder.spinner_presenty_status = (Spinner) v.findViewById(R.id.spinner_presenty);
            holder.btn_add_presenty = (Button) v.findViewById(R.id.btn_add_presenty_add_presenty);

            v.setTag(holder);
        } else {
            holder = (AddAttendanceAdapter.ViewHolder) v.getTag();
        }

        final POJOAddAttendance obj = list.get(position);
        holder.branch.setText(obj.getBranch());
        holder.sem.setText(obj.getSem());
        holder.subject.setText(obj.getSubject());
        holder.date.setText(obj.getDate());
        holder.time_from.setText(obj.getTime_from());
        holder.time_to.setText(obj.getTime_to());

        String currentDate = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String firsttwodigits = currentTime.substring(0, 2);
        Integer time = Integer.valueOf(firsttwodigits);
//        Toast.makeText(activity, ""+firsttwodigits, Toast.LENGTH_SHORT).show();
//        Toast.makeText(activity, "" + currentDate + " " + obj.getDate(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(activity, "check Status "+presentydoneornot, Toast.LENGTH_SHORT).show();

        if (presentydoneornot == "Done") {
            holder.btn_add_presenty.setText("You Can't Add Presenty Again");
            AlertDialog.Builder ad = new AlertDialog.Builder(activity);
            ad.setTitle("Attendance");
            ad.setMessage("You Already Fill Your Attendance");
            ad.setPositiveButton("Thank you", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();

        } else {
            holder.btn_add_presenty.setText("Add Presenty");
            holder.btn_add_presenty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.spinner_presenty_status.getSelectedItem().toString().equals("Select Presenty")) {
                        Toast.makeText(activity, "Select Presenty Status", Toast.LENGTH_SHORT).show();

                    } else {
                        Intent intent = new Intent(activity, LoginThroughFingerPrintActivity.class);
                        editor.putString("attendance_date", obj.getDate()).commit();
                        editor.putString("attendance_subject", obj.getSubject()).commit();
                        editor.putString("attendance_presenty_status", holder.spinner_presenty_status.getSelectedItem().toString()).commit();
                        activity.startActivity(intent);
//                        AsyncHttpClient client = new AsyncHttpClient();
//                        RequestParams params = new RequestParams();
//                        params.put("enrollment_no",preferences.getString("enrollment_no",""));
//                        params.put("date",obj.getDate());
//                        params.put("subject",obj.getSubject());
//                        params.put("presenty_status",holder.spinner_presenty_status.getSelectedItem().toString());
//
//                        client.post(Urls.urlAddPendingAttendance, params, new JsonHttpResponseHandler(){
//
//                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//
//                                try {
//                                    String aa = response.getString("success");
//
//                                    if (aa.equals("1")) {
//                                        Toast.makeText(activity, "Attendence Submitted Successfully", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(new Intent(activity, HomeActivity.class));
//                                        activity.startActivity(intent);
//                                    } else {
//                                        Toast.makeText(activity, "Already Presenty Done", Toast.LENGTH_SHORT).show();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            public void onFailure(int statusCode, Header[] headers, String res, Throwable t)
//                            {
//                                Toast.makeText(activity, "could not connect", Toast.LENGTH_LONG).show();
//
//                            }
//
//                        });
                    }
                }
            });

        }


        return v;
    }

    class ViewHolder {
        TextView id, branch, sem, subject, date, time_from, time_to;
        Spinner spinner_presenty_status;
        Button btn_add_presenty;
    }
}
