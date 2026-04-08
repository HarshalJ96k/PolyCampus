package com.polycampus.android.AdminApp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.polycampus.android.R;
import com.polycampus.android.common.Urls;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ManageHODFragment extends Fragment implements HODAdapter.OnHODActionListener {

    private RecyclerView rvHODs;
    private ExtendedFloatingActionButton fabAddHOD;
    private List<POJOHOD> hodList = new ArrayList<>();
    private HODAdapter adapter;
    private View emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_hods, container, false);
        
        rvHODs = view.findViewById(R.id.rvHODs);
        fabAddHOD = view.findViewById(R.id.fabAddHOD);
        emptyView = view.findViewById(R.id.tvEmptyHODMessage);

        rvHODs.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new HODAdapter(hodList, this);
        rvHODs.setAdapter(adapter);

        fabAddHOD.setOnClickListener(v -> showAddHODDialog());

        loadHODs();
        return view;
    }

    private void loadHODs() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Urls.urlGetHODList, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                hodList.clear();
                try {
                    if (response.getInt("success") == 1) {
                        JSONArray array = response.optJSONObject("data").optJSONArray("hods");
                        for (int i = 0; i < (array != null ? array.length() : 0); i++) {
                            hodList.add(new com.google.gson.Gson().fromJson(array.getJSONObject(i).toString(), POJOHOD.class));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    emptyView.setVisibility(hodList.isEmpty() ? View.VISIBLE : View.GONE);
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }

    private void showAddHODDialog() {
        View dView = getLayoutInflater().inflate(R.layout.dialog_add_hod, null);
        TextInputEditText etU = dView.findViewById(R.id.etHODUsername);
        TextInputEditText etP = dView.findViewById(R.id.etHODPassword);
        AutoCompleteTextView actvB = dView.findViewById(R.id.actvHODBranch);

        String[] branches = {"Computer", "Civil", "Mechanical", "Electrical", "Electronics"};
        actvB.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, branches));

        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Appoint HOD")
                .setView(dView)
                .setPositiveButton("Appoint", (dialog, which) -> {
                    performAddHOD(etU.getText().toString(), etP.getText().toString(), actvB.getText().toString());
                })
                .setNegativeButton("Maybe Later", null)
                .show();
    }

    private void performAddHOD(String u, String p, String b) {
        if (u.isEmpty() || p.isEmpty() || b.isEmpty()) {
            Toast.makeText(getActivity(), "Security credentials required", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("username", u);
        params.put("password", p);
        params.put("branch", b);
        new AsyncHttpClient().post(Urls.urlAddHOD, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getActivity(), "HOD Appointed Successfully", Toast.LENGTH_SHORT).show();
                loadHODs();
            }
        });
    }

    @Override
    public void onDeleteClick(POJOHOD hod) {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Decommission HOD")
                .setMessage("Revoke all administrative access for the " + hod.getBranch() + " department head?")
                .setPositiveButton("Decommission", (dialog, which) -> {
                    RequestParams params = new RequestParams();
                    params.put("id", hod.getId());
                    new AsyncHttpClient().post(Urls.urlDeleteHOD, params, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getActivity(), "HOD Access Revoked", Toast.LENGTH_SHORT).show();
                            loadHODs();
                        }
                    });
                })
                .setNegativeButton("Ignore", null)
                .show();
    }

    @Override
    public void onResetPasswordClick(POJOHOD hod) {
        View dV = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        TextInputEditText etNP = dV.findViewById(R.id.etNewPassword);
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Reset Account: " + hod.getBranch())
                .setView(dV)
                .setPositiveButton("Reset", (dialog, which) -> {
                    RequestParams params = new RequestParams();
                    params.put("branch", hod.getBranch());
                    params.put("new_password", etNP.getText().toString());
                    new AsyncHttpClient().post(Urls.urlResetHODCredentials, params, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getActivity(), "HOD Credentials Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
