package com.polycampus.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.polycampus.android.common.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class eNoticeActivity extends AppCompatActivity {

    RecyclerView rvlist;
    List<POJONotice> pojoNotices;
    AdpterNotice adpterNotice;
    View tvNoNotice;
    android.content.SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enotice);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        rvlist = findViewById(R.id.rvList);
        tvNoNotice = findViewById(R.id.tvNoNOtice);
        pojoNotices = new ArrayList<>();
        
        // Using StaggeredGridLayoutManager for a modern grid look
        rvlist.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        getData();
    }

    private void getData() {
        com.android.volley.RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(eNoticeActivity.this);
        com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(com.android.volley.Request.Method.POST, Urls.getNotice, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("getNotice");
                
                pojoNotices.clear();
                if (jsonArray.length() == 0) {
                    rvlist.setVisibility(View.GONE);
                    tvNoNotice.setVisibility(View.VISIBLE);
                } else {
                    rvlist.setVisibility(View.VISIBLE);
                    tvNoNotice.setVisibility(View.GONE);
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String id = jsonObject1.getString("id");
                    String title = jsonObject1.getString("title");
                    String date = jsonObject1.getString("date");
                    String time = jsonObject1.getString("time");
                    String dis = jsonObject1.optString("description", jsonObject1.optString("dis", ""));
                    String image = jsonObject1.getString("image");
                    pojoNotices.add(new POJONotice(id, image, title, time, date, dis));
                }
                
                adpterNotice = new AdpterNotice(pojoNotices, eNoticeActivity.this);
                rvlist.setAdapter(adpterNotice);
                
            } catch (JSONException e) {
                Log.e("eNoticeActivity", "JSON Parsing error", e);
                Toast.makeText(eNoticeActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(eNoticeActivity.this, "Error fetching notices", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("branch", preferences.getString("branch", "All"));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
