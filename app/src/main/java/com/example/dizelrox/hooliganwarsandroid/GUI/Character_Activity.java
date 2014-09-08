package com.example.dizelrox.hooliganwarsandroid.GUI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dizelrox.hooliganwarsandroid.Logic.Armor;
import com.example.dizelrox.hooliganwarsandroid.Logic.GameInitialize;
import com.example.dizelrox.hooliganwarsandroid.Logic.Item;
import com.example.dizelrox.hooliganwarsandroid.Logic.MyApplication;
import com.example.dizelrox.hooliganwarsandroid.Logic.Player;
import com.example.dizelrox.hooliganwarsandroid.Logic.SoundService;
import com.example.dizelrox.hooliganwarsandroid.Logic.Type;
import com.example.dizelrox.hooliganwarsandroid.Logic.Weapon;
import com.example.dizelrox.hooliganwarsandroid.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Locale;

public class Character_Activity extends Activity {

    Armor[] armArray;
    Weapon[] wepArray;
    Player player;
    public static GameInitialize game;
    ImageButton armorLabelsArray[] = new ImageButton[12];

    ImageButton weaponLabelsArray[] = new ImageButton[8];

    ImageButton headImage;
    ImageButton chestImage;
    ImageButton stomachImage;
    ImageButton legsImage;
    ImageButton weaponImage;
    ImageView playerIcon;

