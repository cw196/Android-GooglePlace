package com.example.assignment4.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment4.HttpRequest.HttpRequest;
import com.example.assignment4.JSON.JSONParserWishList;
import com.example.assignment4.MainInterface.MainInterface;
import com.example.assignment4.Place.Place;
import com.example.assignment4.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class Login extends ActionBarActivity {

    EditText Email;
    EditText Password;
    private RequestQueue request_queue;
    ArrayList<Place> wishList =new ArrayList<>();
    ArrayList<Place> visitedList =new ArrayList<>();
    private static final String TAG="Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = (EditText)findViewById(R.id.Email_edit);
        Password = (EditText)findViewById(R.id.Password_edit);
        request_queue=Volley.newRequestQueue(this);

        //save the loading statement
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                "com.example.assignment4", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //clear the last loading statement
        editor.putBoolean("ready1", false);
        editor.putBoolean("ready2", false);

        editor.commit();
    }

    //Login pressed method
    public void LoginIdentify(View view){
        String url="http://my-second-965.appspot.com/LoginCustomer";
        Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();
        //start threads
        new verification(url).execute();

    }

    class verification extends AsyncTask<String, Integer, String> {

        String url;
        //getting the url;
        public verification(String url){
            this.url=url;
        }
        @Override
        protected String doInBackground(String... params) {

            //start requesting the target url;
            HttpRequest httpRequest = new HttpRequest();

            String result = httpRequest.doGet(Email.getText().toString(), Password.getText().toString(),url);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //verify whether the user information is correct
            if(result.equals("1")){
                Log.d(TAG, "Success!");
                String url="http://my-second-965.appspot.com/wishList?InsertAndRead=0";
                String url2="http://my-second-965.appspot.com/visitedList?InsertAndRead=0";

                final Intent intent = new Intent(getApplicationContext(), MainInterface.class);
                //prefetch the wishList
                Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray json_array) {
                        intent.toString();
                        wishList = JSONParserWishList.parseWishListJSON(json_array);
                        intent.putParcelableArrayListExtra("wishList", wishList);

                        //save the ready statement in database
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                "com.example.assignment4", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();


                        editor.putBoolean("ready1", true);
                        editor.commit();

                        Boolean ready1=sharedPref.getBoolean("ready1", false);
                        Boolean ready2=sharedPref.getBoolean("ready2", false);

                        if(ready1==true&&ready2==true){
                            //startActivity
                            startActivity(intent);

                        }

                    }
                };
                //prefetch the visitedList
                Response.Listener<JSONArray> listener2 = new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray json_array) {
                        intent.toString();
                        visitedList = JSONParserWishList.parseWishListJSON(json_array);
                        intent.putParcelableArrayListExtra("visitedList", visitedList);
                        //save the ready statement in database
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                "com.example.assignment4", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("ready2", true);
                        editor.commit();

                        Boolean ready1=sharedPref.getBoolean("ready1", false);
                        Boolean ready2=sharedPref.getBoolean("ready2", false);

                        if(ready1==true&&ready2==true){
                            //startActivity
                            startActivity(intent);

                        }

                    }
                };



                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                };



                JsonRequest request = new JsonArrayRequest(Request.Method.GET, url, null,listener
                       ,errorListener );


                JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, url2, null,
                        listener2, errorListener);



                request_queue.add(request);
                request_queue.add(request2);






            }
            if(result.equals("0")){
                Toast.makeText(getApplicationContext(), "The email or password is wrong", Toast.LENGTH_SHORT).show();
                Email.setText("");
                Password.setText("");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
