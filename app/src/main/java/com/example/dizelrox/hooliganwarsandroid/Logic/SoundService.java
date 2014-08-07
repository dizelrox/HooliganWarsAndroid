package com.example.dizelrox.hooliganwarsandroid.Logic;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;

import com.example.dizelrox.hooliganwarsandroid.R;

/**
 * Created by DizelRox on 31-Jul-14.
 */
public class SoundService extends IntentService {

    MediaPlayer player;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SoundService(String name) {
        super("SoundService");
    }

    public SoundService()
    {
        super("SoundService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Boolean isLooping = intent.getBooleanExtra("isLooping",false);
        String trackName = intent.getStringExtra("trackName");
        int resId = getResources().getIdentifier(trackName, "raw", getPackageName());
        player = MediaPlayer.create(this,resId);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        player.start();
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }
}
