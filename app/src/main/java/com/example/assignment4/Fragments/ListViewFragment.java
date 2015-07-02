package com.example.assignment4.Fragments;

import android.app.Activity;
import android.app.ListFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.assignment4.Adapter.PlaceAdapter;

import com.example.assignment4.Interface.OnPlaceSelectedListener;
import com.example.assignment4.MainInterface.MainInterface;
import com.example.assignment4.Place.Place;
import com.example.assignment4.R;

import java.util.ArrayList;


public class ListViewFragment extends ListFragment {

    private static final String TAG = "ClientFragment";
    private static final int             NO_SELECTION = -1;
    private int                           selected_item_position = NO_SELECTION;


    OnPlaceSelectedListener onPlaceSelected;

    PlaceAdapter placeAdapter;
    public ArrayList<Place> places=new ArrayList<Place>();


    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance(){
        ListViewFragment listViewFragment=new ListViewFragment();
        return listViewFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onPlaceSelected = (OnPlaceSelectedListener)activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onListItemClick(ListView l,View v,int position,long id){
        Log.d(TAG, "onListItemClick() : " + position);

        selected_item_position =position;

        getListView().setItemChecked(selected_item_position, true);
        Log.d(TAG + "1", ((Place) placeAdapter.getItem(selected_item_position)).title);

        onPlaceSelected.onPlaceSelected((Place) placeAdapter.getItem(selected_item_position));

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Log.d("List.onActivityCreated", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        placeAdapter =new PlaceAdapter(getActivity(), R.layout.fragment_list_view, R.id.place_title,places,((MainInterface)getActivity()).getImageLoader());

        getListView().setAdapter(placeAdapter);
    }
    public  void setIsLoading(boolean is_loading){
        setListShown(!is_loading);
    }


    public void update(ArrayList<Place> retrieved_places){
        Log.d("ListFragment.Update", "upDate");

        setIsLoading(false);


        places.clear();
        if(retrieved_places!=null){
            places.addAll(retrieved_places);
        }

        placeAdapter.notifyDataSetChanged();

    }

    public void cancelAllRequests() {
        if(new MainInterface().request_queue !=null)
        new MainInterface().request_queue.cancelAll(this);
    }

    @Override
    public void onStop(){
        Log.d(TAG, "onStop");
        super.onStop();
        cancelAllRequests();
    }

}
