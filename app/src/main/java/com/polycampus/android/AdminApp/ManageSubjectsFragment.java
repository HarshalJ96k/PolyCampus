package com.polycampus.android.AdminApp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
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

public class ManageSubjectsFragment extends Fragment implements SubjectAdapter.OnSubjectActionListener {

    private RecyclerView rvSubjects;
    private AutoCompleteTextView actvBranch, actvSemFilter;
    private ExtendedFloatingActionButton fabAddSubject;
    private TextView tvEmpty;
    private List<POJOSubject> subjectList = new ArrayList<>();
    private SubjectAdapter adapter;
    private String selectedBranch = "";
    private String selectedSem = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_subjects, container, false);

        rvSubjects = view.findViewById(R.id.rvSubjects);
        actvBranch = view.findViewById(R.id.actvBranch);
        actvSemFilter = view.findViewById(R.id.actvSemFilter);
        fabAddSubject = view.findViewById(R.id.fabAddSubject);
        tvEmpty = view.findViewById(R.id.tvEmptyMessage);

        rvSubjects.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SubjectAdapter(subjectList, this);
        rvSubjects.setAdapter(adapter);

        // 🛡️ Absolute Departmental Lock: Admin/HODs are strictly bound to their department
        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        selectedBranch = prefs.getString("branch", "").trim();

        // Always hide the Branch Filter card as everyone is now department-bound
        View filterCard = view.findViewById(R.id.cardBranchFilter);
        if (filterCard != null) {
            filterCard.setVisibility(View.GONE);
        }

        // Auto-load subjects if branch is valid (HOD level)
        if (!selectedBranch.isEmpty()) {
            loadSubjects();
        } else {
            Toast.makeText(getActivity(), "Unauthorized: Departmental context missing", Toast.LENGTH_LONG).show();
        }

        // Setup Semester Filter Dropdown (Still needed for filtering within the department)
        String[] semesters = {"1st Sem", "2nd Sem", "3rd Sem", "4th Sem", "5th Sem", "6th Sem"};
        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, semesters);
        actvSemFilter.setAdapter(semAdapter);

        actvSemFilter.setOnItemClickListener((parent, view1, position, id) -> {
            selectedSem = semesters[position];
            loadSubjects();
        });

        fabAddSubject.setOnClickListener(v -> showAddSubjectDialog());

        return view;
    }

    private void loadSubjects() {
        if (selectedBranch.isEmpty()) return;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("branch", selectedBranch);
        if (!selectedSem.isEmpty()) {
            params.put("semester", selectedSem);
        }

        client.post(Urls.urlGetSubjectsByFilter, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                subjectList.clear();
                try {
                    if (response.getInt("success") == 1) {
                        JSONArray array = response.optJSONObject("data").optJSONArray("subjects");
                        if (array == null) array = response.optJSONArray("subjects");
                        
                        for (int i = 0; i < (array != null ? array.length() : 0); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            // POJO parsing logic (using optString for safety since it's a new schema)
                            subjectList.add(new com.google.gson.Gson().fromJson(obj.toString(), POJOSubject.class));
                        }
                        tvEmpty.setVisibility(subjectList.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        tvEmpty.setText(response.optString("message", "No curriculum found"));
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Parsing Fault", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAddSubjectDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
        TextInputEditText etSubjectName = dialogView.findViewById(R.id.etSubjectName);
        TextInputEditText etSubjectCode = dialogView.findViewById(R.id.etSubjectCode);
        AutoCompleteTextView actvSem = dialogView.findViewById(R.id.actvSemester);

        String[] semesters = {"1st Sem", "2nd Sem", "3rd Sem", "4th Sem", "5th Sem", "6th Sem"};
        actvSem.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, semesters));

        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Add Curriculum Hub")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etSubjectName.getText().toString();
                    String code = etSubjectCode.getText().toString();
                    String sem = actvSem.getText().toString();
                    
                    if (name.isEmpty() || sem.isEmpty() || selectedBranch.isEmpty()) {
                        Toast.makeText(getActivity(), "Mandatory fields required for curriculum entry", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    performAddSubject(name, code, sem);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performAddSubject(String name, String code, String sem) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("subject_name", name);
        params.put("subject_code", code);
        params.put("semester", sem);
        params.put("branch", selectedBranch);

        client.post(Urls.urlAddSubject, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getActivity(), "Curriculum Entry Saved", Toast.LENGTH_SHORT).show();
                loadSubjects();
            }
        });
    }

    @Override
    public void onDeleteClick(POJOSubject subject) {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Confirm Decommission")
                .setMessage("Remove " + subject.getSubjectName() + " from academic catalogue?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("id", subject.getId());
                    client.post(Urls.urlDeleteSubject, params, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getActivity(), "Curriculum Item Purged", Toast.LENGTH_SHORT).show();
                            loadSubjects();
                        }
                    });
                })
                .setNegativeButton("Keep", null)
                .show();
    }
}
