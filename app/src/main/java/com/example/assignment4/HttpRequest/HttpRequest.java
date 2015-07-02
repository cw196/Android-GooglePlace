package com.example.assignment4.HttpRequest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpRequest {
    String result;
    String readLine = null;

    public String doGet(String username, String password, String baseUrl) {
        URL url = null;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(baseUrl);
        stringBuffer.append("?UserName=");
        stringBuffer.append(username);
        stringBuffer.append("&Password=");
        stringBuffer.append(password);
        String s = stringBuffer.toString();
        Log.v("The url is", s);
        try {
            url = new URL(s);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            result = bufferedReader.readLine();

  /*      while ((readLine = bufferedReader.readLine()) !=null) {
            result += readLine;
        }*/
            in.close();
            urlConnection.disconnect();
            System.out.println("The server's response is " + result);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }
}