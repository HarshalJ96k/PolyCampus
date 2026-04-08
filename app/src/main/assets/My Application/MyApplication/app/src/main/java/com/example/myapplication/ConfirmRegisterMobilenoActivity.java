package com.polycampus.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ConfirmRegisterMobilenoActivity extends AppCompatActivity {
    EditText etConfirmRegister;
    AppCompatButton btnVerify;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm_register_mobileno);
        etConfirmRegister=findViewById(R.id.etConfirmRegisterMobileno);
        btnVerify=findViewById(R.id.acbtnConfirmRegisterVerify);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etConfirmRegister.getText().toString().isEmpty())
                {
                    etConfirmRegister.setError("Please Enter Mobile no");
                } else if (etConfirmRegister.getText().toString().length()!=10)
                {
                  etConfirmRegister.setError("Please Enter Valid Mobile no");
                }else
                {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + etConfirmRegister.getText().toString(), 60,
                            TimeUnit.SECONDS, ConfirmRegisterMobilenoActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ConfirmRegisterMobilenoActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
//                                    progressDialog.dismiss();
                                    Toast.makeText(ConfirmRegisterMobilenoActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    Intent i=new Intent(ConfirmRegisterMobilenoActivity.this,ForgetPasswordActivity.class);
                                    i.putExtra("Verification_code", s);
                                    i.putExtra("Mobileno",etConfirmRegister.getText().toString());
                                     startActivity(i);
                                }
                            });

                }
            }
        });
    }


}
