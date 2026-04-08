package com.polycampus.android;

import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.polycampus.android.TeacherApp.HomeTeacherActivity;

import com.polycampus.android.TeacherApp.LoginTeacherActivity;
import com.polycampus.android.common.NetworkChangeListener;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    TextView tvTitle, tvNewuser, tvForgetPassword;
    EditText etUsername, etPassword;
    Button btnLogin;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ProgressDialog progressDialog;
    com.google.android.material.button.MaterialButtonToggleGroup roleToggleGroup;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        tvTitle = findViewById(R.id.tvLoginTitle);
        tvNewuser = findViewById(R.id.tvLoginNewUser);
        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLoginLogin);
        tvForgetPassword = findViewById(R.id.tvforgetPassword);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor = sharedPreferences.edit();
        
        roleToggleGroup = findViewById(R.id.roleToggleGroup);

        if (sharedPreferences.getBoolean("isLogin", false)) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (sharedPreferences.getBoolean("isAdminLogin", false)) {
            Intent intent = new Intent(LoginActivity.this, com.polycampus.android.AdminApp.HomeAdminActivity.class);
            startActivity(intent);
            finish();
        } else if (sharedPreferences.getBoolean("isTeacherLogin", false)) {
            Intent intent = new Intent(LoginActivity.this, HomeTeacherActivity.class);
            startActivity(intent);
            finish();
        }
        // tvShowHide=findViewById(R.id.tvshowhidePassword);

        // tvShowHide.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View view)
        // {
        // if (isPasswordVisible)
        // {
        // etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        // tvShowHide.setImageResource(R.drawable.icon_hidepassword);
        //
        // }else
        // {
        // etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        // tvShowHide.setImageResource(R.drawable.icon_showpassword);
        // }
        // isPasswordVisible=!isPasswordVisible;
        // etPassword.setSelection(etPassword.getText().length());
        // }
        // });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUsername.getText().toString().isEmpty()) {
                    etUsername.setError("please Enter your Username");
                } else if (etPassword.getText().toString().isEmpty()) {
                    etPassword.setError("Please enter your Password");
                } else if (etUsername.getText().toString().length() < 1) {
                    etUsername.setError(("Please enter a valid Username"));
                } else if (etPassword.getText().toString().length() < 4) {
                    etPassword.setError("Password must be at least 4 characters");
                } else {

                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("Login under process");
                    progressDialog.show();

                    userLogin();

                }
            }

        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ConfirmRegisterMobilenoActivity.class);
                startActivity(i);
            }
        });
        tvNewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        // Security: Developer bypass removed for production
        // (Long-press was granting admin access without credentials)


    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeListener);
    }

    private void userLogin() {
        int selectedId = roleToggleGroup.getCheckedButtonId();
        boolean isTeacher = selectedId == R.id.btnRoleTeacher;
        boolean isAdmin = selectedId == R.id.btnRoleAdmin;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        if (isTeacher || isAdmin) {
            params.put("username", etUsername.getText().toString());
            params.put("password", etPassword.getText().toString());
        } else {
            params.put("Username", etUsername.getText().toString());
            params.put("Password", etPassword.getText().toString());
        }

        String loginUrl;
        if (isTeacher || isAdmin) {
            loginUrl = Urls.urlLoginTeacher;
        } else {
            loginUrl = Urls.loginWebService;
        }

        client.post(loginUrl, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            // 🛡️ API Sync: Professional Response Parsing (v4.0)
                            // The PHP backend now wraps user details in a 'data' object
                            int successCode = response.optInt("success", 0);

                            if (successCode == 1) {
                                JSONObject data = response.optJSONObject("data");
                                if (data == null) {
                                    Toast.makeText(LoginActivity.this, "Server Protocol Error: Missing payload", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    return;
                                }

                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                if (isTeacher || isAdmin) {
                                    // Professional HOD/Teacher Login Handling
                                    String userrole = data.optString("userrole", "Admin");
                                    String branch = data.optString("branch", "");
                                    
                                    if (userrole.equalsIgnoreCase("admin") && branch.isEmpty()) {
                                        Toast.makeText(LoginActivity.this, "Security Breach: Missing Department Profile", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        return;
                                    }

                                    editor.putString("username", etUsername.getText().toString());
                                    editor.putString("name", data.optString("name", ""));
                                    editor.putString("userrole", userrole);
                                    
                                    Intent intent;
                                    if (userrole.equalsIgnoreCase("admin")) {
                                        editor.putBoolean("isAdminLogin", true);
                                        editor.putString("branch", branch);
                                        intent = new Intent(LoginActivity.this, com.polycampus.android.AdminApp.HomeAdminActivity.class);
                                        Toast.makeText(LoginActivity.this, "Logged in as " + branch + " HOD", Toast.LENGTH_SHORT).show();
                                    } else {
                                        editor.putBoolean("isTeacherLogin", true);
                                        editor.putString("teacher_id", data.optString("id", ""));
                                        editor.putString("subjects", data.optString("subjects", ""));
                                        intent = new Intent(LoginActivity.this, HomeTeacherActivity.class);
                                        Toast.makeText(LoginActivity.this, "Teacher Login Successful", Toast.LENGTH_SHORT).show();
                                    }
                                    
                                    editor.commit();
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Student Login / Global Admin Handling
                                    String userrole = data.optString("userrole", "Student");
                                    
                                    editor.putString("name", data.optString("name", ""));
                                    editor.putString("email_id", data.optString("email", data.optString("email_id", "")));
                                    editor.putString("gender", data.optString("gender", ""));
                                    editor.putString("address", data.optString("address", ""));
                                    editor.putString("userrole", userrole);
                                    editor.putString("username", etUsername.getText().toString());

                                    Intent intent;
                                    if (userrole.equalsIgnoreCase("Admin")) {
                                        editor.putBoolean("isAdminLogin", true);
                                        editor.putString("branch", data.optString("branch", ""));
                                        intent = new Intent(LoginActivity.this, com.polycampus.android.AdminApp.HomeAdminActivity.class);
                                    } else {
                                        editor.putBoolean("isLogin", true);
                                        editor.putString("student_id", data.optString("id", ""));
                                        editor.putString("mobile_no", data.optString("mobile_no", ""));
                                        editor.putString("parent_mobile_no", data.optString("parent_mobile_no", ""));
                                        editor.putString("branch", data.optString("branch", ""));
                                        editor.putString("sem", data.optString("sem", ""));
                                        editor.putString("subject", data.optString("subject", ""));
                                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    }
                                    editor.commit();
                                    startActivity(intent);
                                    finish();
                                }
                                progressDialog.dismiss();
                            } else {
                                String message = response.optString("message", "User does not exist or invalid credentials");
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Response Parsing Error", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                            JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

    }
}
