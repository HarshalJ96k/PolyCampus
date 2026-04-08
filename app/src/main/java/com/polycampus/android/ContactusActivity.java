package com.polycampus.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactusActivity extends AppCompatActivity
{

    TextView tvCollegeWebsite,tvCollegePhone1,tvCollegePhone2,tvCollegeEmail1,tvCollegeEmail2,tvCollegeLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        View itemWebsite = findViewById(R.id.itemWebsite);
        tvCollegeWebsite = itemWebsite.findViewById(R.id.tvValue);
        ((android.widget.TextView)itemWebsite.findViewById(R.id.tvLabel)).setText("Official Website");
        ((android.widget.ImageView)itemWebsite.findViewById(R.id.ivIcon)).setImageResource(R.drawable.baseline_public_24);
        tvCollegeWebsite.setText("https://www.gpmzr.ac.in/");

        View itemEmail1 = findViewById(R.id.itemEmail1);
        tvCollegeEmail1 = itemEmail1.findViewById(R.id.tvValue);
        ((android.widget.TextView)itemEmail1.findViewById(R.id.tvLabel)).setText("Principal Email");
        ((android.widget.ImageView)itemEmail1.findViewById(R.id.ivIcon)).setImageResource(R.drawable.baseline_email_24);
        tvCollegeEmail1.setText("principal.gpmurtijapur@dtemaharashtra.gov.in");

        View itemEmail2 = findViewById(R.id.itemEmail2);
        tvCollegeEmail2 = itemEmail2.findViewById(R.id.tvValue);
        ((android.widget.TextView)itemEmail2.findViewById(R.id.tvLabel)).setText("Office Email");
        ((android.widget.ImageView)itemEmail2.findViewById(R.id.ivIcon)).setImageResource(R.drawable.baseline_email_24);
        tvCollegeEmail2.setText("office.gpmurtijapur@dtemaharashtra.gov.in");

        View itemPhone1 = findViewById(R.id.itemPhone1);
        tvCollegePhone1 = itemPhone1.findViewById(R.id.tvValue);
        ((android.widget.TextView)itemPhone1.findViewById(R.id.tvLabel)).setText("Office Contact");
        ((android.widget.ImageView)itemPhone1.findViewById(R.id.ivIcon)).setImageResource(R.drawable.baseline_phone_24);
        tvCollegePhone1.setText("07256-299391");

        View itemPhone2 = findViewById(R.id.itemPhone2);
        tvCollegePhone2 = itemPhone2.findViewById(R.id.tvValue);
        ((android.widget.TextView)itemPhone2.findViewById(R.id.tvLabel)).setText("Mobile Number");
        ((android.widget.ImageView)itemPhone2.findViewById(R.id.ivIcon)).setImageResource(R.drawable.baseline_phone_24);
        tvCollegePhone2.setText("9423689391");

        tvCollegeLocation=findViewById(R.id.tvContactUslocationLink);

        tvCollegeWebsite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.gpmzr.ac.in/"));
                startActivity(intent);
            }
        });
        tvCollegeEmail1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:principal.gpmurtijapur@dtemaharashtra.gov.in"));
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
        tvCollegeEmail2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:office.gpmurtijapur@dtemaharashtra.gov.in"));
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        tvCollegePhone1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+07256-299391"));
                startActivity(intent);
            }
        });
        tvCollegePhone2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+919423689391"));
                startActivity(intent);
            }
        });

        tvCollegeLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Uri locationUri = Uri.parse("geo:0,0?q=Government Polytechnic Murtijapur, Akola, Maharashtra");
                Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
                intent.setPackage("com.google.android.apps.maps");

                if (intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivity(intent);
                }
                else
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=Government+Polytechnic+Murtijapur,+Akola,+Maharashtra")));
                }
            }
        });
    }
}
