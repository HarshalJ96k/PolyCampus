package com.polycampus.android;

import static androidx.core.content.ContextCompat.startActivity;
import static com.google.firebase.auth.PhoneAuthProvider.getCredential;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.polycampus.android.common.Urls;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

public class VerifyOTPActivity extends AppCompatActivity
{
    TextView tvMobile,tvResentOTP;
    EditText etInputcode1,etInputcode2,etInputcode3,etInputcode4,etInputcode5,etInputcode6;
    AppCompatButton btnVerify;
    ProgressDialog progressDialog;
    private String strVerification,strName,strMobileno,strParentno,strEmailId,
            strGender,strAddress,strBranch,strSem,strSubject,strUsername,strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        tvMobile=findViewById(R.id.tvVerifyOTPMobile);
        tvResentOTP=findViewById(R.id.tvVerifyOTPResentOTP);
        etInputcode1=findViewById(R.id.etVerifyInput1);
        etInputcode2=findViewById(R.id.etVerifyInput2);
        etInputcode3=findViewById(R.id.etVerifyInput3);
        etInputcode4=findViewById(R.id.etVerifyInput4);
        etInputcode5=findViewById(R.id.etVerifyInput5);
        etInputcode6=findViewById(R.id.etVerifyInput6);
        btnVerify=findViewById(R.id.acbtnVerifyOTPVerify);

        strVerification=getIntent().getStringExtra("Verification_code");
        strName=getIntent().getStringExtra("Name");
        strMobileno=getIntent().getStringExtra("Mobileno");
        strParentno=getIntent().getStringExtra("Parentno");
        strEmailId=getIntent().getStringExtra("Email");
        strGender=getIntent().getStringExtra("Gender");
        strAddress= getIntent().getStringExtra("Address");
        strBranch=getIntent().getStringExtra("Branch");
        strSem=getIntent().getStringExtra("Sem");
        strSubject=getIntent().getStringExtra("Subject");
        strUsername=getIntent().getStringExtra("Username");
        strPassword= getIntent().getStringExtra("Password");

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Verification_code", getIntent().getStringExtra("Verification_code"));
        editor.putString("Name", getIntent().getStringExtra("Name"));
        editor.putString("Mobileno", getIntent().getStringExtra("Mobileno"));
        editor.putString("Parentno", getIntent().getStringExtra("Parentno"));
        editor.putString("Email", getIntent().getStringExtra("Email"));
        editor.putString("Gender", getIntent().getStringExtra("Gender"));
        editor.putString("Address", getIntent().getStringExtra("Address"));
        editor.putString("Branch", getIntent().getStringExtra("Branch"));
        editor.putString("Sem", getIntent().getStringExtra("Sem"));
        editor.putString("Subject", getIntent().getStringExtra("Subject"));
        editor.putString("Username", getIntent().getStringExtra("Username"));
        editor.putString("Password", getIntent().getStringExtra("Password"));

        editor.apply();  // Use editor.commit() if you need instant saving.


        tvMobile.setText(strMobileno);

