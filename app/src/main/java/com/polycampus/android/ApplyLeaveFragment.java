package com.polycampus.android;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.polycampus.android.common.Urls;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * Fragment to allow students to apply for leave.
 * Merged from ApplyLeaveActivity to ensure a smooth, single-page fragment flow.
 */
public class ApplyLeaveFragment extends Fragment {

    TextInputEditText etName, etBranchSem, etFrom, etTo, etReason;
    MaterialButton btnSubmit;
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_apply_leave, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        etName = view.findViewById(R.id.etLeaveStudentName);
        etBranchSem = view.findViewById(R.id.etLeaveBranchSem);
        etFrom = view.findViewById(R.id.etLeaveFromDate);
        etTo = view.findViewById(R.id.etLeaveToDate);
        etReason = view.findViewById(R.id.etLeaveReason);
        btnSubmit = view.findViewById(R.id.btnSubmitLeave);

        etName.setText(preferences.getString("name", ""));
        etBranchSem.setText(preferences.getString("branch", "") + " - " + preferences.getString("sem", ""));

        etFrom.setOnClickListener(v -> showDatePicker(etFrom));
        etTo.setOnClickListener(v -> showDatePicker(etTo));

        btnSubmit.setOnClickListener(v -> {
            if (validate()) { submitLeave(); }
        });

        return view;
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            target.setText(day + "/" + (month + 1) + "/" + year);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validate() {
        if (etReason.getText().toString().isEmpty() || etFrom.getText().toString().isEmpty() || etTo.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please fill dates and reason", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submitLeave() {
        final android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Submitting Application...");
        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username", preferences.getString("username", ""));
        params.put("name", etName.getText().toString());
        params.put("branch", preferences.getString("branch", ""));
        params.put("sem", preferences.getString("sem", ""));
        params.put("reason", etReason.getText().toString());
        params.put("from_date", etFrom.getText().toString());
        params.put("to_date", etTo.getText().toString());

        client.post(Urls.urlApplyLeave, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                pd.dismiss();
                try {
                    if (response.getString("status").equals("success")) {
                        Toast.makeText(requireContext(), "Leave Application Submitted Successfully", Toast.LENGTH_LONG).show();
                        // Smoothly switch back to student section or attendance
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(requireContext(), "Submission Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { 
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Server Response Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                pd.dismiss();
                Toast.makeText(requireContext(), "Network Error: Could not connect to portal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
