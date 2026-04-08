package com.polycampus.android;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.common.Urls;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class ApplyLeaveActivity extends AppCompatActivity {

    TextInputEditText etName, etBranchSem, etFrom, etTo, etReason;
    MaterialButton btnSubmit;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_leave);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        etName = findViewById(R.id.etLeaveStudentName);
        etBranchSem = findViewById(R.id.etLeaveBranchSem);
        etFrom = findViewById(R.id.etLeaveFromDate);
        etTo = findViewById(R.id.etLeaveToDate);
        etReason = findViewById(R.id.etLeaveReason);
        btnSubmit = findViewById(R.id.btnSubmitLeave);

        etName.setText(preferences.getString("name", ""));
        etBranchSem.setText(preferences.getString("branch", "") + " - " + preferences.getString("sem", ""));

        etFrom.setOnClickListener(v -> showDatePicker(etFrom));
        etTo.setOnClickListener(v -> showDatePicker(etTo));

        btnSubmit.setOnClickListener(v -> {
            if (validate()) { submitLeave(); }
        });
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            target.setText(day + "/" + (month + 1) + "/" + year);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validate() {
        if (etReason.getText().toString().isEmpty() || etFrom.getText().toString().isEmpty() || etTo.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill dates and reason", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submitLeave() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username", preferences.getString("username", ""));
        params.put("name", etName.getText().toString());
        params.put("branch", preferences.getString("branch", ""));
        params.put("sem", preferences.getString("sem", ""));
        params.put("reason", etReason.getText().toString());
        params.put("from_date", etFrom.getText().toString());
        params.put("to_date", etTo.getText().toString());

        client.post(Urls.urlApplyLeave, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getString("status").equals("success")) {
                        Toast.makeText(ApplyLeaveActivity.this, "Application Submitted", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ApplyLeaveActivity.this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            }
        });
    }
}
