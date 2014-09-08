package com.example.dizelrox.hooliganwarsandroid.GUI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dizelrox.hooliganwarsandroid.Logic.MyApplication;
import com.example.dizelrox.hooliganwarsandroid.Logic.Player;
import com.example.dizelrox.hooliganwarsandroid.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;

public class Splash extends Activity {

    ProgressBar progressBar;
    TextView progressText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        progressBar = (ProgressBar) findViewById(R.id.splashProgressBar);
        progressText = (TextView) findViewById(R.id.opponentNameText);
        progressText.setText("");
        new SplashAsync().execute();
    }

    public class SplashAsync extends AsyncTask <Void,String,Void>
    {

        Boolean serverStatus = true;
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(values[0].compareTo("nothing") != 0) {
                progressText.append("\n\r" + values[0]);
            }
            if(Integer.parseInt(values[1]) != 0)
            progressBar.setProgress(Integer.parseInt(values[1]));
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                publishProgress("Loading resources...","0");
                for(int i=1;i<=25;i++) {
                    Thread.sleep(100);
                    publishProgress("nothing",Integer.toString(i));
                }

                MyApplication.ttobj = new TextToSpeech(getApplicationContext(),
                       new TextToSpeech.OnInitListener() {
                          @Override
                          public void onInit(int status) {
                              if(status != TextToSpeech.ERROR && MyApplication.ttobj != null){
                               MyApplication.ttobj.setLanguage(Locale.US);
                              }
                          }
                      });

                publishProgress("Checking network connection...", "0");
                for(int i=progressBar.getProgress();i<=50;i++) {
                    Thread.sleep(100);
                    publishProgress("nothing",Integer.toString(i));
                }

                try {
                    Socket clientSocket = new Socket("dizel-services.ddns.net", 55555);
                    ObjectOutputStream clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream gameServerInput = new ObjectInputStream(clientSocket.getInputStream());
                    String textConnection = "Are you alive?";
                    clientOutput.writeObject(textConnection);
                    ObjectInputStream clientInput = new ObjectInputStream(clientSocket.getInputStream());
                    textConnection = (String) clientInput.readObject();
                    if (textConnection.compareTo("Yes") == 0) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    serverStatus = false;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                publishProgress("Validating...","0");
                for(int i=progressBar.getProgress();i<=75;i++) {
                    Thread.sleep(100);
                    publishProgress("nothing",Integer.toString(i));
                }

                publishProgress("Preparing gameplay...","0");
                for(int i=progressBar.getProgress();i<=100;i++) {
                    Thread.sleep(100);
                    publishProgress("nothing",Integer.toString(i));
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!serverStatus) {
                Toast.makeText(getApplicationContext(), "Server offline",
                        Toast.LENGTH_SHORT).show();
                        finish();
            }
            else {
                SharedPreferences preferences = getSharedPreferences("logged history", MODE_PRIVATE);
                String login = preferences.getString("login","never saved");
                String password = preferences.getString("password","never saved");


                if(login.compareTo("never saved") == 0) {
                    Intent mainMenu = new Intent(Splash.this, Main_Activity.class);
                    startActivity(mainMenu);
                    finish();
                }
                else
                {
                    new LoginAsync().execute(login,password);
                }
                MyApplication.player = MediaPlayer.create(Splash.this, R.raw.background);
                MyApplication.player.setLooping(true); // Set looping
                MyApplication.player.setVolume(100,100);
                MyApplication.player.start();
            }
        }
    }

    public class LoginAsync extends AsyncTask <String,String,Player>
    {
        Player player = null;
        String output = null;

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
                    this.player = (Player) clientInput.readObject();
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
            if(output.compareTo("Player not found") == 0)
                Toast.makeText(getApplicationContext(), "Player not found",
                        Toast.LENGTH_SHORT).show();
            else
            {
                Intent characterFrame = new Intent(Splash.this, Character_Activity.class);
                characterFrame.putExtra("player",this.player);
                startActivity(characterFrame);
                finish();
            }

            super.onPostExecute(player);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
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
}
