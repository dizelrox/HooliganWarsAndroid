package com.example.dizelrox.hooliganwarsandroid.GUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.dizelrox.hooliganwarsandroid.Logic.MyApplication;
import com.example.dizelrox.hooliganwarsandroid.Logic.Player;
import com.example.dizelrox.hooliganwarsandroid.Logic.Type;
import com.example.dizelrox.hooliganwarsandroid.Logic.Weapon;
import com.example.dizelrox.hooliganwarsandroid.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Battle_Activity extends Activity {

    volatile Player player;
    Player opponent;

    TextView consoleText;

    ImageView playerBattleHelmet;
    ImageView playerBattleArmor;
    ImageView playerBattleLegs;
    ImageView playerBattleFeet;
    ImageView playerBattleWeapon;
    ImageView playerIcon;
    ProgressBar playerHealthBar;
    TextView playerNameTextView;


    ImageView opponentBattleHelmet;
    ImageView opponentBattleArmor;
    ImageView opponentBattleLegs;
    ImageView opponentBattleFeet;
    ImageView opponentBattleWeapon;
    ImageView opponentIcon;
    ProgressBar opponentHealthBar;
    TextView opponentNameTextView;

    Button attackButton;
    RadioGroup attackRadios;
    RadioGroup defenseRadios;

    Socket gameServer;
    ObjectOutputStream gameServerOutput;
    ObjectInputStream gameServerInput;

    Boolean turn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battle_activity);

        MyApplication.ttobj.speak("Waiting for opponent", TextToSpeech.QUEUE_FLUSH, null);

        this.player = (Player) getIntent().getSerializableExtra("player");
        this.consoleText = (TextView) findViewById(R.id.consoleText);


        this.playerIcon =           (ImageView) findViewById(R.id.playerBattleIcon);
        this.playerBattleHelmet =   (ImageView) findViewById(R.id.playerBattleHelmet);
        this.playerBattleArmor =    (ImageView) findViewById(R.id.playerBattleArmor);
        this.playerBattleLegs =     (ImageView) findViewById(R.id.playerBattleLegs);
        this.playerBattleFeet =     (ImageView) findViewById(R.id.playerBattleFeet);
        this.playerBattleWeapon =   (ImageView) findViewById(R.id.playerBattleWeapon);
        this.playerHealthBar =      (ProgressBar) findViewById(R.id.playerHealthBar);
        this.playerNameTextView =   (TextView) findViewById(R.id.playerNameText);

        this.opponentIcon =         (ImageView) findViewById(R.id.opponentBattleIcon);
        this.opponentBattleHelmet = (ImageView) findViewById(R.id.opponentBattleHelmet);
        this.opponentBattleArmor =  (ImageView) findViewById(R.id.opponentBattleArmor);
        this.opponentBattleLegs =   (ImageView) findViewById(R.id.opponentBattleLegs);
        this.opponentBattleFeet =   (ImageView) findViewById(R.id.opponentBattleFeet);
        this.opponentBattleWeapon = (ImageView) findViewById(R.id.opponentBattleWeapon);
        this.opponentHealthBar =    (ProgressBar) findViewById(R.id.opponentHealthBar);
        this.opponentNameTextView = (TextView) findViewById(R.id.opponentNameText);

        this.attackButton =         (Button) findViewById(R.id.attackButton);
        this.attackRadios =         (RadioGroup) findViewById(R.id.attackRadios);
        this.defenseRadios =         (RadioGroup) findViewById(R.id.defenseRadios);

        opponentHealthBar.setRotation(180);

        playerHealthBar.setProgress(player.getHealth());

        consoleText.setMovementMethod(new ScrollingMovementMethod());

        initializePlayerIcons();

        attackButton.setEnabled(false);

        findOpponent();
    }

    public void findOpponent()
    {
        new ConnectToGameServer().execute();
    }

    public class ConnectToGameServer extends AsyncTask<Void,String,Void>
    {



        @Override
        protected Void doInBackground(Void... params) {

            try {
                gameServer = new Socket("dizel-services.ddns.net", 55555);
                gameServerOutput = new ObjectOutputStream(gameServer.getOutputStream());
                gameServerInput = new ObjectInputStream(gameServer.getInputStream());
                String message = "I just want to play";
                gameServerOutput.writeObject(message);
                player.setHealth(100);
                player.setDefenceArea(null);
                player.setAttackArea(null);
                gameServerOutput.writeObject(player);
                Thread.sleep(200);
                opponent = (Player) gameServerInput.readObject();
                turn = (Boolean) gameServerInput.readObject();
                defineAttackButtonAvailability();
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
        protected void onPostExecute(Void aVoid) {
            initializeOpponentIcons();
            opponentHealthBar.setVisibility(View.VISIBLE);
            opponentHealthBar.setProgress(opponent.getHealth());
            MyApplication.ttobj.speak("Opponent found you are playing against "+opponent.getName(), TextToSpeech.QUEUE_FLUSH, null);
            if (!turn)
            {
                new WaitForHitAsync().execute();
            }
            super.onPostExecute(aVoid);
        }
    }

    public class WaitForHitAsync extends AsyncTask<Void,String,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            while (player.getDefenceArea() == null)
            {

            }
            try {

                gameServerOutput.writeObject(player.getDefenceArea());
                opponent.setAttackArea((Type) gameServerInput.readObject());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            opponent.throwHit((Weapon) opponent.getItem(Type.WEAPON), opponent.getStrengthFactor(), opponent.getAttackArea(), player);

            String number = Integer.toString((int) (Math.random() * 7 + 1));
            int resId = getResources().getIdentifier("hit"+number, "raw", getPackageName());
            MyApplication.player = MediaPlayer.create(Battle_Activity.this, resId);
            MyApplication.player.setVolume(100,100);
            MyApplication.player.start();
            turn = !turn;
            consoleText.append("[" + player.getCurrentTimeStamp() + "]" + "You have" + player.getConsoleText());
            updateHealthBar("player", player.getHealth());
            if (player.getHealth() <= 0)
            {
                new AlertDialog.Builder(Battle_Activity.this)
                        .setTitle("You've lost the battle")
                        .setMessage("You have been killed! Try your skills next time.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Battle_Activity.this.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            nullPlayerAndGuiFields();
            defineAttackButtonAvailability();
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            gameServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void attackButtonPressed(View v)
   {
       attackButton.setEnabled(false);
       switchRadios();
       turn = !turn;
       new AttackAsycn().execute();
   }

    public class AttackAsycn extends AsyncTask<Void,String,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {

            try {
            System.out.println("entered attack");
            gameServerOutput.writeObject(player.getAttackArea());
            opponent.setDefenceArea((Type) gameServerInput.readObject());
            } catch (IOException e) {
             e.printStackTrace();
            } catch (ClassNotFoundException e) {
                 e.printStackTrace();
             }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            player.throwHit((Weapon) player.getItem(Type.WEAPON), player.getStrengthFactor(), player.getAttackArea(), opponent);
            String number = Integer.toString((int) (Math.random() * 7 + 1));
            int resId = getResources().getIdentifier("hit"+number, "raw", getPackageName());
            MyApplication.player = MediaPlayer.create(Battle_Activity.this, resId);
            MyApplication.player.setVolume(100,100);
            MyApplication.player.start();
            consoleText.append("[" + player.getCurrentTimeStamp() + "]" + opponent.getName() + " has" + opponent.getConsoleText());
            updateHealthBar("bot",opponent.getHealth());
            switchRadios();
            if (opponent.getHealth() <= 0)
            {
                new AlertDialog.Builder(Battle_Activity.this)
                        .setTitle("You've won the battle")
                        .setMessage("Congratulations!You've killed your opponent."+Character_Activity.game.getUnlockedItems())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Battle_Activity.this.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            nullPlayerAndGuiFields();
            defineAttackButtonAvailability();
            new WaitForHitAsync().execute();
            super.onPostExecute(aVoid);
        }
    }

    public void switchRadios()
    {
        for(int i = 0; i < attackRadios.getChildCount(); i++){
            ((RadioButton)attackRadios.getChildAt(i)).setEnabled(!((RadioButton)attackRadios.getChildAt(i)).isEnabled());
            ((RadioButton)defenseRadios.getChildAt(i)).setEnabled(!((RadioButton)defenseRadios.getChildAt(i)).isEnabled());
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void defineAttackButtonAvailability()
    {
        if (player.getAttackArea() != null && player.getDefenceArea() != null && turn)
        {
            attackButton.setEnabled(true);
        } else
        {
            attackButton.setEnabled(false);
        }
    }

    private void nullPlayerAndGuiFields()
    {
        player.setAttackArea(null);
        player.setDefenceArea(null);
        defenseRadios.clearCheck();
        attackRadios.clearCheck();
    }

    private void initializeOpponentIcons()
    {
        opponentNameTextView.setText(opponent.getName());
        updateHealthBar("opponent", opponent.getHealth());
        opponentNameTextView.setText(opponent.getName());
        int resId = getResources().getIdentifier(opponent.getPlayerIcon(), "drawable", getPackageName());
        if (resId != 0)
            opponentIcon.setImageResource(resId);

        if(opponent.getItem(Type.HEAD) != null)
        {
            resId = getResources().getIdentifier(opponent.getItem(Type.HEAD).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                opponentBattleHelmet.setImageResource(resId);
        }

        if(opponent.getItem(Type.CHEST) != null)
        {
            resId = getResources().getIdentifier(opponent.getItem(Type.CHEST).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                opponentBattleArmor.setImageResource(resId);
        }

        if(opponent.getItem(Type.STOMACH) != null)
        {
            resId = getResources().getIdentifier(opponent.getItem(Type.STOMACH).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                opponentBattleLegs.setImageResource(resId);
        }

        if(opponent.getItem(Type.LEGS) != null)
        {
            resId = getResources().getIdentifier(opponent.getItem(Type.LEGS).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                opponentBattleFeet.setImageResource(resId);
        }

        if(opponent.getItem(Type.WEAPON) != null)
        {
            resId = getResources().getIdentifier(opponent.getItem(Type.WEAPON).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                opponentBattleWeapon.setImageResource(resId);
        }
    }



    private void updateHealthBar(String player,int setTo)
    {
        if(player.compareTo("player") == 0)
        {
            (new HealthBarThread(playerHealthBar,setTo)).start();
        }
        else
        {
            (new HealthBarThread(opponentHealthBar,setTo)).start();
        }
    }

    public void radioButtonClicked(View v)
    {
        RadioButton button = (RadioButton) v;
        int buttonIndex = Integer.parseInt((String) v.getTag());
        if(buttonIndex > 3) {
            Type t = Type.getType(buttonIndex-4);
            player.setAttackArea(t);
        }
        else {
            Type t = Type.getType(buttonIndex);
            player.setDefenceArea(t);
        }
        defineAttackButtonAvailability();
    }

    private class HealthBarThread extends Thread{

        ProgressBar bar;
        int setTo;

        public HealthBarThread(ProgressBar bar,int setTo )
        {
            this.bar = bar;
            this.setTo = setTo;
        }

        @Override
        public void run() {

            for(int i = bar.getProgress();i>=setTo;i--)
            {

                try {
                    bar.setProgress(i);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            super.run();
        }
    }

    private void initializePlayerIcons()
    {
        updateHealthBar("player",player.getHealth());
        System.out.println(player.getHealth());
        playerNameTextView.setText(player.getName());
        int resId = getResources().getIdentifier(player.getPlayerIcon(), "drawable", getPackageName());
        if (resId != 0)
            playerIcon.setImageResource(resId);

        if(player.getItem(Type.HEAD) != null)
        {
            resId = getResources().getIdentifier(player.getItem(Type.HEAD).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                playerBattleHelmet.setImageResource(resId);
        }

        if(player.getItem(Type.CHEST) != null)
        {
            resId = getResources().getIdentifier(player.getItem(Type.CHEST).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                playerBattleArmor.setImageResource(resId);
        }

        if(player.getItem(Type.STOMACH) != null)
        {
            resId = getResources().getIdentifier(player.getItem(Type.STOMACH).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                playerBattleLegs.setImageResource(resId);
        }

        if(player.getItem(Type.LEGS) != null)
        {
            resId = getResources().getIdentifier(player.getItem(Type.LEGS).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                playerBattleFeet.setImageResource(resId);
        }

        if(player.getItem(Type.WEAPON) != null)
        {
            resId = getResources().getIdentifier(player.getItem(Type.WEAPON).getBigIcon(), "drawable", getPackageName());
            if (resId != 0)
                playerBattleWeapon.setImageResource(resId);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.battle_, menu);
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
