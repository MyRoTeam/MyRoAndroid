package com.example.nikhil.myroandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

public class WaitActivity extends AppCompatActivity {

    private Pubnub mPubNub;
    private String username;
    SessionManager sessionManager;
    public static String PUB_KEY = "pub-c-0fc9cafa-0e49-446b-9817-3f79e516aa02";
    public static String SUB_KEY = "sub-c-4fe709be-f691-11e5-8180-0619f8945a4f";

    public static final String USER_NAME    = "me.kg.androidrtc.SHARED_PREFS.USER_NAME";
    public static final String CALL_USER    = "me.kg.androidrtc.SHARED_PREFS.CALL_USER";
    public static final String STDBY_SUFFIX = "-stdby";


    public static final String JSON_CALL_USER = "call_user";
    public static final String JSON_CALL_TIME = "call_time";
    public static final String JSON_OCCUPANCY = "occupancy";
    public static final String JSON_STATUS    = "status";
    private String stdByChannel;

    // JSON for user messages
    public static final String JSON_USER_MSG  = "user_message";
    public static final String JSON_MSG_UUID  = "msg_uuid";
    public static final String JSON_MSG       = "msg_message";
    public static final String JSON_TIME      = "msg_timestamp";

    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_OFFLINE   = "Offline";
    public static final String STATUS_BUSY      = "Busy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(getApplicationContext());

        username = sessionManager.getRobotName();

        stdByChannel = username + STDBY_SUFFIX;


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initPubNub();
    }

    /**
     * Subscribe to standby channel so that it doesn't interfere with the WebRTC Signaling.
     */
    public void initPubNub(){
        this.mPubNub  = new Pubnub(PUB_KEY, SUB_KEY);
        this.mPubNub.setUUID(this.username);
        Log.d("TAG", "Called initPubNub");
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
                    Log.d("MA-iPN", "CONNECTED: " + message.toString() + "Channel: " + channel);
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
    protected void onStop() {
        super.onStop();
        if(this.mPubNub!=null){
            this.mPubNub.unsubscribeAll();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(this.mPubNub==null){
            initPubNub();
        } else {
            subscribeStdBy();
        }
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

    private void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
