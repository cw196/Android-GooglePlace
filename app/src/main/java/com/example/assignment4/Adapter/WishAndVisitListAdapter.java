package com.example.assignment4.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.assignment4.Place.Place;
import com.example.assignment4.R;

import java.util.List;


public class WishAndVisitListAdapter extends ArrayAdapter<Place> {

    private LayoutInflater layout_inflater;

    public WishAndVisitListAdapter(Context context, int resource,int default_text_id, List<Place> list) {
        super(context,resource,default_text_id,list);
        layout_inflater = LayoutInflater.from(context);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = layout_inflater.inflate(R.layout.wish_visit_list, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.wish_visit_title);
            holder.address = (TextView) convertView.findViewById(R.id.wish_visit_address);
            holder.time= (TextView) convertView.findViewById(R.id.wish_visit_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Place place = this.getItem(position);


        holder.title.setText(place.title);
        holder.address.setText(place.address);
        holder.time.setText(place.time);

        return convertView;
    }
    static class ViewHolder {

        TextView title;
        TextView address;
        TextView time;
    }
}
