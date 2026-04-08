package com.polycampus.android.TeacherApp;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginTeacherActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 0000;
    TextInputEditText tie_login_username,tie_login_password;
    ImageView img_login_logo;;
    Button btn_login_login,btn_login_register;
    ActivityOptions options;
    ProgressDialog progressDialog;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.polycampus.android.common.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_teacher);
        setTitle("Admin Login Page");

        preferences = PreferenceManager.getDefaultSharedPreferences(LoginTeacherActivity.this);
        editor = preferences.edit();

        if (preferences.getBoolean("isTeacherLogin",false))
        {
            Intent intent = new Intent(LoginTeacherActivity.this, HomeTeacherActivity.class);
            startActivity(intent);
        }

        img_login_logo = findViewById(R.id.img_login_logo);
        tie_login_username = findViewById(R.id.tie_login_username);
        tie_login_password = findViewById(R.id.tie_login_password);
        btn_login_login = findViewById(R.id.btn_login_login);

        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(LoginTeacherActivity.this);
                progressDialog.setTitle("Login Teacher");
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                loginTeacher();
            }
        });

//        btn_login_register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(LoginTeacherActivity.this,RegistrationActivity.class);
//
//                        Pair[] pairs = new Pair[2];
//                        pairs[0] = new Pair<View,String>(img_login_logo,"splash_logo");
////                    pairs[0] = new Pair<View,String>(splash_app_title,"splash_title");
//
//
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                            options = ActivityOptions.makeSceneTransitionAnimation(LoginTeacherActivity.this,pairs[0]);
//                        }
//                        startActivity(intent,options.toBundle());
//
//                    }
//                },SPLASH_SCREEN);
//            }
//        });
    }

    private void loginTeacher() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.urlLoginTeacher,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            String success = obj.getString("success");
                            
                            if (success.equals("1"))
                            {
                                Intent i = new Intent(LoginTeacherActivity.this, HomeTeacherActivity.class);
                                editor.putBoolean("isTeacherLogin",true).commit();
                                editor.putString("username",tie_login_username.getText().toString()).commit();
                                Toast.makeText(LoginTeacherActivity.this,"Login Successfully Done",Toast.LENGTH_SHORT).show();
                                startActivity(i);
                                finish();
                            }else {
                                String message = obj.optString("message", "Invalid teacher credentials");
                                Toast.makeText(LoginTeacherActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginTeacherActivity.this, "Data parsing error", Toast.LENGTH_SHORT).show();
                        } finally {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginTeacherActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", tie_login_username.getText().toString());
                params.put("password", tie_login_password.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        RequestQueue requestQueue = Volley.newRequestQueue(LoginTeacherActivity.this);
        requestQueue.add(stringRequest);

    }
}
