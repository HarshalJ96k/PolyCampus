package com.polycampus.android.MyProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.polycampus.android.HomeActivity;
import com.polycampus.android.LoginActivity;
import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText etname,etmobileno,etparentno,etemail,etgender,etaddress,etbranch,etsem,etsubject;
    Button btnsavechanges;
    String  strname,strmobileno,strparentno,stremail,strgender,straddress,strbranch,strsem,strsubject;
    ProgressDialog dialog;


    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        preferences= PreferenceManager.getDefaultSharedPreferences(UpdateProfileActivity.this);
        editor=preferences.edit();

        etname=findViewById(R.id.etUpdateName);
        etmobileno=findViewById(R.id.etUpdateMobileno);
        etparentno=findViewById(R.id.etUpdateParentno);
        etemail=findViewById(R.id.etUpdateEmail);
        etgender=findViewById(R.id.etUpdateGender);
        etaddress=findViewById(R.id.etUpdateAddress);
        etbranch=findViewById(R.id.etUpdateBranch);
        etsem=findViewById(R.id.etUpdateSem);
        etsubject=findViewById(R.id.etUpdateSubject);

        btnsavechanges=findViewById(R.id.btnUpdateprofileSaveChanges);

        strname=getIntent().getStringExtra("name");
        strmobileno=getIntent().getStringExtra("mobile_no");
        strparentno=getIntent().getStringExtra("parent_mobile_no");
        stremail=getIntent().getStringExtra("email");
        strgender=getIntent().getStringExtra("gender");
        straddress=getIntent().getStringExtra("address");
        strbranch=getIntent().getStringExtra("branch");
        strsem=getIntent().getStringExtra("sem");
        strsubject=getIntent().getStringExtra("subject");

        etname.setText(strname);
        etmobileno.setText(strmobileno);
        etparentno.setText(strparentno);
        etemail.setText(stremail);
        etgender.setText(strgender);
        etaddress.setText(straddress);
        etbranch.setText(strbranch);
        etsem.setText(strsem);
        etsubject.setText(strsubject);

        btnsavechanges.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog=new ProgressDialog(UpdateProfileActivity.this);
                dialog.setTitle("Updating Profile ");
                dialog.setMessage("Please Wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                updateProfile();
            }
        });

    }

    private void updateProfile()
    {
        AsyncHttpClient asyncHttpClient =new AsyncHttpClient();
        RequestParams requestParams=new RequestParams();

        requestParams.put("name",etname.getText().toString());
        requestParams.put("mobile_no",etmobileno.getText().toString());
        requestParams.put("parent_mobile_no",etparentno.getText().toString());
        requestParams.put("email",etemail.getText().toString());
        requestParams.put("gender",etgender.getText().toString());
        requestParams.put("address",etaddress.getText().toString());
        requestParams.put("branch",etbranch.getText().toString());
        requestParams.put("sem",etsem.getText().toString());
        requestParams.put("subject",etsubject.getText().toString());

        asyncHttpClient.post(Urls.updateProfileWebservice,requestParams,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                //success 1 update done; success 0 update not done

                try
                {
                    String status =response.getString("success");
                    if (status.equals("1"))
                    {
                        Toast.makeText(UpdateProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        Intent i =new Intent(UpdateProfileActivity.this, LoginActivity.class);
                        editor.putBoolean("isLogin",false).commit();
                        editor.putString("username",preferences.getString("username","")).commit();
                        editor.putString("name",preferences.getString("name","")).commit();
                        editor.putString("mobile_no",preferences.getString("mobile_no","")).commit();
                        editor.putString("parent_mobile_no",preferences.getString("parent_mobile_no","")).commit();
                        editor.putString("email_id",preferences.getString("email_id","")).commit();
                        editor.putString("gender",preferences.getString("gender","")).commit();
                        editor.putString("address",preferences.getString("address","")).commit();
                        editor.putString("branch",preferences.getString("branch","")).commit();
                        editor.putString("sem",preferences.getString("sem","")).commit();
                        editor.putString("subject",preferences.getString("subject","")).commit();
                        startActivity(i);
                    }
                }
                catch (JSONException e)
                {
                    throw new RuntimeException(e);
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
            {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(UpdateProfileActivity.this, "Profile Not  Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
