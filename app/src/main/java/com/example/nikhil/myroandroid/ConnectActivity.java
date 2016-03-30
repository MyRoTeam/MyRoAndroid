package com.example.nikhil.myroandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectActivity extends AppCompatActivity {

    private Pubnub mPubNub;
    private String username;
    SessionManager sessionManager;
    private String stdByChannel;
    public static String PUB_KEY = "pub-c-0fc9cafa-0e49-446b-9817-3f79e516aa02";
    public static String SUB_KEY = "sub-c-4fe709be-f691-11e5-8180-0619f8945a4f";

    public static final String USER_NAME    = "me.kg.androidrtc.SHARED_PREFS.USER_NAME";
    public static final String CALL_USER    = "me.kg.androidrtc.SHARED_PREFS.CALL_USER";
    public static final String STDBY_SUFFIX = "-stdby";


    public static final String JSON_CALL_USER = "call_user";
    public static final String JSON_CALL_TIME = "call_time";
    public static final String JSON_OCCUPANCY = "occupancy";
    public static final String STATUS_AVAILABLE = "Available";

    public static final String JSON_STATUS    = "status";


    EditText robotcode;

    EditText robotname;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(getApplicationContext());

        username = sessionManager.getUsername();

        stdByChannel = username + STDBY_SUFFIX;

        robotname = (EditText) findViewById(R.id.robot_name_field);
        robotcode = (EditText) findViewById(R.id.robot_code_field);

        Button connect = (Button) findViewById(R.id.connect_button);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });

        initPubNub();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initPubNub()
    {
        this.mPubNub = new Pubnub(this.PUB_KEY, this.SUB_KEY);
        this.mPubNub.setUUID(sessionManager.getUsername());
        subscribeStdBy();
    }

    /**
     * Subscribe to standby channel
     */
    private void subscribeStdBy(){
        try {
            this.mPubNub.subscribe(this.stdByChannel, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-iPN", "MESSAGE: " + message.toString());
                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;
                    try {
                        if (!jsonMsg.has(JSON_CALL_USER)) return;     //Ignore Signaling messages.
                        String user = jsonMsg.getString(JSON_CALL_USER);
                        dispatchIncomingCall(user);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("MA-iPN", "CONNECTED: " + message.toString());
                    setUserStatus(STATUS_AVAILABLE);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("MA-iPN","ERROR: " + error.toString());
                }
            });
        } catch (PubnubException e){
            Log.d("HERE","HEREEEE");
            e.printStackTrace();
        }
    }



    @Override
    protected void onStop()
    {
        super.onStop();
        if(this.mPubNub!=null){
            this.mPubNub.unsubscribeAll();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(this.mPubNub ==null){
            initPubNub();
        }
    }

    public void makeCall() {
        String callNum = robotname.getText().toString();
        if (callNum.isEmpty() || callNum.equals(this.username)) {
            showToast("Enter a valid user ID to call.");
            return;
        }
        dispatchCall(callNum);
    }

    private void dispatchIncomingCall(String userId){
        showToast("Call from: " + userId);
        Intent intent = new Intent(getApplicationContext(), VideoChatActivity.class);
        intent.putExtra(USER_NAME, username);
        intent.putExtra(CALL_USER, userId);
        startActivity(intent);
    }

    private void setUserStatus(String status){
        try {
            JSONObject state = new JSONObject();
            state.put(JSON_STATUS, status);
            this.mPubNub.setState(stdByChannel, username, state, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-sUS","State Set: " + message.toString());
                }
            });
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    public void dispatchCall(final String callNum) {
        final String callNumStdBy = callNum + STDBY_SUFFIX;
        this.mPubNub.hereNow(callNumStdBy, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("MA-dC", "HERE_NOW: " + " CH - " + callNumStdBy + " " + message.toString() + " Channel : " + channel);
                try {
                    int occupancy = ((JSONObject) message).getInt(JSON_OCCUPANCY);
                    if (occupancy == 0) {
                        showToast("User is not online!");
                        return;
                    }
                    JSONObject jsonCall = new JSONObject();
                    jsonCall.put(JSON_CALL_USER, username);
                    jsonCall.put(JSON_CALL_TIME, System.currentTimeMillis());
                    mPubNub.publish(callNumStdBy, jsonCall, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.d("MA-dC", "SUCCESS: " + message.toString());
                            Intent intent = new Intent(getApplicationContext(), VideoChatActivity.class);
                            intent.putExtra(USER_NAME, username);
                            intent.putExtra(CALL_USER, callNum);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }




}
