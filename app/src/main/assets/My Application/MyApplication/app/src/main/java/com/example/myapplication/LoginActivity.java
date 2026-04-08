package com.polycampus.android;

import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.common.NetworkChangeListener;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    ImageView ivLogo;
    TextView tvTitle,tvNewuser ,tvForgetPassword;
    EditText etUsername,etPassword;
    Button btnLogin;
    private boolean isPasswordVisible= false;
   ImageView tvShowHide;

    ProgressDialog progressDialog;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ivLogo=findViewById(R.id.ivLoginLogo);
        tvTitle=findViewById(R.id.tvLoginTitle);
        tvNewuser=findViewById(R.id.tvLoginNewUser);
        etUsername=findViewById(R.id.etLoginUsername);
        etPassword=findViewById(R.id.etLoginPassword);
        btnLogin=findViewById(R.id.btnLoginLogin);
        tvForgetPassword=findViewById(R.id.tvforgetPassword);


//        tvShowHide=findViewById(R.id.tvshowhidePassword);


//        tvShowHide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isPasswordVisible)
//                {
//                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    tvShowHide.setImageResource(R.drawable.icon_hidepassword);
//
//                }else{
//                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    tvShowHide.setImageResource(R.drawable.icon_showpassword);
//                }
//                isPasswordVisible=!isPasswordVisible;
//                etPassword.setSelection(etPassword.getText().length());
//            }
//        });


    btnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (etUsername.getText().toString().isEmpty()) {
                etUsername.setError("please Enter your Username");
            } else if (etPassword.getText().toString().isEmpty()) {
                etPassword.setError("Please enter your Password");
            } else if (etUsername.getText().toString().length() < 8) {
                etUsername.setError(("Username must be greater than 8"));
            } else if (etPassword.getText().toString().length() < 8) {
                etPassword.setError("Password must be greater than 8");
            } else if (!etUsername.getText().toString().matches("^(?=.*[A-Z]).+$")) {
                etUsername.setError("Used atleast 1 uppercase letter");
            } else if (!etUsername.getText().toString().matches("^(?=.*[a-z]).+$")) {
                etUsername.setError("Used atleast 1 lowercase letter");
            } else if (!etUsername.getText().toString().matches("^(?=.*[!@#$%^&*_]).+$")) {
                etUsername.setError("Used atleast 1 special symbol");
            } else {

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Please wait");
                progressDialog.setMessage("Login under process");
                progressDialog.show();
                userLogin();

            }
        }

    });

    tvForgetPassword.setOnClickListener(new  View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(LoginActivity.this,ConfirmRegisterMobilenoActivity.class);
            startActivity(i);
        }
    });
    tvNewuser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeListener);
    }
    private void userLogin() {
        //Client and Server Communication over network data transfer or manipulate
        AsyncHttpClient client = new AsyncHttpClient(); //Client and Server Communication
        RequestParams params = new RequestParams();

        params.put("Username", etUsername.getText().toString());
        params.put("Password", etPassword.getText().toString());

        client.post(Urls.loginwebServiceAdress, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            String status = response.getString("success");
                            if (status.equals("1")) {
                                Toast.makeText(LoginActivity.this, "Login Successfully Done", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

    }
}

