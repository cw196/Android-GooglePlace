package com.example.assignment4.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.assignment4.Place.Place;
import com.example.assignment4.R;


public class DetailViewFragment extends Fragment {

    private static final String	TAG	= "DetailViewFragment";

    TextView place_detail_title;
    TextView place_detail_address;
    TextView place_detail_open_hours;
    TextView place_detail_types;


    public  DetailViewFragment(){

    }
    public static DetailViewFragment newInstance(){
        DetailViewFragment detailViewFragment=new DetailViewFragment();
        return detailViewFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){


        View details_view=inflater.inflate(R.layout.fragment_detail_view,container,false);

        place_detail_title = (TextView)details_view.findViewById((R.id.place_details_title));
        place_detail_address=(TextView)details_view.findViewById(R.id.place_details_address);
        place_detail_open_hours=(TextView)details_view.findViewById(R.id.place_detail_open_hours);
        place_detail_types=(TextView)details_view.findViewById(R.id.place_detail_types);

        String a= (String) place_detail_title.getText();
        return details_view;
    }

    public void update(Place place){



        place_detail_title.setText(place.title);
        place_detail_address.setText(place.address);
        place_detail_open_hours.setText(place.open_hour);
        String types="";
        for(int i=0;i<place.type.length;i++){
            types=types+place.type[i];
        }
        place_detail_types.setText(types);
    }


    public void clear(){


        place_detail_title.setText("");
        place_detail_address.setText("");
        place_detail_open_hours.setText("");
        place_detail_types.setText("");
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }
}
