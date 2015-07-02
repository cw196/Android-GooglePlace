package com.example.assignment4.MainInterface;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment4.Fragments.DetailViewFragment;
import com.example.assignment4.Fragments.ListViewFragment;
import com.example.assignment4.Interface.OnPlaceSelectedListener;
import com.example.assignment4.JSON.JSONParser;
import com.example.assignment4.JSON.JSONParserWishList;
import com.example.assignment4.Place.Place;
import com.example.assignment4.Preference.SinglePreference;
import com.example.assignment4.R;
import com.example.assignment4.RefinePlaces.RefinePlaces;
import com.example.assignment4.utils.BitmapCache;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainInterface extends ActionBarActivity implements OnPlaceSelectedListener {

    private static final String TAG = "MainInterface";


    private FragmentManager fragment_manager;
    private ListViewFragment listViewFragment = null;

    public RequestQueue request_queue = null;
    private ImageLoader image_loader = null;
    private BitmapCache bitmap_cache = null;
    private ArrayList<Place> markers = new ArrayList<Place>();
    Double lat = null;
    Double lng = null;
    Double lat_pre = 0.0;
    Double lng_pre = 0.0;
    //saved Place from Google Place API
    ArrayList<Place> history;
    //the Place that within the certain distance
    ArrayList<Place> refined_list;
    ArrayList<Place> wishList;
    ArrayList<Place> visitedList;
    private GoogleMap mMap;
    Boolean firstTimeLoadMap = true;
    Boolean list_updated=false;
    //the radius parameter, the maximum number is 50000 meters according to Google Place guild.
    int radius = 50000;
    //the radius from current location, which is used to refine which places are selected.
    int range = 3000;
    //update rate, default by 10 minutes
    int refresh_period;
    //update range, if user move over 200 meter, the interface will be refreshed
    int refresh_range=200;
    //keep the timer
    int timer=0;
    //keep saving the current location information
    double current_lat;
    double current_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_interface);

        //get the refresh_period from SharedPreference
        PreferenceManager.setDefaultValues(this, R.xml.settings_all, true);

        SharedPreferences shared_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        refresh_period = shared_prefs.getInt("period", 10);


        if (savedInstanceState != null)
            firstTimeLoadMap = savedInstanceState.getBoolean("firstTimeLoadMap");

        //set SlidingPaneLayout
        SlidingPaneLayout sliding_layout = (SlidingPaneLayout) findViewById(R.id.sliding_layout);
        sliding_layout.setSliderFadeColor(getResources().getColor(android.R.color.transparent));
        sliding_layout.setPanelSlideListener(new SliderListener());
        sliding_layout.openPane();



        //set up map
        setUpMapIfNeeded();

        //get fragment manager
        fragment_manager = getFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragment_manager.beginTransaction();

        //add listViewFragment
        if (savedInstanceState == null) {
            listViewFragment = ListViewFragment.newInstance();
            fragmentTransaction.add(listViewFragment, "ListViewFragment");
            //fragmentTransaction.replace(R.id.fragment, listViewFragment);
        }

        //get wishList and visitedList ArrayList
        wishList = getIntent().getParcelableArrayListExtra("wishList");
        visitedList = getIntent().getParcelableArrayListExtra("visitedList");

        fragmentTransaction.commit();
        fragment_manager.executePendingTransactions();

        //set volley queue
        request_queue = Volley.newRequestQueue(this);
        bitmap_cache = new BitmapCache();
        image_loader = new ImageLoader(request_queue, bitmap_cache);
    }




    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MapFragment))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public void setUpMap() {
        mMap.clear();
        mMap.setMyLocationEnabled(true);

        //check interval for  updating list
        int interval = 5000;


        //get the current location
        final LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locMan.getBestProvider(criteria, true);
        Location loc = locMan.getLastKnownLocation(provider);
        if (loc != null) {
            //save the current location for requesting
            lat = loc.getLatitude();
            lng = loc.getLongitude();
        } else if (loc == null) {
            lat = 0.0;
            lng = 0.0;
        }

        LatLng myCoordinates = new LatLng(lat, lng);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 14);

        mMap.animateCamera(yourLocation);



        //perform when location changes or reach the time interval
        locMan.requestLocationUpdates(provider, interval, 0, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                timer=timer+5000;
                // When GPS infro changes, update new location infro
                String url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=" + radius + "&key=AIzaSyB6rNuWzEozLpnOnm2S0hv3bOHyKwYPLTI";

                //store the current location where user is
                current_lat = location.getLatitude();
                current_lng = location.getLongitude();
                //calculate the movement from current location to last location
                Double extend_radius = distFrom(current_lat, current_lng, lat_pre, lng_pre);
                //calculate the distance between requiring data and current location
                Double move_distance = distFrom(lat, lng, current_lat, current_lng);

                list_updated=false;
                Log.d(TAG,timer/1000+" "+refresh_period);

                if ((radius - move_distance) < range||(extend_radius > refresh_range)||((timer/1000)%(refresh_period*60)==0)) {
                    //re-update list when the the user is extend the range of requested data or when refresh time is reached
                    if ((radius - move_distance) < range||firstTimeLoadMap||((timer/1000)%(refresh_period*60)==0)) {

                        Log.d(TAG,"refreshed!!!!!!!!");
                        //request the database again to refresh the stored data and refresh the interface
                        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                                new Response.Listener<JSONObject>() {
                                    public void onResponse(JSONObject json_object) {
                                      try {
                                            history=JSONParser.parseMovieListJSON(json_object);
                                            refined_list= new RefinePlaces().RefineArray(history,current_lat,current_lng,range);
                                            updateList(refined_list);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {
                                            error.printStackTrace();
                                    }
                                }
                        );
                        request_queue.add(request1);
                        //set the current location to last location
                        lat_pre = location.getLatitude();
                        lng_pre = location.getLongitude();
                        //reset the circle point of required data
                        lat=lat_pre;
                        lng=lng_pre;
                        firstTimeLoadMap=false;
                    }
                    //update interface when user go out of a certain range
                    else{

                        try {

                            updateList(refined_list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        lat_pre = location.getLatitude();
                        lng_pre = location.getLongitude();
                    }

                }
//                t.setText(content);
            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onProviderEnabled(String provider) {


            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        });
    }

    public void updateList(ArrayList<Place> refined_list) throws JSONException {
        //new listViewFragment
        listViewFragment = ListViewFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                fragment_manager.beginTransaction();
        //replay the old listViewFragment
        fragmentTransaction.replace(R.id.fragment, listViewFragment);
        fragmentTransaction.commit();
        fragment_manager.executePendingTransactions();


        //update listViewFragment's list
        listViewFragment.update(refined_list);
        markers = listViewFragment.places;
        //set mpa infro
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.map_infro, null);
                TextView Title = (TextView) v.findViewById(R.id.infro_title);
                TextView Address = (TextView) v.findViewById(R.id.infro_address);
                Title.setText("Name:" + arg0.getTitle());
                Address.setText("Address:" + arg0.getSnippet());

                return v;

            }
        });

        //re-pin markers
        for (Place marker : markers) {
            if (marker != null) {
                Boolean mark_location = false;

                for (Place marker_wish : wishList) {
                    Boolean sameMarker = false;
                    if (marker_wish.title.equals(marker.title)) {
                        for (Place marker_visited : visitedList) {
                            if (marker_wish.title.equals(marker_visited.title)) {
                                sameMarker = true;
                            }
                        }
                        if (!sameMarker) {
                            mark_location = true;
                            mMap.addMarker(new MarkerOptions().position(new LatLng(marker.lat, marker.lng)).title(marker.title).snippet(marker.address).icon(BitmapDescriptorFactory.fromResource(R.drawable.wish_marker)));

                        }
                    }
                }
                for (Place marker_visited : visitedList) {
                    if (marker_visited.title.equals(marker.title)) {
                        mark_location = true;
                        mMap.addMarker(new MarkerOptions().position(new LatLng(marker.lat, marker.lng)).title(marker.title).snippet(marker.address).icon(BitmapDescriptorFactory.fromResource(R.drawable.visited_marker)));

                    }
                }


                if (mark_location == false) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(marker.lat, marker.lng)).title(marker.title).snippet(marker.address));
                }
            }


        }
    }


    public ImageLoader getImageLoader() {
        return image_loader;
    }


    //change original markers to special icon
    public void setMarker() {
        mMap.clear();
        for (Place marker : markers) {
            Boolean mark_location = false;
            for (Place marker_wish : wishList) {
                Boolean sameMarker = false;
                if (marker_wish.title.equals(marker.title)) {
                    for (Place marker_visited : visitedList) {
                        if (marker_wish.title.equals(marker_visited.title)) {
                            sameMarker = true;
                        }
                    }
                    if (!sameMarker) {
                        mark_location = true;
                        mMap.addMarker(new MarkerOptions().position(new LatLng(marker.lat, marker.lng)).title(marker.title).snippet(marker.address).icon(BitmapDescriptorFactory.fromResource(R.drawable.wish_marker)));
                    }
                }
            }
            for (Place marker_visited : visitedList) {
                if (marker_visited.title.equals(marker.title)) {
                    mark_location = true;
                    mMap.addMarker(new MarkerOptions().position(new LatLng(marker.lat, marker.lng)).title(marker.title).snippet(marker.address).icon(BitmapDescriptorFactory.fromResource(R.drawable.visited_marker)));
                }
            }


            if (mark_location == false) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(marker.lat, marker.lng)).title(marker.title).snippet(marker.address));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_interface, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.wishList) {
            Intent intent1 = new Intent(this, WishAndVisitedList.class);
            intent1.putParcelableArrayListExtra("wishList", wishList);
            startActivity(intent1);
        }
        if (id == R.id.visitedList) {
            Intent intent2 = new Intent(this, WishAndVisitedList.class);
            intent2.putParcelableArrayListExtra("visitedList", visitedList);
            startActivity(intent2);
        }
        if(id ==R.id.action_settings){
            startActivity(new Intent(this, SinglePreference.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (request_queue != null) {
            request_queue.cancelAll(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                "com.example.assignment4", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        editor.putBoolean("ready1", false);
        editor.putBoolean("ready2", false);

        editor.commit();
    }

    //when list view is selected
    @Override
    public void onPlaceSelected(Place place) {
        DetailViewFragment detailViewFragment = new DetailViewFragment().newInstance();
        LatLng point = new LatLng(place.lat, place.lng);
        CameraUpdate point_location = CameraUpdateFactory.newLatLngZoom(point, 14);
        mMap.animateCamera(point_location);

        FragmentTransaction fragmentTransaction =
                fragment_manager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, detailViewFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragment_manager.executePendingTransactions();
        if (detailViewFragment != null) {
            detailViewFragment.update(place);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putBoolean("firstTimeLoadMap", firstTimeLoadMap);

    }

    //when back button is clicked
    public void changeFragment(View view) {
        ListViewFragment listViewFragment = ListViewFragment.newInstance();

        FragmentTransaction fragmentTransaction =
                fragment_manager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, listViewFragment);
        fragmentTransaction.commit();
        fragment_manager.executePendingTransactions();

        listViewFragment.update(markers);
    }

    public void UploadWishDatabase(View view) throws UnsupportedEncodingException {
        TextView place_detail_title = (TextView) findViewById(R.id.place_details_title);
        TextView place_detail_address = (TextView) findViewById(R.id.place_details_address);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Log.d(TAG, (String) place_detail_address.getText());
        String url = "http://my-second-965.appspot.com/wishList?Title=" + URLEncoder.encode((String) place_detail_title.getText(), "utf-8") + "&Address=" + URLEncoder.encode((String) place_detail_address.getText(), "utf-8") + "&Time=" + URLEncoder.encode(dateFormat.format(date), "utf-8") + "&InsertAndRead=1";
        JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray json_array) {
                        wishList = JSONParserWishList.parseWishListJSON(json_array);

                        setMarker();
                        Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        );
        Toast.makeText(getApplicationContext(), "Adding into database", Toast.LENGTH_SHORT).show();
        request_queue.add(request2);


    }

    public void UploadVisitedDatabase(View view) throws UnsupportedEncodingException {

        TextView place_detail_title = (TextView) findViewById(R.id.place_details_title);
        TextView place_detail_address = (TextView) findViewById(R.id.place_details_address);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Log.d(TAG, (String) place_detail_address.getText());
        String url = "http://my-second-965.appspot.com/visitedList?Title=" + URLEncoder.encode((String) place_detail_title.getText(), "utf-8") + "&Address=" + URLEncoder.encode((String) place_detail_address.getText(), "utf-8") + "&Time=" + URLEncoder.encode(dateFormat.format(date), "utf-8") + "&InsertAndRead=1";
        JsonArrayRequest request3 = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray json_array) {
                        visitedList = JSONParserWishList.parseWishListJSON(json_array);

                        setMarker();
                        Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        );
        Toast.makeText(getApplicationContext(), "Adding into database", Toast.LENGTH_SHORT).show();
        request_queue.add(request3);
    }


    public class SliderListener extends SlidingPaneLayout.SimplePanelSlideListener {
        @Override
        public void onPanelOpened(View panel) {

        }

        @Override
        public void onPanelClosed(View panel) {

        }
    }




    //calculate distance between two location1
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist * 1.609344 * 1000;
    }


}
