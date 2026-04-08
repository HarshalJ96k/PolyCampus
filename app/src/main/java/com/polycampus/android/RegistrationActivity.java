package com.polycampus.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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

import com.google.android.material.textfield.TextInputLayout;
import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText etName, etMobileNo, etEmailID, etUsername, etPassword,
            etAdress, etParentno;
    com.google.android.material.textfield.MaterialAutoCompleteTextView etGender, etBranch, etSem;
    TextInputLayout tilName, tilMobile, tilParent, tilEmail, tilGender, tilAddress, tilBranch, tilSem, tilUser, tilPass;

    ArrayAdapter<CharSequence> adapter;
    Button btnRegister;
    TextView tvSignin;
    ImageView imageView;
    ListView lvgender;
    private boolean isPasswordVisible = false;

    ProgressDialog progressDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etName = findViewById(R.id.etRegisterName);
        etMobileNo = findViewById(R.id.etRegisterMobileno);
        etEmailID = findViewById(R.id.etRegisterEmail);
        etGender = findViewById(R.id.etRegisterGender);
        
        String[] genders = {"Male", "Female", "Others"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        etGender.setAdapter(genderAdapter);
        
        etUsername = findViewById(R.id.etRegisterUserName);
        etPassword = findViewById(R.id.etRegisterPassword);
        etAdress = findViewById(R.id.etRegisterAddress);
        etParentno = findViewById(R.id.etRegisterParentno);
        
        tilName = findViewById(R.id.tilRegisterName);
        tilMobile = findViewById(R.id.tilRegisterMobileno);
        tilParent = findViewById(R.id.tilRegisterParentno);
        tilEmail = findViewById(R.id.tilRegisterEmail);
        tilGender = findViewById(R.id.tilRegisterGender);
        tilAddress = findViewById(R.id.tilRegisterAddress);
        tilBranch = findViewById(R.id.tilRegisterBranch);
        tilSem = findViewById(R.id.tilRegisterSem);
        tilUser = findViewById(R.id.tilRegisterUserName);
        tilPass = findViewById(R.id.tilRegisterPassword);
        
        btnRegister = findViewById(R.id.btnRegisterregister);
        //  imageView= findViewById(R.id.ivHidePassword);
        tvSignin = findViewById(R.id.tvAlreadyUser);

        etBranch = findViewById(R.id.etRegisterBranch);
        etSem = findViewById(R.id.etRegisterSem);

        String[] branches = getResources().getStringArray(R.array.branch);
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, branches);
        etBranch.setAdapter(branchAdapter);

        String[] semesters = getResources().getStringArray(R.array.semester);
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, semesters);
        etSem.setAdapter(semesterAdapter);

        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearErrors();
                if (etName.getText().toString().isEmpty()) {
                    tilName.setError("Please enter your name");
                } else if (etMobileNo.getText().toString().isEmpty()) {
                    tilMobile.setError("Please enter mobile number");
                } else if (etMobileNo.getText().toString().length() != 10) {
                    tilMobile.setError("Mobile number must be 10 digit");
                } else if (etEmailID.getText().toString().isEmpty()) {
                    tilEmail.setError("Please enter your email Id");
                } else if (!etEmailID.getText().toString().contains("@") ||
                        !etEmailID.getText().toString().contains(".com")) {
                    tilEmail.setError("Invalid Email ID");
                } else if (etUsername.getText().toString().isEmpty()) {
                    tilUser.setError("Must be enter your enrollment no");
                } else if (etGender.getText().toString().isEmpty()) {
                    tilGender.setError("Please select gender");
                } else if (etAdress.getText().toString().isEmpty()) {
                    tilAddress.setError("Please enter your Address");
                } else if (etParentno.getText().toString().isEmpty()) {
                    tilParent.setError("Please enter your parent no");
                } else if (etBranch.getText().toString().isEmpty() || etBranch.getText().toString().equals("Select Your Branch")) {
                    tilBranch.setError("Please select your branch");
                } else if (etSem.getText().toString().isEmpty() || etSem.getText().toString().equals("Select Your Sem")) {
                    tilSem.setError("Please select your semester");
                } else if (etPassword.getText().toString().isEmpty()) {
                    tilPass.setError("Please enter your password");
                } else if (etPassword.getText().toString().length() < 8) {
                    tilPass.setError("Password must be at least 8 characters");
                } else {
                    progressDialog = new ProgressDialog(RegistrationActivity.this);
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setMessage("Sending Verification Code");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();

                    // Developer Bypass - Useful when Firebase Billing is not enabled
                    if (etMobileNo.getText().toString().equals("1234567890")) {
                        progressDialog.dismiss();
                        Intent i = new Intent(RegistrationActivity.this, VerifyOTPActivity.class);
                        i.putExtra("Verification_code", "BYPASS");
                        i.putExtra("Name", etName.getText().toString());
                        i.putExtra("Mobileno", etMobileNo.getText().toString());
                        i.putExtra("Parentno", etParentno.getText().toString());
                        i.putExtra("Email", etEmailID.getText().toString());
                        i.putExtra("Gender", etGender.getText().toString());
                        i.putExtra("Address", etAdress.getText().toString());
                        i.putExtra("Branch", etBranch.getText().toString());
                        i.putExtra("Sem", etSem.getText().toString());
                        i.putExtra("Subject", "");
                        i.putExtra("Username", etUsername.getText().toString());
                        i.putExtra("Password", etPassword.getText().toString());
                        startActivity(i);
                        return;
                    }

                    final String generatedOtp = String.format("%06d", new java.util.Random().nextInt(1000000));
                    
                    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("Email", etEmailID.getText().toString());
                    requestParams.put("otp", generatedOtp);
                    
                    asyncHttpClient.post(Urls.sendOtpEmail,   requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            try {
                                if (response.has("success") && response.getString("success").equals("1")) {
                                    Toast.makeText(RegistrationActivity.this, "OTP Sent to Email", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(RegistrationActivity.this, VerifyOTPActivity.class);
                                    i.putExtra("Verification_code", generatedOtp);
                                    i.putExtra("Name", etName.getText().toString());
                                    i.putExtra("Mobileno", etMobileNo.getText().toString());
                                    i.putExtra("Parentno", etParentno.getText().toString());
                                    i.putExtra("Email", etEmailID.getText().toString());
                                    i.putExtra("Gender", etGender.getText().toString());
                                    i.putExtra("Address", etAdress.getText().toString());
                                    i.putExtra("Branch", etBranch.getText().toString());
                                    i.putExtra("Sem", etSem.getText().toString());
                                    i.putExtra("Subject", "");
                                    i.putExtra("Username", etUsername.getText().toString());
                                    i.putExtra("Password", etPassword.getText().toString());
                                    startActivity(i);
                                } else {
                                    String msg = response.has("message") ? response.getString("message") : "Failed to send email";
                                    Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(RegistrationActivity.this, "JSON Parse Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(RegistrationActivity.this, "Server Error: " + statusCode, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            android.util.Log.e("API_ERROR", "Status: " + statusCode + " Body: " + responseString);
                            Toast.makeText(RegistrationActivity.this, "Error " + statusCode + ": Check Server Path", Toast.LENGTH_LONG).show();
                        }
                    });
//                    userRegisterDetails();
                }
            }

        });

    }

    private void clearErrors() {
        tilName.setError(null);
        tilMobile.setError(null);
        tilParent.setError(null);
        tilEmail.setError(null);
        tilGender.setError(null);
        tilAddress.setError(null);
        tilBranch.setError(null);
        tilSem.setError(null);
        tilUser.setError(null);
        tilPass.setError(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter
//                    .createFromResource(this, R.array.VIII_Standard,
//                            android.R.layout.simple_spinner_dropdown_item);
//            spinner_student_subject.setAdapter(adapter);
//        } else if (position == 9) {
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter
//                    .createFromResource(this, R.array.IX_Standard,
//                            android.R.layout.simple_spinner_dropdown_item);
//
//            spinner_student_subject.setAdapter(adapter);
//        } else if (position == 10) {
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter
//                    .createFromResource(this, R.array.X_Standard,
//                            android.R.layout.simple_spinner_dropdown_item);
//            spinner_student_subject.setAdapter(adapter);
//        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    private void userRegisterDetails() {
//        //client and server communication ,// over network data transfer or manipulate
//        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();//client and server communication
//        RequestParams requestParams = new RequestParams(); //put data
//        requestParams.put("Name", etName.getText().toString());//2 parameter , key and value
//        requestParams.put("Mobileno", etMobileNo.getText().toString());
//        requestParams.put("Parentno", etParentno.getText().toString());
//        requestParams.put("Email", etEmailID.getText().toString());
//        requestParams.put("Gender", etGender.getText().toString());
//        requestParams.put("Address", etAdress.getText().toString());
//        requestParams.put("Branch", spinner_branch.getSelectedItem().toString());
//        requestParams.put("Sem", spinner_sem.getSelectedItem().toString());
//        requestParams.put("Subject", spinner_sub.getSelectedItem().toString());
//        requestParams.put("Username", etUsername.getText().toString());
//        requestParams.put("Password", etPassword.getText().toString());
//
//        asyncHttpClient.post(Urls.registerUserWebService, requestParams, new JsonHttpResponseHandler() {
//
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//
//                        try {
//                            String status = response.getString("success");
//                            if (status.equals("1")) {
//                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
//                                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
//                                startActivity(i);
//                                progressDialog.dismiss();
//
//
//                            } else {
//                                Toast.makeText(RegistrationActivity.this, "Already data exist", Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
//                            }
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//
//                        Toast.makeText(RegistrationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//
//                    }
//                }
//        );
//    }
}



