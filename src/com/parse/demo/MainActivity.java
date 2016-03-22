package com.parse.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends Activity {
    Button loginbutton;
    Button signup;
    Button forgotPasswd;
    String usernametxt;
    String passwordtxt;
    EditText password;
    EditText username;
    boolean isCheckedOutByCurrentUser;
    private List<ParseObject> mDevices;
    private Dialog mProgressDialog;
    private boolean isAdmin;
    private boolean isManager;
    private String companyName;


    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {

            if(!isManager)
            {
                // Gets the current list of todos in sorted order
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Devices");
                query.whereEqualTo("deviceId", (String) ParseInstallation
                        .getCurrentInstallation().get("deviceToken"));
                try {
                    mDevices = query.find();
                    if (mDevices.size() != 0) {
                        Log.d("com.parse.demo", "mDevices"+mDevices.size());
                        ParseObject lDevice = mDevices.get(0);
                        {
                            if(isAdmin)
                            {
                                isCheckedOutByCurrentUser = true;
                            }
                            else if (lDevice.getBoolean("isCheckedOut") && lDevice.getString("deviceCurrentUser").equalsIgnoreCase(usernametxt)) {
                                isCheckedOutByCurrentUser = true;
                            }
                        }
                    } else {
                        Log.d("com.parse.demo", "mDevices"+mDevices.size());
                        isCheckedOutByCurrentUser = isAdmin;
                    }

                }
                catch (ParseException e) {
                    Log.d("score", "Error: " + e.getMessage());
                }

            }
            else
            {
                isCheckedOutByCurrentUser = true;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            MainActivity.this.mProgressDialog = ProgressDialog.show(
                    MainActivity.this, "", "Loading...", true);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            // Put the list of todos into the list view


            MainActivity.this.mProgressDialog.dismiss();
            if(isCheckedOutByCurrentUser)
            {
                Intent intent = new Intent(
                        MainActivity.this,
                        InitialListViewActivity.class);
                intent.putExtra("isAdmin", isAdmin);
                intent.putExtra("isManager",isManager);
                intent.putExtra("username", usernametxt);
                intent.setAction("test");
                startActivity(intent);
            }
            else
            {
                showAlertDialog("This device is not registered on your username. Please ask admin to checkout this device.");
            }


        }
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from main.xml
        setContentView(R.layout.activity_main);
        // Locate EditTexts in main.xml
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        // Locate Buttons in main.xml
        loginbutton = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);
        forgotPasswd =  (Button)findViewById(R.id.btnfrgtPasswd);
        // Login Button Click Listener
        loginbutton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();

                // Send data to Parse.com for verification
                ParseUser.logInInBackground(usernametxt, passwordtxt,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {

                                    isAdmin = user.getBoolean("isAdmin");
                                    isManager = user.getBoolean("isManager");
                                    ToDoListApplication application = (ToDoListApplication)getApplicationContext();
                                    application.setCompanyName(user.getString("companyName"));


                                    new RemoteDataTask().execute();


                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "No such user exist, please signup",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        // Sign up Button Click Listener
        signup.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {

                Intent intent = new Intent(
                        MainActivity.this,
                        SignUpActivity.class);
                startActivity(intent);


            }
        });

        forgotPasswd.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {

                Intent intent = new Intent(
                        MainActivity.this,
                        ForgotPasswordActivity.class);
                startActivity(intent);


            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // if (id == R.id.action_settings) {
        // return true;
        // }
        return super.onOptionsItemSelected(item);
    }

	
	/*
	 * -(void)checkIfAdminAndCheckeOutStatus {
	 * 
	 * 
	 * PFQuery *query = [PFQuery queryWithClassName:@"Devices"];
	 * NSLog(@"deviceID %@",[[[UIDevice currentDevice] identifierForVendor]
	 * UUIDString]); [query whereKey:@"deviceId" equalTo:[[[UIDevice
	 * currentDevice] identifierForVendor] UUIDString]];//
	 * 
	 * NSArray *objects = [query findObjects];
	 * NSLog(@"Successfully retrieved %lu scores.", (unsigned
	 * long)objects.count); if(objects.count!=0) { PFObject *object = [objects
	 * objectAtIndex:0]; if([mUsername isEqualToString:[object
	 * valueForKey:@"deviceCurrentUser"]]) { mIsCheckedOut = [[object
	 * valueForKey:@"isCheckedOut"]boolValue]; NSLog(mIsCheckedOut ? @"Yes" :
	 * @"No"); if(mIsCheckedOut) { self.mExpectedCheckinDate = [object
	 * valueForKey:@"expectedCheckinDate"]; } } else { mIsCheckedOut = NO;
	 * NSLog(mIsCheckedOut ? @"Yes" : @"No"); }
	 * 
	 * 
	 * } else { mIsCheckedOut = NO; NSLog(mIsCheckedOut ? @"Yes" : @"No"); }
	 * 
	 * 
	 * // [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError
	 * *error) { // if (!error) { // // The find succeeded. //
	 * NSLog(@"Successfully retrieved %lu scores.", (unsigned
	 * long)objects.count); // if(objects.count!=0) // { // PFObject *object =
	 * [objects objectAtIndex:0]; // if([mUsername isEqualToString:[object
	 * valueForKey:@"deviceCurrentUser"]]) // { // mIsCheckedOut = [[object
	 * valueForKey:@"isCheckedOut"]boolValue]; // NSLog(mIsCheckedOut ? @"Yes" :
	 * @"No"); // } // else // { // mIsCheckedOut = NO; // NSLog(mIsCheckedOut ?
	 * @"Yes" : @"No"); // } // // } // else // { // mIsCheckedOut = NO; //
	 * NSLog(mIsCheckedOut ? @"Yes" : @"No"); // } // // // } // }]; }
	 */

    public void showAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);
        alertDialogBuilder.setTitle("Device Tracker");
        alertDialogBuilder.setMessage(msg).setCancelable(true)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
