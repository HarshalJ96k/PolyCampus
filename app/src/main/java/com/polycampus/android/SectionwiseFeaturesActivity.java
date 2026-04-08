package com.polycampus.android;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class SectionwiseFeaturesActivity extends AppCompatActivity
{

    SearchView svFeature;
    ListView lvListFeature;
    TextView tvNoFeatureAvailable;
    String strFeature;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sectionwise_features);

        svFeature=findViewById(R.id.svsectionwisefeatues);
        lvListFeature=findViewById(R.id.lvSectionWiseFeatures);
        tvNoFeatureAvailable=findViewById(R.id.tvSectionWiseFeaturesNoFeature);

        getSectionWiseFeatures();

        strFeature =getIntent().getStringExtra("categoryname");
 
    }


    private void getSectionWiseFeatures()
    {
        AsyncHttpClient client =new AsyncHttpClient();
        RequestParams params =new RequestParams();
//        params.put();
    }
}
