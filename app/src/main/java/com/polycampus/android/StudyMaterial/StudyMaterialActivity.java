package com.polycampus.android.StudyMaterial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class StudyMaterialActivity extends AppCompatActivity {

    List<PojoMyStudyMaterial> list;
    ListView lv_my_study_material;
    TextView tv_no_records;
    View no_records_layout;
    SearchView sv_study_material;
    ProgressBar pBar;
    StudyMaterialAdapter adapter;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_material);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(StudyMaterialActivity.this);
        editor = preferences.edit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(preferences.getString("class_name", "") + " Study Material");
        }
        
        list = new ArrayList<PojoMyStudyMaterial>();
        lv_my_study_material = (ListView) findViewById(R.id.lv_my_study_material);
        tv_no_records = (TextView) findViewById(R.id.tv_no_records);
        no_records_layout = findViewById(R.id.no_records_layout);
        pBar = (ProgressBar) findViewById(R.id.progressBar);

        sv_study_material = findViewById(R.id.sv_study_material);
        sv_study_material.setQueryHint("Search study materials...");
        sv_study_material.setIconifiedByDefault(false);
        sv_study_material.setFocusable(false); // don't auto focus on start
        
        sv_study_material.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStudyMaterial(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchStudyMaterial(query);
                return false;
            }
        });

        myStudyMaterial();

    }

    private void searchStudyMaterial(String query) {
        List<PojoMyStudyMaterial> temppojoclass = new ArrayList<>();
        temppojoclass.clear();

        for(PojoMyStudyMaterial pojo :list)
        {
            if (pojo.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    pojo.getDate().toLowerCase().contains(query.toLowerCase()) ||
                    pojo.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                    pojo.getViewdoc().toLowerCase().contains(query.toLowerCase()))
            {
                temppojoclass.add(pojo);
            }
        }

        if (temppojoclass.isEmpty()) {
            no_records_layout.setVisibility(View.VISIBLE);
            tv_no_records.setText("No matching materials found");
        } else {
            no_records_layout.setVisibility(View.GONE);
        }

        adapter = new StudyMaterialAdapter(temppojoclass, StudyMaterialActivity.this, tv_no_records);
        lv_my_study_material.setAdapter(adapter);
    }


    public void myStudyMaterial() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("branch", preferences.getString("branch", ""));
        params.put("sem", preferences.getString("sem", ""));
        client.post(Urls.urlMyStudyMaterial, params, new JsonHttpResponseHandler() {

            public void onStart() {
                pBar.setVisibility(View.VISIBLE);
                super.onStart();
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    pBar.setVisibility(View.GONE);

                    JSONArray jarry = response.getJSONArray("getStudyMaterial");
                    if (jarry.isNull(0)) {
                        no_records_layout.setVisibility(View.VISIBLE);
                        tv_no_records.setText("No Study Material Available Yet");
                    } else {
                        no_records_layout.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < jarry.length(); i++) {
                        JSONObject jsonObject = jarry.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String doc = jsonObject.getString("doc");
                        String date = jsonObject.getString("date");

                        list.add(new PojoMyStudyMaterial(title, description, doc, date));
                    }

                    adapter = new StudyMaterialAdapter(list, (AppCompatActivity) StudyMaterialActivity.this, tv_no_records);
                    lv_my_study_material.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(StudyMaterialActivity.this, "could not connect", Toast.LENGTH_LONG).show();

            }

        });

    }
}
