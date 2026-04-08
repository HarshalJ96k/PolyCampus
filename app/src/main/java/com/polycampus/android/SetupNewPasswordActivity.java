package com.polycampus.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SetupNewPasswordActivity extends AppCompatActivity {
    String strMobileno;
    EditText etNewpassword,etCnfirmpassword;
    AppCompatButton btnSetupPassword;
    ProgressDialog progressDialog;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup_new_password);
        etNewpassword = findViewById(R.id.etSetupNewPasswordMobileno);
        etCnfirmpassword = findViewById(R.id.etSetupNewPassword);
        btnSetupPassword= findViewById(R.id.acbtnConfirmRegisterVerify);
        strMobileno = getIntent().getStringExtra("Mobileno");
        Toast.makeText(this, strMobileno, Toast.LENGTH_SHORT).show();

        btnSetupPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNewpassword.getText().toString().isEmpty() || etCnfirmpassword.getText().toString().isEmpty()) {
                    Toast.makeText(SetupNewPasswordActivity.this, "Please Enter New or Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!etNewpassword.getText().toString().equals(etCnfirmpassword.getText().toString())) {
                    etCnfirmpassword.setError("Password did not match");
                } else {
                    progressDialog = new ProgressDialog(SetupNewPasswordActivity.this);
                    progressDialog.setTitle("Updating Password");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    forgetPassword();
                }
            }
        });
    }
    private void forgetPassword() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("Mobileno", strMobileno);
        params.put("Password", etNewpassword.getText().toString());
        client.post(Urls.forgetPasswordWebService, params,
                new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                super.onSuccess(statusCode, headers, response);
                try {
                    String status = response.getString("success");
                    if (status.equals("1")) {
                        Toast.makeText(SetupNewPasswordActivity.this, "New Password Set up Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SetupNewPasswordActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(SetupNewPasswordActivity.this, "Password not Changed", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e)
                {
                    throw new RuntimeException(e);
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(SetupNewPasswordActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
