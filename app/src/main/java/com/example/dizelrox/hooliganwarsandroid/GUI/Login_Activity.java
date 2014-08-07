package com.example.dizelrox.hooliganwarsandroid.GUI;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dizelrox.hooliganwarsandroid.Logic.MyApplication;
import com.example.dizelrox.hooliganwarsandroid.Logic.Player;
import com.example.dizelrox.hooliganwarsandroid.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;

public class Login_Activity extends Activity {
    EditText loginInput;
    EditText passwordInput;
    TextView errorTextView;
    ProgressBar loginProgressBar;
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginInput = (EditText) findViewById(R.id.loginInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        loginProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        loginButton = (Button) findViewById(R.id.loginButton);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_, menu);
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

    public void login(View v)
    {
        String login = loginInput.getText().toString();
        String password = passwordInput.getText().toString();

        loginButton.setVisibility(View.GONE);
        new LoginAsync().execute(login,password);
        loginProgressBar.setVisibility(View.VISIBLE);
    }

    public class LoginAsync extends AsyncTask <String,String,Player>
    {
        Player player;
        String output = null;

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            errorTextView.setText(values[0]);
        }

        @Override
        protected Player doInBackground(String... params) {

            try {
                String login = (String) params[0];
                String password = (String) params[1];

                publishProgress("Connecting to database....");
                Thread.sleep(1000);

                String link = ("http://dizel-services.ddns.net:8080/HW_Servlet/GetUserFromDB?" +
                        "&login="+URLEncoder.encode(login,"UTF-8")+
                        "&password="+URLEncoder.encode(password,"UTF-8"));

                publishProgress("Searching for player...");
                Thread.sleep(1000);

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(link);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);



                publishProgress("Receiving player...");
                Thread.sleep(1000);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Player player) {
            errorTextView.setText(null);
            loginButton.setVisibility(View.VISIBLE);
            loginProgressBar.setVisibility(View.GONE);

            if(output.compareTo("Player not found\r\n") == 0)
                Toast.makeText(getApplicationContext(), "Player not found",
                        Toast.LENGTH_SHORT).show();
            else
            {
                Gson gson = new Gson();
                player = gson.fromJson(output, Player.class);
                Intent characterFrame = new Intent(Login_Activity.this, Character_Activity.class);
                characterFrame.putExtra("player",player);
                startActivity(characterFrame);
            }

            super.onPostExecute(player);
        }
    }
}
