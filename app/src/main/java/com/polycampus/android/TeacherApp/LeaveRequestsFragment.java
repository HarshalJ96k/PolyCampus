package com.polycampus.android.TeacherApp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class LeaveRequestsFragment extends Fragment {

    ListView lvRequests;
    ProgressBar progressBar;
    View emptyState;
    List<POJOLeaveRequest> leaveList;
    LeaveAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_requests, container, false);

        lvRequests = view.findViewById(R.id.lvLeaveRequests);
        progressBar = view.findViewById(R.id.progressLeave);
        emptyState = view.findViewById(R.id.emptyStateLeave);

        getLeaveRequests();

        return view;
    }

    private void getLeaveRequests() {
        leaveList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        
        // 🔒 Global Lockdown: Filter leave requests by HOD's branch
        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sessionBranch = prefs.getString("branch", "All");
        if (!sessionBranch.equalsIgnoreCase("All")) {
            params.put("branch", sessionBranch);
        }
        
        client.post(Urls.urlGetLeaveRequests, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray jarry = response.getJSONArray("leave_requests");
                    if (jarry.length() == 0) {
                        emptyState.setVisibility(View.VISIBLE);
                        return;
                    }
                    emptyState.setVisibility(View.GONE);
                    for (int i = 0; i < jarry.length(); i++) {
                        JSONObject jobj = jarry.getJSONObject(i);
                        leaveList.add(new POJOLeaveRequest(
                                jobj.getString("id"),
                                jobj.getString("student_username"),
                                jobj.getString("student_name"),
                                jobj.getString("branch"),
                                jobj.getString("semester"),
                                jobj.getString("reason"),
                                jobj.getString("from_date"),
                                jobj.getString("to_date"),
                                jobj.getString("status"),
                                jobj.getString("teacher_comment")
                        ));
                    }
                    adapter = new LeaveAdapter(leaveList);
                    lvRequests.setAdapter(adapter);
                } catch (JSONException e) { e.printStackTrace(); }
            }
        });
    }

    private void updateStatus(String id, String status, String comment) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("status", status);
        params.put("comment", comment);
        
        client.post(Urls.urlUpdateLeaveStatus, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getActivity(), "Status Updated to " + status, Toast.LENGTH_SHORT).show();
                getLeaveRequests(); // Refresh
            }
        });
    }

    class LeaveAdapter extends BaseAdapter {
        List<POJOLeaveRequest> list;
        public LeaveAdapter(List<POJOLeaveRequest> list) { this.list = list; }
        @Override public int getCount() { return list.size(); }
        @Override public Object getItem(int i) { return list.get(i); }
        @Override public long getItemId(int i) { return i; }
        @Override public View getView(int i, View view, ViewGroup viewGroup) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.lv_leave_request_item, viewGroup, false);
            TextView tvName = v.findViewById(R.id.txt_leave_student_name);
            TextView tvBranch = v.findViewById(R.id.txt_leave_branch_sem);
            TextView tvRange = v.findViewById(R.id.txt_leave_date_range);
            TextView tvReason = v.findViewById(R.id.txt_leave_reason_content);
            MaterialButton btnApprove = v.findViewById(R.id.btn_leave_approve);
            MaterialButton btnReject = v.findViewById(R.id.btn_leave_reject);

            POJOLeaveRequest pojo = list.get(i);
            tvName.setText(pojo.getName());
            tvBranch.setText(pojo.getBranch() + " - " + pojo.getSemester());
            tvRange.setText("Format: " + pojo.getFrom() + " - " + pojo.getTo());
            tvReason.setText(pojo.getReason());

            if (!pojo.getStatus().equals("Pending")) {
                v.findViewById(R.id.layout_leave_actions).setVisibility(View.GONE);
                // Highlight status if needed
            }

            btnApprove.setOnClickListener(x -> updateStatus(pojo.getId(), "Approved", "OK Accepted"));
            btnReject.setOnClickListener(x -> updateStatus(pojo.getId(), "Rejected", "Need formal proof"));

            return v;
        }
    }
}
