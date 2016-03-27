package com.example.nikhil.myroandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sinch.android.rtc.calling.Call;

public class ConnectActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final EditText robotname = (EditText) findViewById(R.id.robot_name_field);
        EditText robotcode = (EditText) findViewById(R.id.robot_code_field);

        Button connect = (Button) findViewById(R.id.connect_button);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userToCall = robotname.getText().toString();
                if (userToCall.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a user to call", Toast.LENGTH_LONG).show();
                    return;
                }

                Call call = getSinchServiceInterface().callUserVideo(userToCall);
                String callId = call.getCallId();

                Intent callScreen = new Intent(getApplicationContext(), CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
