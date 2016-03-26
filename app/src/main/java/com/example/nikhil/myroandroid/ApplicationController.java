package com.example.nikhil.myroandroid;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Nikhil on 3/12/16.
 */
public class ApplicationController  extends Application {
    private static final boolean DEBUG = false;
    public static final String URL= DEBUG ? "http://10.0.2.2:3000/" : "https://pure-fortress-98966.herokuapp.com/";

    public static RequestQueue requestQueue;


    @Override
    public  void onCreate(){
        super.onCreate();

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }
    }
}