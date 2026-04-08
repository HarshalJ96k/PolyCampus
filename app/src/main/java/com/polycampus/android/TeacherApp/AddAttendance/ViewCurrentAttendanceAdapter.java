package com.polycampus.android.TeacherApp.AddAttendance;

import android.app.Activity;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ViewCurrentAttendanceAdapter extends BaseAdapter {

    List<POJOViewCurrentAttendance> list;
    AppCompatActivity activity;
    TextView tv_no_records;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public ViewCurrentAttendanceAdapter(List<POJOViewCurrentAttendance> list, AppCompatActivity activity, TextView tv_no_records) {
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

        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.lv_view_current_attendance, null);

            holder.branch = (TextView) v.findViewById(R.id.tv_add_presenty_branch);
            holder.sem = (TextView) v.findViewById(R.id.tv_add_presenty_sem);
            holder.subject = (TextView) v.findViewById(R.id.tv_add_presenty_subject);
            holder.date = (TextView) v.findViewById(R.id.tv_add_presenty_date);
            holder.time_from = (TextView) v.findViewById(R.id.tv_add_presenty_time_from);
            holder.time_to = (TextView) v.findViewById(R.id.tv_add_presenty_time_to);
            holder.btn_delete_presenty = (Button) v.findViewById(R.id.btn_add_presenty_delete_presenty);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        final POJOViewCurrentAttendance obj = list.get(position);
        holder.branch.setText(obj.getBranch());
        holder.sem.setText(obj.getSem());
        holder.subject.setText(obj.getSubject());
        holder.date.setText(obj.getDate());
        holder.time_from.setText(obj.getTime_from());
        holder.time_to.setText(obj.getTime_to());
        
        
        holder.btn_delete_presenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(activity);
                ad.setTitle("")
                        .setMessage("Are You Sure You Want To Delete")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteAttendance(Integer.parseInt(obj.getId()),position);
                            }
                        });

                AlertDialog alertDialog = ad.create();
                alertDialog.show();
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.darker_gray);
            }
        });

        return v;
    }

    private void deleteAttendance(int id, int position) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", ""+id);
        client.post(Urls.urlDeleteAttendance, params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String aa = response.getString("success");

                    if (aa.equals("1")) {
                        list.remove(position);
                        notifyDataSetChanged();

                    } else {
                        Toast.makeText(activity, "Unable to Remove Attendance", Toast.LENGTH_SHORT).show();
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
        TextView id, branch, sem, subject, date, time_from, time_to;
        Button btn_delete_presenty;
    }
}
