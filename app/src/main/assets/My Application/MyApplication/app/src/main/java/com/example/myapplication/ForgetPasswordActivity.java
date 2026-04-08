package com.polycampus.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class ForgetPasswordActivity extends AppCompatActivity {
    TextView tvMobile,tvResentOTP;
    EditText etInputcode1,etInputcode2,etInputcode3,etInputcode4,etInputcode5,etInputcode6;
    AppCompatButton btnVerify;
    ProgressDialog progressDialog;
    private String strVerification,strMobileno;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        strMobileno=getIntent().getStringExtra("Mobileno");

        tvMobile.setText(strMobileno);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etInputcode1.getText().toString().trim().isEmpty()||
                        etInputcode2.getText().toString().trim().isEmpty()||
                        etInputcode3.getText().toString().trim().isEmpty()||
                        etInputcode4.getText().toString().trim().isEmpty()||
                        etInputcode5.getText().toString().trim().isEmpty()||
                        etInputcode6.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(ForgetPasswordActivity.this, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show();
                }
                String otpcode=etInputcode1.getText().toString()+
                        etInputcode2.getText().toString()+
                        etInputcode3.getText().toString()+
                        etInputcode4.getText().toString()+
                        etInputcode5.getText().toString()+
                        etInputcode6.getText().toString();

                if (strVerification!=null)
                {
                    progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
                    progressDialog.setTitle("Verifying OTP");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthCredential  phoneAuthCredential= PhoneAuthProvider.
                            getCredential(strVerification,otpcode);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Intent i = new Intent(ForgetPasswordActivity.this,SetupNewPasswordActivity.class);
                                        i.putExtra("Mobileno",strMobileno);
                                        startActivity(i);

                                    }else {
                                        Toast.makeText(ForgetPasswordActivity.this, "OTP Verify", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });



        tvResentOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + strMobileno, 60,
                        TimeUnit.SECONDS, ForgetPasswordActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressDialog.dismiss();
                                Toast.makeText(ForgetPasswordActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {progressDialog.dismiss();
                                Toast.makeText(ForgetPasswordActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                strVerification=s;
                            }
                });
            }
        });



        setupInputOTP();

    }


    private void setupInputOTP() {
        etInputcode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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


