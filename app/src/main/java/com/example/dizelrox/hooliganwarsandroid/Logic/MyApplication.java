package com.example.dizelrox.hooliganwarsandroid.Logic;

import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.util.Locale;

/**
 * Created by dima on 7/12/2014.
 */
public class MyApplication extends Application {

    public static TextToSpeech ttobj;

    @Override
    public void onCreate() {
        super.onCreate();
        ttobj=new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            ttobj.setLanguage(Locale.US);
                        }
                    }
                });
    }
}
