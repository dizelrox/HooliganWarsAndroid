package com.example.dizelrox.hooliganwarsandroid.Logic;

import android.app.Application;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;

import com.example.dizelrox.hooliganwarsandroid.R;

/**
 * Created by dima on 7/12/2014.
 */
public class MyApplication extends Application {

    public static TextToSpeech ttobj;
    public static MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static void muteClicked(View v) {
        if (MyApplication.player.isPlaying()) {
            MyApplication.player.pause();
            ImageButton b = (ImageButton) v;
            b.setImageResource(R.drawable.sound_no_volume);
        } else {
            MyApplication.player.start();
            ImageButton b = (ImageButton) v;
            b.setImageResource(R.drawable.sound_volume);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        player.stop();
        player.release();
    }


}
