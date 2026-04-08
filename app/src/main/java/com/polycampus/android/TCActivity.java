package com.polycampus.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.mime.Header;

public class TCActivity extends AppCompatActivity
{

    EditText etTcEnrollment,etTcName,etTcCaste,etTcPlaceOfBirth,etTcDOBMonthYear,etTcChristianEra,etTcWordsandFigure,
            etTcInstituteLastAttended,etTcDateofAdmission,etTcProgress,etTcConduct,etTcDateOfLeavingInstitute,etTcCourseYear,
            etTcStudyingSinceWhen,etTcReasonforLeavingPoly,etTcBranch,etTcPercentage,etTcExamName;
    Button btnSubmitTCInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcactivity);

        etTcEnrollment=findViewById(R.id.etTc_Enrollment);
        etTcName=findViewById(R.id.etTc_Full_Name);
        etTcCaste=findViewById(R.id.etTc_Caste);
        etTcPlaceOfBirth=findViewById(R.id.etTc_Place_of_Birth);
        etTcDOBMonthYear=findViewById(R.id.etTc_Date_of_Birth_Month_Year);
        etTcChristianEra=findViewById(R.id.etTc_According_to_Christian_Era);
        etTcWordsandFigure=findViewById(R.id.etTc_in_Words_and_figures);
        etTcInstituteLastAttended=findViewById(R.id.etTc_Institute_Last_Attended);
        etTcDateofAdmission=findViewById(R.id.etTc_Date_of_Admission);
        etTcProgress=findViewById(R.id.etTc_Progress);
        etTcConduct=findViewById(R.id.etTc_Conduct);
        etTcDateOfLeavingInstitute=findViewById(R.id.etTc_Date_of_Leaving_this_Institution);
        etTcCourseYear=findViewById(R.id.etTc_Enter_Course_and_Year);
        etTcStudyingSinceWhen=findViewById(R.id.etTc_Enter_Studying_Since_When);
        etTcReasonforLeavingPoly=findViewById(R.id.etTc_Reason_for_leaving_Poly__Institute);
        etTcBranch=findViewById(R.id.etTc_Branch);
        etTcPercentage=findViewById(R.id.etTc_Percentage);
        etTcExamName=findViewById(R.id.etTc_Exam_Name);
        btnSubmitTCInfo=findViewById(R.id.btnTc_Submit);

        btnSubmitTCInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendTcDetails();

            }
        });
    }

    private void sendTcDetails()
    {
        //client and server communication ,// over network data transfer or manipulate
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();//client and server communication
        RequestParams requestParams = new RequestParams(); //put data
        requestParams.put("enrollmentno", etTcEnrollment.getText().toString());//2 parameter , key and value
        requestParams.put("name", etTcName.getText().toString());
        requestParams.put("caste", etTcCaste.getText().toString());
        requestParams.put("place_of_birth", etTcPlaceOfBirth.getText().toString());
        requestParams.put("dob_month_and_year",etTcDOBMonthYear.getText().toString());
        requestParams.put("christian_era", etTcChristianEra.getText().toString());
        requestParams.put("words_and_figure",etTcWordsandFigure.getText().toString());
        requestParams.put("institute_last_attended",etTcInstituteLastAttended.getText().toString());
        requestParams.put("date_of_admission",etTcDateofAdmission.getText().toString());
        requestParams.put("progress",etTcProgress.getText().toString());
        requestParams.put("conduct",etTcConduct.getText().toString());
        requestParams.put("date_of_leaving_institute",etTcDateOfLeavingInstitute.getText().toString());
        requestParams.put("course_year",etTcCourseYear.getText().toString());
        requestParams.put("studying_since_when",etTcStudyingSinceWhen.getText().toString());
        requestParams.put("reason_for_leaving_poly",etTcReasonforLeavingPoly.getText().toString());
        requestParams.put("branch",etTcBranch.getText().toString());
        requestParams.put("percentage",etTcPercentage.getText().toString());
        requestParams.put("exam_name",etTcExamName.getText().toString());



        asyncHttpClient.post(Urls.bonafiedDetails, requestParams, new JsonHttpResponseHandler()
                {

                    public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                    {

                        try {
                            String status = response.getString("success");
                            if (status.equals("1")) {
                                Toast.makeText(TCActivity.this, "Request Sent Successfully ", Toast.LENGTH_SHORT).show();
                                Toast.makeText(TCActivity.this, "Please Wait Few Days", Toast.LENGTH_SHORT).show();
                                Intent i =new Intent(TCActivity.this,StudentSectionFragment.class);
                                startActivity(i);


                            } else {
                                Toast.makeText(TCActivity.this, "Already Request exist", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }


                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                        Toast.makeText(TCActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                    }
                }
        );
    }
}
