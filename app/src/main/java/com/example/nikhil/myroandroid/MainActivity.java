package com.example.nikhil.myroandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SessionManager sessionManager;
    private ProgressDialog mSpinner;

    public static boolean robotModeClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(getApplicationContext());
        //Toast.makeText(getApplicationContext(), "User Login Status: " + sessionManager.isLoggedIn(), Toast.LENGTH_LONG).show();
        sessionManager.checkLogin();

        Button usermode = (Button) findViewById(R.id.user_mode_btn);


        Button robotmode = (Button) findViewById(R.id.robot_mode_btn);

    }

    public void enterMode()
    {
        if(MainActivity.robotModeClicked)
        {
            Intent intent = new Intent(getApplicationContext(), WaitActivity.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
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

        if(id == R.id.action_my_myro)
        {
            Intent intent = new Intent(getApplicationContext(), MyMyroActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
