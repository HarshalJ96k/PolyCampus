package com.polycampus.android.common;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.polycampus.android.R;

import java.util.List;

public class AdapterGetAllNoticeDetails extends BaseAdapter {



    List<POJOGetAllnotice> pojoGetAllnotices;
    Activity activity;

    public AdapterGetAllNoticeDetails(List<POJOGetAllnotice> list, Activity activity) {
        this.pojoGetAllnotices = list;
        this.activity = activity;
    }


    @Override
    public int getCount() {
        return pojoGetAllnotices.size();
    }

    @Override
    public Object getItem(int position) {
        return pojoGetAllnotices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
final ViewHolder holder;
        LayoutInflater inflater=(LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(view==null){
            holder = new ViewHolder();
             view= inflater.inflate(R.layout.lv_getallnotices,null);
            holder.ivNoticeImage=view.findViewById(R.id.lvivGetallnoticeimage);
            holder.tvNoticeName=view.findViewById(R.id.tvNoticeName);
            view.setTag(holder);

        }
        else{
            holder=(ViewHolder) view.getTag();
            final POJOGetAllnotice obj= pojoGetAllnotices.get(i);
            Glide.with(activity).load("http://192.168.106.1:80/PloCampus/images/"+obj.getNoticeImage())
                    .skipMemoryCache(true).error(R.drawable.image_not_found).into(holder.ivNoticeImage);
        }

        return null;
    }

    class ViewHolder {
        ImageView ivNoticeImage;
        TextView tvNoticeName;

    }
}
