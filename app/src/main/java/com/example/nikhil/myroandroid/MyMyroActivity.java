package com.example.nikhil.myroandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class MyMyroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_myro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SessionManager sessionManager = new SessionManager(getApplicationContext());

        TextView username = (TextView) findViewById(R.id.username);
        username.setText("User Name: "+sessionManager.getUsername());

        TextView userid = (TextView) findViewById(R.id.userid);
        userid.setText("User ID: "+sessionManager.getUserID());

        TextView robotname = (TextView) findViewById(R.id.robotname);
        robotname.setText("Robot Name: "+sessionManager.getRobotName());

        TextView robotid = (TextView) findViewById(R.id.robotid);
        robotid.setText("Robot ID: "+sessionManager.getRobotID());

        TextView robotcode = (TextView) findViewById(R.id.robotcode);
        robotcode.setText("Robot Code: "+sessionManager.getRobotCode());



    }

}
