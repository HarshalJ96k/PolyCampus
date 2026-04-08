package com.polycampus.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.mime.Header;


public class BonafiedActivity extends AppCompatActivity
{
    EditText etName, etbonafiedReason, etAdmissionreceipt, etbonafiedApplication, etAcademicYear, etSemester, etBranch;
    Button submitBonafiedDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonafied);

        setupToolbar();

        etName=findViewById(R.id.etBonafiedFullName);
        etbonafiedReason=findViewById(R.id.etBonafiedReason);
        etAdmissionreceipt=findViewById(R.id.etBonafiedAdmissionReceipt);
        etbonafiedApplication=findViewById(R.id.etBonafiedApplication);
        etAcademicYear=findViewById(R.id.etBonafiedAcademic_year);
        etSemester=findViewById(R.id.etBonafiedSemester);
        etBranch=findViewById(R.id.etBonafiedBranch);
        submitBonafiedDetail=findViewById(R.id.btnBonafiedSubmit);

        submitBonafiedDetail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (validateForm()) {
                    sendBonafiedDetails();
                }
            }
        });
    }

    private void setupToolbar() {
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbarBonafied);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private boolean validateForm() {
        if (etName.getText().toString().isEmpty() || etbonafiedReason.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill required details", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendBonafiedDetails()
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("name", etName.getText().toString());
        requestParams.put("reason", etbonafiedReason.getText().toString());
        requestParams.put("admission_receipt", etAdmissionreceipt.getText().toString());
        requestParams.put("application", etbonafiedApplication.getText().toString());
        requestParams.put("academic_year", etAcademicYear.getText().toString());
        requestParams.put("semester", etSemester.getText().toString());
        requestParams.put("branch",etBranch.getText().toString());

        asyncHttpClient.post(Urls.bonafiedDetails, requestParams, new JsonHttpResponseHandler()
                {
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response)
                    {
                        try {
                            String status = response.getString("success");
                            if (status.equals("1")) {
                                Toast.makeText(BonafiedActivity.this, "Request Sent Successfully", Toast.LENGTH_LONG).show();
                                finish(); // Go back to student section
                            } else {
                                Toast.makeText(BonafiedActivity.this, "Request already exists", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(BonafiedActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
