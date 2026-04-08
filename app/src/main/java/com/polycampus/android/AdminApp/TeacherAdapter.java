package com.polycampus.android.AdminApp;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TeacherAdapter extends BaseAdapter {
    List<POJOAllTeacher> list;
    Activity activity;

    public TeacherAdapter(List<POJOAllTeacher> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(activity).inflate(R.layout.lv_all_teacher, parent, false);
        
        POJOAllTeacher obj = list.get(position);
        
        TextView tvName = v.findViewById(R.id.txt_teacher_name);
        TextView tvSubjects = v.findViewById(R.id.txt_teacher_subjects);
        TextView tvJoining = v.findViewById(R.id.txt_teacher_joining);
        ShapeableImageView imgProfile = v.findViewById(R.id.img_teacher_profile);
        ImageButton btnDelete = v.findViewById(R.id.btn_deleteTeacher);
        
        tvName.setText(obj.getName());
        tvSubjects.setText("Subjects: " + obj.getSubjects());
        tvJoining.setText("Joined: " + obj.getDate_of_joining());
        
        Glide.with(activity)
                .load(Urls.IMAGE_ASSET_DIR + obj.getImage())
                .placeholder(R.drawable.profileimage)
                .into(imgProfile);
                
        btnDelete.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(activity)
                    .setTitle("Remove Teacher")
                    .setMessage("Are you sure you want to remove " + obj.getName() + " from the faculty?")
                    .setPositiveButton("Keep", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Remove", (dialog, which) -> deleteTeacher(obj.getId(), position))
                    .show();
        });
        
        return v;
    }

    private void deleteTeacher(String id, final int pos) {
        final android.app.ProgressDialog pd = new android.app.ProgressDialog(activity);
        pd.setMessage("Removing teacher...");
        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        
        client.post(Urls.urlDeleteTeacher, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pd.dismiss();
                try {
                    String status = response.optString("status", "");
                    String success = response.optString("success", "");
                    
                    if (status.equalsIgnoreCase("success") || success.equals("1")) {
                        Toast.makeText(activity, "Teacher Removed Successfully", Toast.LENGTH_SHORT).show();
                        list.remove(pos);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(activity, "Failed to remove: " + response.optString("message", "Unknown Error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                pd.dismiss();
                Toast.makeText(activity, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
