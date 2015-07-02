package com.example.assignment4.RefinePlaces;

import com.example.assignment4.MainInterface.MainInterface;
import com.example.assignment4.Place.Place;

import java.util.ArrayList;


public class RefinePlaces {
    ArrayList<Place> returned_list=new ArrayList<>();
    public ArrayList<Place> RefineArray(ArrayList<Place> arrayList, double lat_current,double lng_current, int range){

        for(int i=0;i<arrayList.size();i++){
                double place_lat=arrayList.get(i).lat;
                double place_lng=arrayList.get(i).lng;
                double distance=new MainInterface().distFrom(place_lat,place_lng,lat_current,lng_current);
                if(distance<range){
                    returned_list.add(arrayList.get(i));
                }
        }
        return returned_list;

    }
}
