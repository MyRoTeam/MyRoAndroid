package com.example.nikhil.myroandroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button signupbutton = (Button) findViewById(R.id.signup_button);
        final EditText username = (EditText) findViewById(R.id.username_field);
        final EditText password = (EditText) findViewById(R.id.password_field);
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        sessionManager = new SessionManager(getApplicationContext());

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String URL = ApplicationController.URL+"users/login";

                Log.d("URL",URL);

                //post params to be sent to the server
                final HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());

                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Login Response:%n %s", response.toString());
                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                        //store the user data in shared preferences
                        try {

                            //store the auth token
                            String authToken = response.getString("authToken");
                            sessionManager.writePreference("authToken", authToken);

                            //store the user_id
                            String user_id = response.getJSONObject("user").getString("_id");
                            sessionManager.writePreference("user_id", user_id);

                            //store the user name
                            String user_name = response.getJSONObject("user").getString("username");
                            sessionManager.writePreference("user_name",user_name);

                            //store the robot name
                            sessionManager.writePreference("robot_name", Build.SERIAL);

                            //store the UDID
                            sessionManager.writePreference("udid", Secure.getString(getContentResolver(), Secure.ANDROID_ID));

                            //set the logged in flag
                            sessionManager.writePreference("isLoggedIn", true);

                            params.clear();

                            final String robotURL = ApplicationController.URL+"robots";

                            params.put("name", sessionManager.getRobotName());
                            params.put("udid", /*sessionManager.getUDID()*/ "1234567891234567");


                            JsonObjectRequest request = new JsonObjectRequest(robotURL, new JSONObject(params), new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Robot Response:%n %s", response.toString());

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
                                    Log.d("Error Response:%n %s", error.toString() + " : " + error.getMessage());
                                }
                            });




                            ApplicationController.requestQueue.add(request);



                        } catch (JSONException e) {
                            Log.e("MYAPP",  e.toString());
                            // Do something to recover ... or kill the app.
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response:%n %s", error.toString());
                    }
                });



                ApplicationController.requestQueue.add(req);


            }
        });

    }

}
