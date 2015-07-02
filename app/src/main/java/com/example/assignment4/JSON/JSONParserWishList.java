package com.example.assignment4.JSON;

import android.util.Log;

import com.example.assignment4.Place.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JSONParserWishList {
    private static final String	TAG	= "JSONParser";

    public static Place parseWishJSON (JSONObject place) {
        Place r = new Place();
        r.title = place.optString("title");

        r.address = place.optString("address");

        r.time =place.optString("time");

        return r;
    }

/*    public static Restaurant parseMovieJSON(JSONObject movie) {

        Movie m = new Movie();
        m.title = movie.optString("title");
        m.rated = movie.optString("rated");
        m.rating = movie.optDouble("rating");
        m.urlPoster = movie.optString("urlPoster");
        m.releaseDate = movie.optString("releaseDate");
        m.simplePlot = movie.optString("simplePlot");

        return m;
    }*/

    public static ArrayList<Place> parseWishListJSON(JSONArray json_array) {
        ArrayList<Place> places = new ArrayList<>();

        try {
            // Log.d(TAG, json_array.toString(2));
            for(int i = 0; i < json_array.length(); i++) {
                JSONObject json_object = json_array.getJSONObject(i);
                places.add(parseWishJSON(json_object));
                //JSONArray restaurant_list = json_object.getJSONArray("");
        /*        for (int j = 0; j < restaurant_list.length(); i++) {

                    JSONObject property = restaurant_list.getJSONObject(i);

                    restaurants.add(parseRestaurantDetailsJSON(property));
                }*/

            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException");
            e.printStackTrace();
            return places;
        }
        return places;
    }
}