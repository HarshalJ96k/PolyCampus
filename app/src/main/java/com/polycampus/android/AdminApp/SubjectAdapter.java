package com.polycampus.android.AdminApp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polycampus.android.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<POJOSubject> subjectList;
    private OnSubjectActionListener listener;

    public interface OnSubjectActionListener {
        void onDeleteClick(POJOSubject subject);
    }

    public SubjectAdapter(List<POJOSubject> subjectList, OnSubjectActionListener listener) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_card, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        POJOSubject subject = subjectList.get(position);
        holder.tvSubjectName.setText(subject.getSubjectName());
        holder.tvSubjectCode.setText(subject.getSubjectCode());
        holder.tvSemester.setText(subject.getSemester());
        
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(subject));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvSubjectCode, tvSemester;
        ImageButton btnDelete;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubjectCode = itemView.findViewById(R.id.tvSubjectCode);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            btnDelete = itemView.findViewById(R.id.btnDeleteSubject);
        }
    }
}
