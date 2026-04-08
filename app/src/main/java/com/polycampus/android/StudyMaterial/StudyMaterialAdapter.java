package com.polycampus.android.StudyMaterial;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.polycampus.android.R;
import com.polycampus.android.common.Urls;

import java.util.List;

public class StudyMaterialAdapter extends BaseAdapter {

    List<PojoMyStudyMaterial> list;
    AppCompatActivity activity;
    TextView tv_no_records;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public StudyMaterialAdapter(List<PojoMyStudyMaterial> list, AppCompatActivity activity, TextView tv_no_records) {
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
            v = inflater.inflate(R.layout.lv_study_material, null);

            holder.study_material_title = (TextView) v.findViewById(R.id.lv_tv_study_material_title);
            holder.study_material_description = (TextView)v.findViewById(R.id.lv_tv_study_material_description);
            holder.iv_study_material_pdf = (ImageView) v.findViewById(R.id.iv_study_material_pdf);
            holder.study_material_date = (TextView)v.findViewById(R.id.lv_tv_study_material_date);
            holder.btn_open_pdf = v.findViewById(R.id.btn_open_pdf);
            holder.btn_download_pdf = v.findViewById(R.id.btn_download_pdf);

            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) v.getTag();
        }

        final PojoMyStudyMaterial obj = list.get(position);
        holder.study_material_title.setText(obj.getTitle());
        holder.study_material_description.setText(obj.getDescription());
        holder.study_material_date.setText(obj.getDate());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docPath = obj.getViewdoc();
                if (docPath == null || docPath.isEmpty()) {
                    Toast.makeText(activity, "No document available", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proper encoding of the URL path part to handle spaces/symbols
                String docUrl = Urls.OnlineDocAddress + Uri.encode(docPath);
                
                if (v.getId() == R.id.btn_download_pdf) {
                    // Direct Download Implementation
                    android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(docUrl));
                    request.setTitle(obj.getTitle());
                    request.setDescription("Downloading study material...");
                    request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, docPath);

                    android.app.DownloadManager downloadManager = (android.app.DownloadManager) activity.getSystemService(Activity.DOWNLOAD_SERVICE);
                    if (downloadManager != null) {
                        downloadManager.enqueue(request);
                        Toast.makeText(activity, "Downloading started...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Open/Preview Implementation
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(docUrl), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        activity.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Fallback: Open in Browser if no PDF viewer app is found
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(docUrl));
                        activity.startActivity(browserIntent);
                    }
                }
            }
        };

        holder.iv_study_material_pdf.setOnClickListener(clickListener);
        holder.btn_open_pdf.setOnClickListener(clickListener);
        holder.btn_download_pdf.setOnClickListener(clickListener);

        return v;
    }

    class ViewHolder {
        TextView study_material_title, study_material_description, study_material_date;
        ImageView iv_study_material_pdf;
        View btn_open_pdf, btn_download_pdf;
    }
}
