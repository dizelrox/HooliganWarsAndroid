package com.example.dizelrox.hooliganwarsandroid.GUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dizelrox.hooliganwarsandroid.Logic.BackgroundSoundService;
import com.example.dizelrox.hooliganwarsandroid.Logic.SoundService;
import com.example.dizelrox.hooliganwarsandroid.R;
import com.example.dizelrox.hooliganwarsandroid.Logic.RequestAndResult_Codes;


public class Main_Activity extends Activity {

    public static Intent svc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        svc = new Intent(this, SoundService.class);
        svc.putExtra("isLooping",true);
        svc.putExtra("trackName","background1");
        //startService(svc);
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signUp(View v)
    {
        Intent signUpActivity = new Intent(this,SignUp_Activity.class);
        startActivityForResult(signUpActivity, RequestAndResult_Codes.SIGN_UP_REQUEST_CODE);
    }

    public void login(View v)
    {
        Intent loginActivity = new Intent(this,Login_Activity.class);
        startActivityForResult(loginActivity,RequestAndResult_Codes.LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(svc);
    }
}
