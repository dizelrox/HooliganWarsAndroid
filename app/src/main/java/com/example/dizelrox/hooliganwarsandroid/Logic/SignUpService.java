package com.example.dizelrox.hooliganwarsandroid.Logic;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.example.dizelrox.hooliganwarsandroid.GUI.Character_Activity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

/**
 * Created by DizelRox on 18-Jul-14.
 */
public class SignUpService extends IntentService {

    Handler mHandler;

    public SignUpService(String name) {
        super(name);
    }

    public SignUpService() {
        super("SignUpService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
        String login = intent.getStringExtra("login");
        String password = intent.getStringExtra("password");
        Player p = (Player) intent.getSerializableExtra("player");
        String output = null;


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(p);

        String link = ("http://dizel-services.ddns.net:8080/HW_Servlet/AddUserToDB?" +
                "jsonObject="+URLEncoder.encode(json,"UTF-8")+
                "&login="+URLEncoder.encode(login,"UTF-8")+
                "&password="+URLEncoder.encode(password,"UTF-8"));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(link);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
            System.out.println(output);

            if(output.compareTo("Added player successfully\r\n") == 0) {
                mHandler.post(new Runnable() {
                    //      @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(), "Added player successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Activity kill = (Activity) intent.getSerializableExtra("previousActivity");

                Intent characterActivity = new Intent(this, Character_Activity.class);
                characterActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(characterActivity);
                kill.finish();
            }
            else
                mHandler.post(new Runnable() {
                    //      @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(), "Failed to add player",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}