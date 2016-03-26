package com.example.nikhil.myroandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + sessionManager.isLoggedIn(), Toast.LENGTH_LONG).show();
        sessionManager.checkLogin();

        //post params to be sent to the server
        final HashMap<String, String> params = new HashMap<String, String>();

        params.clear();

        final String robotURL = ApplicationController.URL+"/robots";

        params.put("name", sessionManager.getRobotName());
        params.put("udid", sessionManager.getUDID());

        JsonObjectRequest request = new JsonObjectRequest(robotURL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response:%n %s", response.toString());

                //store the user data in shared preferences
                try {

                    String robot_code = response.getString("code");
                    sessionManager.writePreference("robot_code", robot_code);

                    String robot_id = response.getString("_id");
                    sessionManager.writePreference("robot_id", robot_id);

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);

                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response:%n %s", error.toString());
            }
        });
        ApplicationController.requestQueue.add(request);




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

        if(id == R.id.action_logout)
        {
            sessionManager.logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }
}
