package com.polycampus.android;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.polycampus.android.common.Urls;

public class ImageDetails extends AppCompatActivity {
    String image, title, date, time, dis;
    ImageView imageView;
    TextView tvTitle, tvDate, tvTime, tvDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // Get data from intent
        image = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        dis = getIntent().getStringExtra("dis");

        // Initialize views
        imageView = findViewById(R.id.ivImageU);
        tvTitle = findViewById(R.id.tvTitleU);
        tvDate = findViewById(R.id.tvDateU);
        tvTime = findViewById(R.id.tvTimeU);
        tvDis = findViewById(R.id.tvDisU);

        // Set data
        tvTitle.setText(title);
        tvDate.setText(date);
        tvTime.setText(time);
        tvDis.setText(dis);

        Glide.with(ImageDetails.this)
                .load(Urls.webServiceAddress + "image/" + image)
                .skipMemoryCache(true)
                .error(R.drawable.sticky_note)
                .into(imageView);
    }
}
