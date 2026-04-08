package com.polycampus.android.AdminApp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polycampus.android.R;

import java.util.List;

public class HODAdapter extends RecyclerView.Adapter<HODAdapter.ViewHolder> {

    private List<POJOHOD> list;
    private OnHODActionListener listener;

    public interface OnHODActionListener {
        void onDeleteClick(POJOHOD hod);
        void onResetPasswordClick(POJOHOD hod);
    }

    public HODAdapter(List<POJOHOD> list, OnHODActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hod_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        POJOHOD hod = list.get(position);
        holder.tvB.setText(hod.getBranch() + " Department");
        holder.tvU.setText("User: " + hod.getUsername());
        
        holder.btnD.setOnClickListener(v -> listener.onDeleteClick(hod));
        holder.btnR.setOnClickListener(v -> listener.onResetPasswordClick(hod));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvB, tvU;
        ImageButton btnD, btnR;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvB = itemView.findViewById(R.id.tvHODBranch);
            tvU = itemView.findViewById(R.id.tvHODUsername);
            btnD = itemView.findViewById(R.id.btnDeleteHOD);
            btnR = itemView.findViewById(R.id.btnResetHOD);
        }
    }
}
