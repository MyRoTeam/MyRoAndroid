package com.example.nikhil.myroandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

public class ConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final EditText robotname = (EditText) findViewById(R.id.robot_name_field);
        EditText robotcode = (EditText) findViewById(R.id.robot_code_field);

        Button connect = (Button) findViewById(R.id.connect_button);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
