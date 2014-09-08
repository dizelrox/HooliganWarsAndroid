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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dizelrox.hooliganwarsandroid.Logic.MyApplication;
import com.example.dizelrox.hooliganwarsandroid.Logic.Player;
import com.example.dizelrox.hooliganwarsandroid.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.UnknownHostException;


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
        progressBar = (ProgressBar) findViewById(R.id.signUpProgressBar);
        progressText = (TextView) findViewById(R.id.opponentNameText);

        final ImageButton signUp_mute = (ImageButton) findViewById(R.id.signUp_mute);
        signUp_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.muteClicked(signUp_mute);
            }
        });
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


                player = (Player) params[0];
                String login = (String) params[1];
                String password = (String) params[2];
            try {
                publishProgress("Connecting to server");
                Thread.sleep(1000);
                Socket clientSocket = new Socket("192.168.0.101", 55555);
                publishProgress("Connected!");
                Thread.sleep(1000);
                ObjectOutputStream clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                String command = "Let me add a player!";
                clientOutput.writeObject(command);
                clientOutput.writeObject(player);
                publishProgress("Adding new player to database");
                Thread.sleep(1000);
                clientOutput.writeObject(login);
                clientOutput.writeObject(password);
                ObjectInputStream clientInput = new ObjectInputStream(clientSocket.getInputStream());
                output = (String) clientInput.readObject();
                publishProgress("Done!");
                Thread.sleep(1000);

            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
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

            if (output.compareTo("Failed to add player") == 0) {
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


