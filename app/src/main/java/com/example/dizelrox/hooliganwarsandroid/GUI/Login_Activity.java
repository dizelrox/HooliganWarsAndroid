package com.example.dizelrox.hooliganwarsandroid.GUI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class Login_Activity extends Activity {
    EditText loginInput;
    EditText passwordInput;
    TextView errorTextView;
    ProgressBar loginProgressBar;
    Button loginButton;
    ToggleButton rememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginInput = (EditText) findViewById(R.id.loginInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        loginProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        loginButton = (Button) findViewById(R.id.loginButton);
        rememberMe = (ToggleButton) findViewById(R.id.rememberMe);

        final ImageButton login_mute = (ImageButton) findViewById(R.id.login_mute);
        login_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.muteClicked(login_mute);
            }
        });
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


                String login = (String) params[0];
                String password = (String) params[1];

            try {
                publishProgress("Connecting to server");
                Thread.sleep(1000);
                Socket clientSocket = new Socket("dizel-services.ddns.net", 55555);
                publishProgress("Connected!");
                Thread.sleep(1000);
                ObjectOutputStream clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                String command = "Let me in!";
                clientOutput.writeObject(command);
                clientOutput.writeObject(login);
                clientOutput.writeObject(password);
                ObjectInputStream clientInput = new ObjectInputStream(clientSocket.getInputStream());
                output = (String) clientInput.readObject();
                if(output.compareTo("Player found") == 0)
                {
                    if(rememberMe.isChecked())
                    {
                        SharedPreferences preferences = getSharedPreferences("logged history",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("login",login);
                        editor.putString("password",password);
                        editor.commit();

                        String ll = preferences.getString("login","never saved");
                        String pp = preferences.getString("password","never saved");

                        System.out.println(ll+"  "+pp);
                    }
                    player = (Player) clientInput.readObject();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(Player player) {
            errorTextView.setText(null);
            loginButton.setVisibility(View.VISIBLE);
            loginProgressBar.setVisibility(View.GONE);

            if(output.compareTo("Player not found") == 0)
                Toast.makeText(getApplicationContext(), "Player not found",
                        Toast.LENGTH_SHORT).show();
            else
            {
                Intent characterFrame = new Intent(Login_Activity.this, Character_Activity.class);
                characterFrame.putExtra("player",this.player);
                startActivity(characterFrame);
                finish();
            }

            super.onPostExecute(player);
        }
    }
}
