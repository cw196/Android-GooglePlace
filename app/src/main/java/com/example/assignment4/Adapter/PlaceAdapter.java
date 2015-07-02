package com.example.assignment4.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.assignment4.Place.Place;
import com.example.assignment4.R;


import java.util.List;


public class PlaceAdapter extends ArrayAdapter {

    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader=null;
    public PlaceAdapter(Context context, int item_layout_id,int default_text_id, List<Place> movies, ImageLoader imageLoader) {
        super(context, item_layout_id, default_text_id, movies);
        layoutInflater = LayoutInflater.from(context);
        this.imageLoader=imageLoader;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.fragment_list_view,null);

            holder = new ViewHolder();

            holder.thumbnail = (NetworkImageView)convertView.findViewById(R.id.place_thumbnail);
            holder.title = (TextView) convertView.findViewById(R.id.place_title);
            holder.address = (TextView) convertView.findViewById(R.id.place_address);


            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Place place = (Place) this.getItem(position);

        holder.thumbnail.setImageUrl(place.thumb_url,imageLoader);
        holder.title.setText(place.title);
        holder.address.setText(place.address);


        return convertView;
    }

    static class ViewHolder {
        NetworkImageView thumbnail;
        TextView title;
        TextView address;

    }
}
