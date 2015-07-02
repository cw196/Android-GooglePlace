package com.example.assignment4.MainInterface;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.assignment4.Adapter.WishAndVisitListAdapter;
import com.example.assignment4.Place.Place;
import com.example.assignment4.R;

import java.util.ArrayList;

public class WishAndVisitedList extends ActionBarActivity {

    private  ArrayList<Place> wishList=new ArrayList<>();
    private  ArrayList<Place> visited=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_and_view_list);


        if(getIntent().getParcelableArrayListExtra("wishList")!=null){
            wishList=getIntent().getParcelableArrayListExtra("wishList");
            WishAndVisitListAdapter adapter = new WishAndVisitListAdapter(this, R.layout.wish_visit_list, R.id.wish_visit_title, wishList);

            ListView property_list = (ListView) findViewById(R.id.wish_list);
            property_list.setAdapter(adapter);
        }


        if(getIntent().getParcelableArrayListExtra("visitedList")!=null){
            visited=getIntent().getParcelableArrayListExtra("visitedList");
            WishAndVisitListAdapter adapter = new WishAndVisitListAdapter(this, R.layout.wish_visit_list, R.id.wish_visit_title, visited);

            ListView property_list = (ListView) findViewById(R.id.wish_list);
            property_list.setAdapter(adapter);
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wish_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