    TextView playerStat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_activity);


        this.player = (Player) getIntent().getSerializableExtra("player");
        this.game = new GameInitialize(player.getCurrentArmorsArray(), player.getCurrentWeaponsArray());

        this.playerIcon = (ImageView) findViewById(R.id.playerShopIcon);

        this.armArray = game.getArmor();
        this.wepArray = game.getWeapon();

        int resId = getResources().getIdentifier(player.getPlayerIcon(), "drawable", getPackageName());
        if (resId != 0)
            playerIcon.setImageResource(resId);

        this.playerStat = (TextView) findViewById(R.id.playerStat);

        updateStockItemsInUseByPlayer();

        findAllImageButtons();
        setShopIcons();

        MyApplication.ttobj.speak("Welcome " + player.getName() + ". Pick items wisely. Your enemy will show no mercy!", TextToSpeech.QUEUE_FLUSH, null);

        final ImageButton character_mute = (ImageButton) findViewById(R.id.character_mute);
        character_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.muteClicked(character_mute);
            }
        });

        playerStat.setText(player.getStats());
        checkIfWearingItems();
    }

    public void startBattle(View v)
    {
        Intent battleIntent = new Intent(this,Battle_Activity.class);
        battleIntent.putExtra("player",player);
        MyApplication.ttobj.stop();
        startActivity(battleIntent);
    }

    private void checkIfWearingItems()
    {
        for(int i=0;i<=4;i++)
        {
            Item item = player.getItem(Type.getType(i));
            if(item != null)
            {
                if(item.getItemType() != Type.WEAPON)
                {
                    for(int j=0;j<12;j++)
                    {
                        if(armArray[j].getName().compareTo(item.getName()) == 0)
                        {
                            player.setItemNull(item.getItemType());
                            player.setItem(armArray[j]);
                            player.getItem(Type.getType(i)).inUse = false;
                            String s = player.getItem(Type.getType(i)).getBigIcon();
                            changeLabelBigIcon(player.getItem(Type.getType(i)));
                            player.getItem(Type.getType(i)).inUse = true;
                            break;
                        }
                    }
                }
                else
                {
                    for(int j=0;j<8;j++)
                    {
                        if(wepArray[j].getName().compareTo(item.getName()) == 0)
                        {
                            player.setItemNull(item.getItemType());
                            player.setItem(wepArray[j]);
                            player.getItem(Type.getType(i)).inUse = false;
                            changeLabelBigIcon(player.getItem(Type.getType(i)));
                            player.getItem(Type.getType(i)).inUse = true;
                            break;
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.character_, menu);
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

    private void updateStockItemsInUseByPlayer()
    {
        for (int i = 0; i < this.armArray.length; i++)
        {
            if ((player.getItem(Type.HEAD) != null && armArray[i].getName().equals(player.getItem(Type.HEAD).getName()))
                    || (player.getItem(Type.CHEST) != null && armArray[i].getName().equals(player.getItem(Type.CHEST).getName()))
                    || (player.getItem(Type.STOMACH) != null && armArray[i].getName().equals(player.getItem(Type.STOMACH).getName()))
                    || (player.getItem(Type.LEGS) != null && armArray[i].getName().equals(player.getItem(Type.LEGS).getName())))
            {
                player.setItem(armArray[i]);
                armArray[i].inUse = true;
            }
        }

        for (int i = 0; i < this.wepArray.length; i++)
        {
            if (player.getItem(Type.WEAPON) != null && wepArray[i].getName().equals(player.getItem(Type.WEAPON).getName()))
            {
                player.setItem(wepArray[i]);
                wepArray[i].inUse = true;
                break;
            }
        }
    }

    public void logout(View v)
    {
        new SaveAsync().execute();
    }

    public class SaveAsync extends AsyncTask<Void,String,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket gameServer = new Socket("dizel-services.ddns.net", 55555);
                ObjectOutputStream gameServerOutput = new ObjectOutputStream(gameServer.getOutputStream());
                ObjectInputStream gameServerInput = new ObjectInputStream(gameServer.getInputStream());
                String message = "Save Progress";
                gameServerOutput.writeObject(message);
                gameServerOutput.writeObject(player);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
            Intent mainMenu = new Intent(Character_Activity.this,Main_Activity.class);
            startActivity(mainMenu);
            super.onPostExecute(aVoid);
        }
    }

    private void findAllImageButtons()
    {
        for(int i=0;i<12;i++)
        {
            int resId = getResources().getIdentifier("armorLabel"+i, "id", getPackageName());
            if (resId != 0)
                armorLabelsArray[i] = (ImageButton) findViewById(resId);
        }

        for(int i=0;i<8;i++)
        {
            int resId = getResources().getIdentifier("weaponLabel"+i, "id", getPackageName());
            if (resId != 0)
                weaponLabelsArray[i] = (ImageButton) findViewById(resId);
        }

        headImage = (ImageButton) findViewById(R.id.headImage);
        chestImage = (ImageButton) findViewById(R.id.chestImage);
        stomachImage = (ImageButton) findViewById(R.id.stomachImage);
        legsImage = (ImageButton) findViewById(R.id.legsImage);
        weaponImage = (ImageButton) findViewById(R.id.weaponImage);
    }

    private void setShopIcons()
    {
        for(int i=0;i<12;i++) {
            int resId = getResources().getIdentifier(armArray[i].getSmallIcon(), "drawable", getPackageName());
            String s = armArray[i].getSmallIcon();
            if (resId != 0)
                armorLabelsArray[i].setImageResource(resId);
        }

        for(int i=0;i<8;i++) {
            int resId = getResources().getIdentifier(wepArray[i].getSmallIcon(), "drawable", getPackageName());
            if (resId != 0)
                weaponLabelsArray[i].setImageResource(resId);
        }
    }

    public void tryToWearItem(View v)
    {
        ImageButton button = (ImageButton) v;
        String buttonIndex = (String) v.getTag();
        Item item;

        if(Integer.parseInt(buttonIndex) < 12)
            item = armArray[Integer.parseInt(buttonIndex)];
        else
            item = wepArray[Integer.parseInt(buttonIndex)-12];

        if (player.getItem(item.getItemType()) == null)
        {

            if (item.isAvalible() && item.inUse == false)
            {
                changeLabelBigIcon(item);
                player.setItem(item);
                changeLabelSmallIcon(button, item);
                playerStat.setText(player.getStats());

            }

        }
        else
            System.out.println("already wearing this type");
    }

    @Override
    protected void onResume() {
        setShopIcons();
        super.onResume();
    }

    public void changeLabelBigIcon(Item item)
    {
        Type type = item.getItemType();
        switch (type)
        {
            case HEAD: {
                int resId = getResources().getIdentifier(item.getSmallIcon(), "drawable", getPackageName());
                System.out.println(item.getSmallIcon());
                if (resId != 0)
                headImage.setImageResource(resId);
                else
                    System.out.println("lick my lollypop");
                break;
            }
            case CHEST: {
                int resId = getResources().getIdentifier(item.getSmallIcon(), "drawable", getPackageName());
                if (resId != 0)
                    chestImage.setImageResource(resId);
                break;
            }
            case STOMACH: {
                int resId = getResources().getIdentifier(item.getSmallIcon(), "drawable", getPackageName());
                if (resId != 0)
                    stomachImage.setImageResource(resId);
                break;
            }
            case LEGS: {
                int resId = getResources().getIdentifier(item.getSmallIcon(), "drawable", getPackageName());
                if (resId != 0)
                    legsImage.setImageResource(resId);
                break;
            }
            case WEAPON: {
                int resId = getResources().getIdentifier(item.getSmallIcon(), "drawable", getPackageName());
                if (resId != 0)
                    weaponImage.setImageResource(resId);
                break;
            }
        }

    }

    private void changeLabelSmallIcon(ImageButton button, Item item)
    {
        System.out.println(item.getSmallIcon());
        int resId = getResources().getIdentifier(item.getSmallIcon(), "drawable", getPackageName());
        if (resId != 0)
            button.setImageResource(resId);
    }

    public void unwearItem(View v)
    {
        ImageButton button = (ImageButton) v;
        int itemType = Integer.parseInt((String) v.getTag());
        Type t = Type.getType(itemType);
        if (player.getItem(Type.getType(itemType)) != null)
        {
            player.getItem(t).inUse = false;
            player.setItemNull(t);
            button.setImageResource(R.drawable.default_big_image);
            setShopIcons();
            playerStat.setText(player.getStats());
        }
    }
}
