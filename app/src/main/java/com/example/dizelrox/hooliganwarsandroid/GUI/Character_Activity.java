package com.example.dizelrox.hooliganwarsandroid.GUI;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.dizelrox.hooliganwarsandroid.Logic.Armor;
import com.example.dizelrox.hooliganwarsandroid.Logic.GameInitialize;
import com.example.dizelrox.hooliganwarsandroid.Logic.Item;
import com.example.dizelrox.hooliganwarsandroid.Logic.MyApplication;
import com.example.dizelrox.hooliganwarsandroid.Logic.Player;
import com.example.dizelrox.hooliganwarsandroid.Logic.Type;
import com.example.dizelrox.hooliganwarsandroid.Logic.Weapon;
import com.example.dizelrox.hooliganwarsandroid.R;

import java.util.Locale;

public class Character_Activity extends Activity {

    Armor[] armArray;
    Weapon[] wepArray;
    Player player;
    GameInitialize game;
    ImageButton armorLabelsArray[] = new ImageButton[12];

    ImageButton weaponLabelsArray[] = new ImageButton[8];

    ImageButton headImage;
    ImageButton chestImage;
    ImageButton stomachImage;
    ImageButton legsImage;
    ImageButton weaponImage;
    ImageView playerIcon;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_activity);


        this.player = (Player) getIntent().getSerializableExtra("player");
        this.game = new GameInitialize(player.getCurrentArmorsArray(), player.getCurrentWeaponsArray());

        this.armArray = game.getArmor();
        this.wepArray = game.getWeapon();

        this.playerIcon = (ImageView) findViewById(R.id.playerShopIcon);
        int resId = getResources().getIdentifier(player.getPlayerIcon(), "drawable", getPackageName());
        if (resId != 0)
            playerIcon.setImageResource(resId);


        updateStockItemsInUseByPlayer();

        findAllImageButtons();
        setShopIcons();

        MyApplication.ttobj.speak("Welcome " + player.getName() + ". Pick items wisely. Your enemy will show no mercy!", TextToSpeech.QUEUE_FLUSH, null);
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

            }

        }
        else
            System.out.println("already wearing this type");
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
        }
    }
}
