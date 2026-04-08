package com.polycampus.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.common.Urls;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.entity.mime.Header;

public class RegistrationActivity extends AppCompatActivity {
    EditText etName, etMobileNo, etEmailID, etUsername, etPassword, etbranch, etYear, etGender, etAdress, etParentno;
    Button btnRegister;
TextView tvSignin;
ImageView imageView;
    private boolean isPasswordVisible= false;

    ProgressDialog progressDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etName = findViewById(R.id.etRegisterName);
        etMobileNo = findViewById(R.id.etRegisterMobileNo);
        etEmailID = findViewById(R.id.etRegisterEmail);
        etUsername = findViewById(R.id.etRegisterUserName);
        etPassword = findViewById(R.id.etRegisterPassword);
        etbranch = findViewById(R.id.etRegisterBranch);
        etYear = findViewById(R.id.etRegisterYear);
        etGender = findViewById(R.id.etRegisterGender);
        etAdress = findViewById(R.id.etRegisterAdress);
        etParentno = findViewById(R.id.etRegisterParentno);
        btnRegister = findViewById(R.id.btnRegisterregister);
        imageView= findViewById(R.id.ivHidePassword);
        tvSignin=findViewById(R.id.tvAlreadyUser);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible)
                {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                   imageView.setImageResource(R.drawable.icon_hidepassword);

                }else{
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView.setImageResource(R.drawable.icon_showpassword);
                }
                isPasswordVisible=!isPasswordVisible;
                etPassword.setSelection(etPassword.getText().length());
            }
        });


        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().isEmpty()) {
                    etName.setError("Please enter your name");
                } else if (etMobileNo.getText().toString().isEmpty()) {
                    etMobileNo.setError("Please enter mobile number");
                } else if (etMobileNo.getText().toString().length() != 10) {
                    etMobileNo.setError("Mobile number must be 10 digit");
                } else if (etEmailID.getText().toString().isEmpty()) {
                    etEmailID.setError("Please enter your email Id");
                } else if (!etEmailID.getText().toString().contains("@") ||
                        !etEmailID.getText().toString().contains(".com")) {
                    etEmailID.setError("Invalid Email ID");
                } else if (etUsername.getText().toString().isEmpty()) {
                    etUsername.setError("Must be enter your enrollment no");
                } else if (etGender.getText().toString().isEmpty()) {
                    etGender.setError("Please enter your gender");
                } else if (etAdress.getText().toString().isEmpty()) {
                    etAdress.setError("Please enter your Adress");
                } else if (etParentno.getText().toString().isEmpty()) {
                    etParentno.setError("Please enter your parentno");
                } else if (etbranch.getText().toString().isEmpty()) {
                    etbranch.setError("Please Enter your branch");
                } else if (etYear.getText().toString().isEmpty()) {
                    etbranch.setError("Please Enter your Year");
                } else {

                    progressDialog = new ProgressDialog(RegistrationActivity.this);
                    progressDialog.setTitle("Please wait...");
                    progressDialog.setMessage("Registration is in process");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + etMobileNo.getText().toString(), 60,
                            TimeUnit.SECONDS, RegistrationActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegistrationActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegistrationActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    Intent i = new Intent(RegistrationActivity.this, VerifyOTPActivity.class);
                                    i.putExtra("Verification_code", s);
                                    i.putExtra("Name", etName.getText().toString());
                                    i.putExtra("Mobileno", etMobileNo.getText().toString());
                                    i.putExtra("Parentno", etParentno.getText().toString());
                                    i.putExtra("Email", etEmailID.getText().toString());
                                    i.putExtra("Gender", etGender.getText().toString());
                                    i.putExtra("Address", etAdress.getText().toString());
                                    i.putExtra("Branch", etbranch.getText().toString());
                                    i.putExtra("Year", etYear.getText().toString());
                                    i.putExtra("Username", etUsername.getText().toString());
                                    i.putExtra("Password", etPassword.getText().toString());
                                    startActivity(i);
                                }
                    });
                    // userRegisterDetails();
                }
            }
        });
    }

    private void userRegisterDetails() {
        //client and server communication ,// over network data transfer or manipulate
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();//client and server communication
        RequestParams requestParams = new RequestParams(); //put data
        requestParams.put("Name", etName.getText().toString());//2 parameter , key and value
        requestParams.put("Mobileno", etMobileNo.getText().toString());
        requestParams.put("Parentno", etParentno.getText().toString());
        requestParams.put("Email", etEmailID.getText().toString());
        requestParams.put("Gender", etGender.getText().toString());
        requestParams.put("Address", etAdress.getText().toString());
        requestParams.put("Branch", etbranch.getText().toString());
        requestParams.put("Year", etYear.getText().toString());
        requestParams.put("Username", etUsername.getText().toString());
        requestParams.put("Password", etPassword.getText().toString());

        asyncHttpClient.post(Urls.registerUserWebService, requestParams, new JsonHttpResponseHandler() {

                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        try {
                            String status = response.getString("success");
                            if (status.equals("1")) {
                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(i);
                                progressDialog.dismiss();


                            } else {
                                Toast.makeText(RegistrationActivity.this, "Already data exist", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }


                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                        Toast.makeText(RegistrationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }
        );
    }
}



