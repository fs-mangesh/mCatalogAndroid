package com.parse.demo;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;


/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ParseAnalyticsService extends IntentService {


    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.parse.demo.service.receiver";
    JSONArray finalResultArr =null;
    public static final String EXTRA_MESSENGER="com.parse.demo.service.EXTRA_MESSENGER";

    public ParseAnalyticsService() {
        super("ParseAnalyticsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.addHeader("X-Parse-Application-Id", "i48dw0oV5E2prYLcPIf4ZEYS1G8PXlWa4sqC3g56");
        request.addHeader("X-Parse-REST-API-Key", "OCQb1QWKGP3SsKwoEDqoc8uFygptO28oTcPBA3nV");
        try {

            JSONObject condition = new JSONObject();
            JSONObject deviceStatus = new JSONObject();
            String s = "";
            String s1 = "";


            try {
//                HashMap<String,String> map1 = new HashMap<String,String>();
//                map1.put("deviceMake","Android");
//                map1.put("deviceStatus","Free");
                condition.put("deviceMake","Apple");

                condition.put("deviceStatus","Free");
//                JSONArray jsonArray = new JSONArray();
//                jsonArray.put(condition);
//                jsonArray.put(deviceStatus);

                try {
                    s = URLEncoder.encode(condition.toString(), "UTF-8");


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //String finalStr = "https://api.parse.com/1/classes/Devices"+"?where="+s;
            String finalStr = "https://api.parse.com/1/classes/Devices";
            URI tempLink = new URI(finalStr);
            String s2 = tempLink.toASCIIString();
            URI link = new URI(s2);

            Log.d("com.parse.demo","final Link"+link.toString());
            request.setURI(link);
            try {

                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                JSONTokener tokener = new JSONTokener(builder.toString());
                try {
                    JSONObject finalResult = new JSONObject(tokener);
                    finalResultArr = finalResult.getJSONArray("results");
                    Log.d("com.parse.demo","json response"+finalResultArr);
                    if(finalResultArr != null)
                    {
                        Bundle extras = intent.getExtras();
                        if(extras != null)
                        {
                            Messenger messenger=(Messenger)extras.get(EXTRA_MESSENGER);
                            Message msg=Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putString(RESULT,finalResultArr.toString());
                            msg.setData(bundle);
                            try {
                                messenger.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
