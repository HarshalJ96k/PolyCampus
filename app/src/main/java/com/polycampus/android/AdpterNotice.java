package com.polycampus.android;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.polycampus.android.common.Urls;

import java.util.List;
import java.util.Random;

public class AdpterNotice extends RecyclerView.Adapter<AdpterNotice.ViewHolder> {
    private final List<POJONotice> pojoNotices;
    private final Activity activity;
    private final Random random = new Random();

    public AdpterNotice(List<POJONotice> pojoNotices, Activity activity) {
        this.pojoNotices = pojoNotices;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdpterNotice.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.notice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdpterNotice.ViewHolder holder, int position) {
        POJONotice item = pojoNotices.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDate.setText(item.getDate());
        holder.tvTime.setText(item.getTime());
        holder.tvDescription.setText(item.getDescription());

        Glide.with(activity)
                .load(Urls.webServiceAddress + "image/" + item.getImage())
                .skipMemoryCache(true)
                .error(R.drawable.sticky_note)
                .into(holder.ivImage);

        View.OnClickListener openFullView = v -> {
            Intent i = new Intent(activity, ImageDetails.class);
            i.putExtra("image", item.getImage());
            i.putExtra("title", item.getTitle());
            i.putExtra("date", item.getDate());
            i.putExtra("time", item.getTime());
            i.putExtra("dis", item.getDescription());
            activity.startActivity(i);
        };

        holder.cvCard.setOnClickListener(openFullView);
        holder.ivImage.setOnClickListener(openFullView);
    }

    @Override
    public int getItemCount() {
        return pojoNotices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvTime, tvDescription;
        ImageView ivImage;
        CardView cvCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTile);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescription = itemView.findViewById(R.id.tvDis);
            ivImage = itemView.findViewById(R.id.ivImage);
            cvCard = itemView.findViewById(R.id.cvCard);
        }
    }
}
