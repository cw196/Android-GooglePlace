package com.example.assignment4.JSON;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.example.assignment4.MainInterface.MainInterface;
import com.example.assignment4.Place.Place;


public class JSONParser {
    private static final String	TAG	= "JSONParser";

    public static Place parseMovieJSON(JSONObject json_single_place) throws JSONException {

        JSONObject geometry= json_single_place.getJSONObject("geometry");
        JSONObject location=geometry.getJSONObject("location");



        Place place = new Place();


            place.thumb_url=json_single_place.optString("icon");
            place.title = json_single_place.optString("name");
            place.address = json_single_place.optString("vicinity");


            place.lat = location.optDouble("lat");
            place.lng = location.optDouble("lng");
            place.type = new String[json_single_place.getJSONArray("types").length()];
            for (int i=0;i<json_single_place.getJSONArray("types").length();i++){
                place.type[i]=json_single_place.getJSONArray("types").getString(i);
            }



        return place;




    }

    public static ArrayList<Place> parseMovieListJSON(JSONObject category) {
        ArrayList<Place> movies = new ArrayList<>();

        try {

                JSONArray result_list=category.getJSONArray("results");
                for(int j =0; j<result_list.length();j++){
                    JSONObject single_place = result_list.getJSONObject(j);
                    Place place=parseMovieJSON(single_place);
                    if(place!=null)
                    movies.add(place);
                }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException");
            e.printStackTrace();
            return movies;
        }
        return movies;
    }

}

