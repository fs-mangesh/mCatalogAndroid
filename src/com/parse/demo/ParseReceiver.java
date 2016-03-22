package com.parse.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;

import java.util.List;

/**
 * Created by afourtest on 1/2/15.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver {

    private List<ParseObject> mDevices;
    @Override
    public void onPushOpen(Context context, Intent intent) {
        boolean isAdmin = false;
        String mUsername = "";
        SharedPreferences sharedpreferences = context.getSharedPreferences("com.parse.demo", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("isAdmin"))
        {
            isAdmin = sharedpreferences.getBoolean("isAdmin",false);

        }
        if(sharedpreferences.contains("currentUser"))
        {
            mUsername = sharedpreferences.getString("currentUser","unknown");
        }
        Log.e("Push", "Clicked");
        Intent i = new Intent(context, InitialListViewActivity.class);
        i.putExtra("isAdmin", isAdmin);
        i.putExtra("username", mUsername);
        i.setAction("push");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}