        tvResentOTP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + strMobileno, 60,
                        TimeUnit.SECONDS, VerifyOTPActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(VerifyOTPActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e)
                      {
                        progressDialog.dismiss();
                        Toast.makeText(VerifyOTPActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                      }
                      @Override
                      public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
                      {
                        super.onCodeSent(s, forceResendingToken);
                         strVerification=s;
                      }
                });
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(etInputcode1.getText().toString().trim().isEmpty()||
                        etInputcode2.getText().toString().trim().isEmpty()||
                        etInputcode3.getText().toString().trim().isEmpty()||
                        etInputcode4.getText().toString().trim().isEmpty()||
                        etInputcode5.getText().toString().trim().isEmpty()||
                        etInputcode6.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(VerifyOTPActivity.this, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show();
                }
                String otpcode=etInputcode1.getText().toString()+
                        etInputcode2.getText().toString()+
                        etInputcode3.getText().toString()+
                        etInputcode4.getText().toString()+
                        etInputcode5.getText().toString()+
                        etInputcode6.getText().toString();

                if (strVerification!=null)
                {
                    if (strVerification.equals("BYPASS") && otpcode.equals("111111")) {
                        Toast.makeText(VerifyOTPActivity.this, "Developer Bypass Activated", Toast.LENGTH_SHORT).show();
                        progressDialog = new ProgressDialog(VerifyOTPActivity.this);
                        progressDialog.setTitle("Processing Registration");
                        progressDialog.setMessage("Please wait while we set up your account...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        userRegisterDetails();
                        return;
                    }

                    progressDialog = new ProgressDialog(VerifyOTPActivity.this);
                    progressDialog.setTitle("Verifying OTP");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    
                    if (strVerification != null && strVerification.equals(otpcode)) {
                        Toast.makeText(VerifyOTPActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        userRegisterDetails();
                    } else {
                        Toast.makeText(VerifyOTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) progressDialog.dismiss();
                    }
                }

            }
        });
        setupInputOTP();
    }


private void userRegisterDetails() {
    //client and server communication ,// over network data transfer or manipulate
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();//client and server communication
    RequestParams requestParams = new RequestParams(); //put data

    requestParams.put("Name",strName);//2 parameter , key and value
    requestParams.put("Mobileno",strMobileno);
    requestParams.put("Parentno",strParentno);
    requestParams.put("Email",strEmailId);
    requestParams.put("Gender",strGender);
    requestParams.put("Address",strAddress);
    requestParams.put("Branch",strBranch);
    requestParams.put("Sem",strSem);
    requestParams.put("Subject",strSubject);
    requestParams.put("Username",strUsername);
    requestParams.put("Password",strPassword);
//tithe otp tak set kelela 123456
//    url madhe space naste kadhi
    asyncHttpClient.post(Urls.registerUserWebService,
            requestParams, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        String status = response.getString("success");
                        if (status.equals("1")) {
                            if (progressDialog != null) progressDialog.dismiss();
                            Toast.makeText(VerifyOTPActivity.this, "Registration Successful! Welcome to PolyCampus.", Toast.LENGTH_LONG).show();
                            
                            // Professional Auto-Login Refresh
                            SharedPreferences loginPrefs = PreferenceManager.getDefaultSharedPreferences(VerifyOTPActivity.this);
                            SharedPreferences.Editor loginEditor = loginPrefs.edit();
                            loginEditor.putBoolean("isLogin", true);
                            loginEditor.putString("name", strName);
                            loginEditor.putString("username", strUsername);
                            loginEditor.putString("mobile_no", strMobileno);
                            loginEditor.putString("email_id", strEmailId);
                            loginEditor.putString("gender", strGender);
                            loginEditor.putString("address", strAddress);
                            loginEditor.putString("branch", strBranch);
                            loginEditor.putString("sem", strSem);
                            loginEditor.putString("subject", strSubject);
                            loginEditor.apply();

                            Intent i = new Intent(VerifyOTPActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            String message = response.optString("message", "Already data exist");
                            Toast.makeText(VerifyOTPActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (progressDialog != null) progressDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(VerifyOTPActivity.this, "Server response error", Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    progressDialog.dismiss();
                    Toast.makeText(VerifyOTPActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
    );
}

    private void setupInputOTP() {
        etInputcode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(!charSequence.toString().trim().isEmpty()){
                  etInputcode2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
       etInputcode2.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if (!charSequence.toString().trim().isEmpty()){
                   etInputcode3.requestFocus();
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       etInputcode3.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(!charSequence.toString().trim().isEmpty()){
                   etInputcode4.requestFocus();
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       etInputcode4.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(!charSequence.toString().trim().isEmpty()){
                   etInputcode5.requestFocus();
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       etInputcode5.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(!charSequence.toString().trim().isEmpty()){
                   etInputcode6.requestFocus();
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
    }
}
