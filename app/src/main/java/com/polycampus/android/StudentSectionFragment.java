package com.polycampus.android;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.polycampus.android.StudyMaterial.CeertificateDownlode;
import com.polycampus.android.common.Urls;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class StudentSectionFragment extends Fragment
{
    CardView card,card2;
//    CardView cardView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_student_section, container, false);

//        pojoGetAllSectionDetails=new ArrayList<>();
//        searchView=view.findViewById(R.id.svStudentSectionFragmentSearch);
//        lvShowallSection=view.findViewById(R.id.lvStudentSectionFragmentShowMultipleSection);
//        tvNoSectionAvailable=view.findViewById(R.id.tvStudentSectionFragmentNoSectionAvailable);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
//        {
//            @Override
//            public boolean onQueryTextSubmit(String query)
//            {
//                searchSection(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query)
//            {
//                searchSection(query);
//                return false;
//            }
//        });
//
//        getAllSection();
        card=view.findViewById(R.id.cvCardBo);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CeertificateDownlode.class);
                startActivity(i);
            }
        });
        card2=view.findViewById(R.id.cvCardtc);
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CeertificateDownlodeTC.class);
                startActivity(i);
            }
        });

        return view;
    }

//    private void searchSection(String query)
//    {
//        List<POJOGetAllSectionDetails> tempSection = new ArrayList<>();
//        tempSection.clear();
//
//        for (POJOGetAllSectionDetails obj :pojoGetAllSectionDetails)
//        {
//            if (obj.getCategoryName().toUpperCase().contains(query.toUpperCase()))
//            {
//                tempSection.add(obj);
//            }
//            else
//            {
//                tvNoSectionAvailable.setVisibility(View.VISIBLE);
//            }
//
//            adapterGetAllSectionDetails=new AdapterGetAllSectionDetails(tempSection,AddStudyMaterialActivity.this);
//            lvShowallSection.setAdapter(adapterGetAllSectionDetails);
//        }
//    }
//
//    private void getAllSection()
//    {
//        AsyncHttpClient client=new AsyncHttpClient();
//        RequestParams params =new RequestParams();
//
//        client.post(Urls.getAllCategoryWebService,
//                new JsonHttpResponseHandler()
//                {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response)
//                    {
//                        super.onSuccess(statusCode, headers, response);
//                        try {
//                            JSONArray jsonArray=response.getJSONArray("getAllCategory");
//                            if(jsonArray.isNull(0))
//                            {
//                                tvNoSectionAvailable.setVisibility(View.GONE);
//
//                            }
//                            for (int i=0;i<jsonArray.length();i++)
//                            {
//                                JSONObject jsonObject=jsonArray.getJSONObject(i);
//                                String sectionid=jsonObject.getString("id");
//                                String sectionimage=jsonObject.getString("categoryimage");
//                                String sectionname=jsonObject.getString("categoryname");
//
//                                pojoGetAllSectionDetails.add(new POJOGetAllSectionDetails(sectionid,sectionimage,sectionname));
//                            }
//                            adapterGetAllSectionDetails=new AdapterGetAllSectionDetails(pojoGetAllSectionDetails,AddStudyMaterialActivity.this);
//                            lvShowallSection.setAdapter(adapterGetAllSectionDetails);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
//                    {
//                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                        Toast.makeText(AddStudyMaterialActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
}
