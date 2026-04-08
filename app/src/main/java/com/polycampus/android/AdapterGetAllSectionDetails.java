package com.polycampus.android;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.polycampus.android.common.Urls;

import java.util.List;

public class AdapterGetAllSectionDetails extends BaseAdapter
{

    String id, categoryImage, categoryName;
    List<POJOGetAllSectionDetails> pojoGetAllCategoryDetails;
    Activity activity;

    public AdapterGetAllSectionDetails(List<POJOGetAllSectionDetails> list, Activity activity) {
        this.pojoGetAllCategoryDetails = list;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return pojoGetAllCategoryDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return pojoGetAllCategoryDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.lv_get_all_data, null);
            viewHolder.ivCategoryImage = view.findViewById(R.id.ivStudentSectionImage);
            viewHolder.tvCategoryName = view.findViewById(R.id.tvsectionname);
            viewHolder.cardView = view.findViewById(R.id.cvgetAlldata);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final POJOGetAllSectionDetails obj = pojoGetAllCategoryDetails.get(position);
        viewHolder.tvCategoryName.setText(obj.getCategoryName());

        // Load image using Glide
        Glide.with(activity)
                .load(Urls.webServiceAddress + "images" + obj.getCategoryImage())
                .skipMemoryCache(true)
                .error(R.drawable.imagenotfound)
                .into(viewHolder.ivCategoryImage);

        // Set click listener on CardView to open respective activity
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = obj.getCategoryName();
                Toast.makeText(activity, "Clicked: " + categoryName, Toast.LENGTH_SHORT).show();

                // Check which section is clicked and open the respective activity
                if (categoryName.equalsIgnoreCase("Bonafied")) {
                    // Open Bonafide activity
                    Intent intent = new Intent(activity,BonafiedActivity.class);
                    activity.startActivity(intent);
                } else if (categoryName.equalsIgnoreCase("Verify and Confirm TC")) {
                    // Open TC activity
                    Intent intent = new Intent(activity, TCActivity.class);
                    activity.startActivity(intent);
                }
//                else {
//                    // You can handle other categories here if needed
//                    Intent intent = new Intent(activity, SectionwiseFeaturesActivity.class);
//                    intent.putExtra("categoryname", categoryName);
//                    activity.startActivity(intent);
//                }
            }
        });

        return view;
    }

    class ViewHolder {
        CardView cardView;
        ImageView ivCategoryImage;
        TextView tvCategoryName;
    }
}
