package com.example.nikhil.myroandroid;

import android.os.Bundle;
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

import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button register = (Button) (findViewById(R.id.register));
        final EditText new_username = (EditText) (findViewById(R.id.new_username));
        final EditText password = (EditText) (findViewById(R.id.new_password));
        final EditText confirm_password = (EditText) (findViewById(R.id.new_password_confirm));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if username is minimum length
                if (new_username.getText().toString().length() <= 3) {
                    Toast.makeText(getApplicationContext(), "Username needs to be at least 3 characters long", Toast.LENGTH_LONG).show();
                } else if (password.getText().toString().length() < 8) {
                    Toast.makeText(getApplicationContext(), "Password needs to be at least 8 characters long", Toast.LENGTH_LONG).show();
                } else if (!(password.getText().toString().equals(confirm_password.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "Password and Confirmed password are not the same", Toast.LENGTH_LONG).show();
                } else {
                    final String URL = ApplicationController.URL+"users";//"https://pure-fortress-98966.herokuapp.com/users";

                    //post params to be sent to the server
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("username", new_username.getText().toString());
                    params.put("password", password.getText().toString());

                    JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Register Response:%n %s", response.toString());
                            Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Register Error Response:%n %s", error.toString());
                        }
                    });
                    ApplicationController.requestQueue.add(req);


                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
