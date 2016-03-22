package com.parse.demo;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ToDoListApplication extends Application {

	String deviceToken;
    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
	public void onCreate() {
		super.onCreate();

    // Initialize crash reporting.
    ParseCrashReporting.enable(this);

		Parse.initialize(this, "i48dw0oV5E2prYLcPIf4ZEYS1G8PXlWa4sqC3g56", "sO1FIq3N2sQgWfHHYggrDsF5regL8mr8yQ5vsglZ");


		ParseUser.enableAutomaticUser();
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParsePush.subscribeInBackground("", new SaveCallback() {
			  @Override
			  public void done(ParseException e) {
			    if (e == null) {
			      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
			     deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
			     
			      //Log.d("com.parse.push", deviceToken);
			    } else {
			      Log.e("com.parse.push", "failed to subscribe for push", e);
			    }
			  }
			});
		
	}



}
