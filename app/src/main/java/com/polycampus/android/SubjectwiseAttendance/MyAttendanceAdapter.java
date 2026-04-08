package com.polycampus.android.SubjectwiseAttendance;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.polycampus.android.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MyAttendanceAdapter extends RecyclerView.Adapter<MyAttendanceAdapter.ViewHolder> {

    private final List<PojoClassMyAttendance> list;
    private final Context context;

    public MyAttendanceAdapter(List<PojoClassMyAttendance> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_my_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PojoClassMyAttendance obj = list.get(position);
        holder.presenty_date.setText(obj.getDate());
        holder.presenty_subject_name.setText(obj.getSubject_name());
        holder.presenty_status.setText(obj.getPresenty());

        if (obj.getPresenty().equalsIgnoreCase("Present")) {
            holder.status_card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.success_green));
            holder.status_icon.setImageResource(R.drawable.ic_done);
            holder.status_icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            
            holder.presenty_status.setBackgroundResource(R.drawable.tag_present);
            holder.presenty_status.setTextColor(ContextCompat.getColor(context, R.color.success_green));
        } else {
            holder.status_card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.error_red));
            holder.status_icon.setImageResource(R.drawable.close_icon);
            holder.status_icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            
            holder.presenty_status.setBackgroundResource(R.drawable.tag_absent);
            holder.presenty_status.setTextColor(ContextCompat.getColor(context, R.color.error_red));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView presenty_date, presenty_subject_name, presenty_status;
        ImageView status_icon;
        MaterialCardView status_card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            presenty_date = itemView.findViewById(R.id.txt_child_view_attendence_date);
            presenty_subject_name = itemView.findViewById(R.id.txt_child_view_attendence_subject);
            presenty_status = itemView.findViewById(R.id.txt_child_view_attendence_presenty);
            status_icon = itemView.findViewById(R.id.img_status_icon);
            status_card = itemView.findViewById(R.id.card_status_icon);
        }
    }
}
