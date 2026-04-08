package com.polycampus.android;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.polycampus.android.common.AdapterGetAllNoticeDetails;
import com.polycampus.android.common.POJOGetAllnotice;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UpcomingEventsAcitivity extends AppCompatActivity {
    ListView listView;
    TextView tvUpcomingEvents;


    List<POJOGetAllnotice>pojoGetAllnotices;
    AdapterGetAllNoticeDetails adapterGetAllNoticeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upcoming_events_acitivity);
        listView=findViewById(R.id.lvUpcomingEventsboard);
        tvUpcomingEvents=findViewById(R.id.tvUpcomindEvents);
        getAllNotice();


    }

    private void getAllNotice() {
        AsyncHttpClient client= new AsyncHttpClient();
        RequestParams params= new RequestParams();
        client.post("http://192.168.106.1:80/PolyCampus/getAllNoticeDetails",params,
                new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            JSONArray jsonArray= response.getJSONArray(Integer.parseInt("getAllNotice"));
                            if (jsonArray.isNull(0)){
                                tvUpcomingEvents.setVisibility(View.VISIBLE);
                            }
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String strid = jsonObject.getString("id");
                                String strimage = jsonObject.getString("noticemage");
                                String strname = jsonObject.getString("noticename");
                                pojoGetAllnotices.add(new POJOGetAllnotice(strid,strimage,strname));

                            }
                            adapterGetAllNoticeDetails=new AdapterGetAllNoticeDetails(pojoGetAllnotices,UpcomingEventsAcitivity.this);
                            listView.setAdapter(adapterGetAllNoticeDetails);

                        }catch (Exception e){
                            throw new RuntimeException();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(UpcomingEventsAcitivity.this
                                , "Server Error", Toast.LENGTH_LONG).show();
                    }
                }
                );
    }

}
