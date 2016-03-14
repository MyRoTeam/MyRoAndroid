package com.example.nikhil.myroandroid;

import android.content.Context;

/**
 * Created by Nikhil on 3/13/16.
 */
public class User
{
    SessionManager sessionManager;
    public User(Context context)
    {
        sessionManager = new SessionManager(context);
    }


}
