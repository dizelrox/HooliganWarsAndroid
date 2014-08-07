package com.example.dizelrox.hooliganwarsandroid.GUI;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dizelrox.hooliganwarsandroid.Logic.Player;
import com.example.dizelrox.hooliganwarsandroid.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;


public class SignUp_Activity extends Activity {

    EditText nameInput;
    EditText loginInput;
    EditText passwordInput;
    Button signUpButton;
    ProgressBar progressBar;
    TextView progressText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        nameInput = (EditText) findViewById(R.id.playerNameInput);
        loginInput = (EditText) findViewById(R.id.loginInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.progressText);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up_, menu);
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
        String login = loginInput.getText().toString();
        String password = passwordInput.getText().toString();
        String playerName = nameInput.getText().toString();

        String loginRegex = "^[a-zA-Z0-9_-]{4,15}$";
        String nameRegex = "[a-zA-Z\\\\._\\\\-]{3,8}";

        if (playerName.matches(nameRegex))
        {
            if (login.matches(loginRegex))
            {
                if (password.matches(loginRegex))
                {
                        Player player = new Player(playerName);
                    player.setLoginToDatabase(login);

                        signUpButton.setVisibility(View.GONE);
                        new SignUpAsync().execute(player,login,password);
                        progressBar.setVisibility(View.VISIBLE);


                } else
                {
                    Toast.makeText(this, "Illegal password! Must be between 4 to 15 characters!",Toast.LENGTH_LONG).show();
                }
            } else
            {
                Toast.makeText(this, "Illegal login! Must be between 4 to 15 characters!",Toast.LENGTH_LONG).show();
            }

        } else
        {
            Toast.makeText(this, "Name can contain only letters",Toast.LENGTH_LONG).show();
        }
    }

    public class SignUpAsync extends AsyncTask <Object,String,Player>
    {
        String output = null;
        Player player;
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressText.setText(values[0]);
        }

        @Override
        protected Player doInBackground(Object... params) {



            try {

                player = (Player) params[0];
                String login = (String) params[1];
                String password = (String) params[2];

                publishProgress("Creating player....");
                Thread.sleep(1000);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(player);

                String link = ("http://dizel-services.ddns.net:8080/HW_Servlet/AddUserToDB?" +
                        "jsonObject="+ URLEncoder.encode(json, "UTF-8")+
                        "&login="+URLEncoder.encode(login,"UTF-8")+
                        "&password="+URLEncoder.encode(password,"UTF-8"));

                publishProgress("Connecting to database...");
                Thread.sleep(1000);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(link);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);

                publishProgress("Sending player...");
                Thread.sleep(1000);
                System.out.println(output);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Player aVoid) {
            progressText.setText(null);
            signUpButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (output.compareTo("Failed to add player\r\n") == 0) {
                Toast.makeText(getApplicationContext(), "Failed to add player",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent characterFrame = new Intent(SignUp_Activity.this,Character_Activity.class);
                characterFrame.putExtra("player",player);
                startActivity(characterFrame);
            }
            super.onPostExecute(aVoid);
        }
    }

}


