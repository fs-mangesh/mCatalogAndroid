package com.parse.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InitialListViewActivity extends Activity {
    ListView list;
    boolean isAdmin;
    boolean isCheckedOut;
    private boolean isManager;
    InitialListViewAdapter adapter;
    public InitialListViewActivity CustomListView = null;
    public ArrayList<InitialDataModel> CustomListViewValuesArr = new ArrayList<InitialDataModel>();
    JSONObject obj;
    JSONObject m_jArryObj;
    JSONArray m_jArry;
    String mUsername;
    private List<ParseObject> mDevices;
    private Dialog mProgressDialog;
    Date mExpectedCheckinDate;
    Resources res;
    String mREgNumber;
    TextView mTxtCountdown;
    private CountDownTimer countDownTimer;
    private String mAction;
    SharedPreferences prefs;

    /*
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
     *
     * @"No"); if(mIsCheckedOut) { self.mExpectedCheckinDate = [object
     * valueForKey:@"expectedCheckinDate"]; } } else { mIsCheckedOut = NO;
     * NSLog(mIsCheckedOut ? @"Yes" : @"No"); }
     *
     *
     * } else { mIsCheckedOut = NO; NSLog(mIsCheckedOut ? @"Yes" : @"No"); }
     */
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {
            // Gets the current list of todos in sorted order
            if(!isManager)
            {
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
                            isCheckedOut = lDevice.getBoolean("isCheckedOut");
                            if (isCheckedOut) {
                                mExpectedCheckinDate = lDevice
                                        .getDate("expectedCheckinDate");
                            }
                        }
                    } else {
                        Log.d("com.parse.demo", "mDevices"+mDevices.size());
                        isCheckedOut = false;
                    }

                } catch (ParseException e) {
                    Log.d("score", "Error: " + e.getMessage());
                }

            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            InitialListViewActivity.this.mProgressDialog = ProgressDialog.show(
                    InitialListViewActivity.this, "", "Loading...", true);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            // Put the list of todos into the list view
            try {
                obj = new JSONObject(loadJSONFromAsset());
                m_jArryObj = obj.optJSONObject("storelocator");
                m_jArry = m_jArryObj.getJSONArray("item");

                // String deviceToken = (String)
                // ParseInstallation.getCurrentInstallation().get("deviceToken");
                JSONArray l_arr = new JSONArray();
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject row = m_jArry.getJSONObject(i);
                    String role = row.getString("role");
                    String status = row.getString("status");
                    if (isManager)
                    {
                        if (role.equals("manager")) {
                            l_arr = row.getJSONArray("data");
                        }
                    }

                    else if (isAdmin & !isCheckedOut) {
                        if (role.equals("admin") & (status.equals("checkout"))) {
                            l_arr = row.getJSONArray("data");
                        }
                    } else if (isAdmin & isCheckedOut) {
                        if (role.equals("admin")
                                & (status.equals("checkoutDone"))) {
                            l_arr = row.getJSONArray("data");
                        }
                    } else if (!isAdmin) {
                        if (role.equals("user")) {
                            l_arr = row.getJSONArray("data");
                        }

                    }
                    // else if (!isAdmin & isCheckedOut) {
                    // if (role.equals("user")
                    // & (status.equals("checkoutDone"))) {
                    // l_arr = row.getJSONArray("data");
                    // }
                    // }

                }
                setListData(l_arr);
                /**************** Create Custom Adapter *********/
                adapter = new InitialListViewAdapter(CustomListView,
                        CustomListViewValuesArr, res);
                list.setAdapter(adapter);

                if(isCheckedOut && !(mAction == ""))
                {
                    if(countDownTimer != null)
                        countDownTimer.cancel();
                    Log.d("com.parse.demo","isCheckedOut "+isCheckedOut +" mAction"+mAction);
                    countDownTimer = new MyCountDown(mExpectedCheckinDate.getTime()-new Date().getTime(), 1 * 1000);
                    countDownTimer.start();
                }
                else if(mAction =="checkin")
                {
                    if(countDownTimer != null)
                        countDownTimer.cancel();
                    mTxtCountdown.setText("Home");
                }

                Log.d("this is my array", "arr: " + m_jArry.toString());
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            InitialListViewActivity.this.mProgressDialog.dismiss();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            Log.d("com.parse.demo","Action "+intent.getAction());
            final String intentAction = intent.getAction();
            if (intent.getAction().equalsIgnoreCase("push")) {
                Log.d("com.parse.demo", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }
        mTxtCountdown = (TextView) findViewById(R.id.screenName);
        mTxtCountdown.setText("Home");
        CustomListView = this;

        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isAdmin = extras.getBoolean("isAdmin");
            isManager = extras.getBoolean("isManager");
            mUsername = extras.getString("username");
        }

        new RemoteDataTask().execute();
        res = getResources();
        list = (ListView) findViewById(R.id.list); // List defined in XML ( See
        // Below )
        Log.d("com.parse.demo","isCheckedOut "+isCheckedOut);
        prefs = this.getSharedPreferences(
                "com.parse.demo", Context.MODE_PRIVATE);

    }

    /****** Function to set data in ArrayList *************/
    public void setListData(JSONArray jsonArr) {
        Resources res = getResources();
        CustomListViewValuesArr.clear();
        for (int i = 0; i < jsonArr.length(); i++) {

            final InitialDataModel sched = new InitialDataModel();
            try {
                JSONObject row = jsonArr.getJSONObject(i);
                /******* Firstly take data in model object ******/
                sched.setMainText(row.getString("heading"));
                sched.setImage(row.getString("image"));
                sched.setSubText(row.getString("subheading"));

                /******** Take Model Object in ArrayList **********/
                CustomListViewValuesArr.add(sched);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /***************** This function used by adapter ****************/
    public void onItemClick(int mPosition) {
        InitialDataModel tempValues = (InitialDataModel) CustomListViewValuesArr
                .get(mPosition);

        if (isAdmin & !isCheckedOut) {
            switch (mPosition) {
                case 0: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            CheckoutActivity.class);
                    mAction ="register";
                    intent.putExtra("action", "register");
                    intent.putExtra("username", mUsername);
                    startActivity(intent);
                }

                break;
                case 1: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            AdminDailyActivity.class);
                    intent.putExtra("action", "search");
                    intent.putExtra("username", mUsername);
                    startActivity(intent);

                }

                break;
                case 2: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            AdminDateHistoryActivity.class);
                    intent.putExtra("action", "search");
                    intent.putExtra("username", mUsername);
                    startActivity(intent);

                }

                break;
                case 3: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            CheckoutActivity.class);
                    intent.putExtra("action", "checkout");
                    intent.putExtra("username", mUsername);
                    startActivityForResult(intent, 1);

                }

                break;
                case 4: {
                    this.finish();
                }

                break;
                default:
                    break;
            }
        } else if (isAdmin & isCheckedOut) {
            switch (mPosition) {
                case 0: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            AdminDailyActivity.class);
                    intent.putExtra("action", "search");
                    intent.putExtra("username", mUsername);
                    startActivity(intent);
                }

                break;
                case 1: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            AdminDateHistoryActivity.class);
                    intent.putExtra("action", "search");
                    intent.putExtra("username", mUsername);
                    startActivity(intent);

                }

                break;
                case 2: {

                    checkinCurrentDevice();
                }

                break;
                case 3: {
                    this.finish();

                }

                break;
                case 4:

                    break;
                default:
                    break;
            }
        } else if (!isAdmin && isCheckedOut &&!isManager) {
            switch (mPosition) {
                case 0: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            CheckoutActivity.class);
                    intent.putExtra("action", "extend");
                    intent.putExtra("username", mUsername);
                    startActivityForResult(intent, 1);
                }

                break;
                case 1: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            CheckoutActivity.class);
                    intent.putExtra("action", "retain");
                    intent.putExtra("username", mUsername);
                    startActivityForResult(intent, 1);
                }
                break;
                case 2: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            SearchDevicesActivity.class);
                    intent.putExtra("action", "search");
                    intent.putExtra("username", mUsername);
                    startActivity(intent);
                }

                break;
                case 3: {
                    this.finish();

                }

                break;
                case 4:

                    break;
                default:
                    break;
            }
        }

        else if (!isAdmin & !isCheckedOut &&!isManager) {
            showAlertDialog("Ask admin to checkout the device!!");
        }
        else if(isManager)
        {
            switch (mPosition) {
                case 0: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            ParseAnalyticsActivity.class);

                    startActivity(intent);
                }

                break;
                case 1: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            AdminDailyActivity.class);
                    intent.putExtra("action", "search");
                    intent.putExtra("username", mUsername);
                    startActivity(intent);

                }

                break;
                case 2: {
                    Intent intent = new Intent(InitialListViewActivity.this,
                            AdminDateHistoryActivity.class);
                    intent.putExtra("action", "search");
                    intent.putExtra("username", mUsername);
                    startActivity(intent);

                }

                break;
                case 3: {
                    this.finish();

                }

                break;
                case 4:

                    break;
                default:
                    break;
            }


        }
        // SHOW ALERT

        Toast.makeText(
                CustomListView,
                "" + tempValues.getMainText() + " Image:"
                        + tempValues.getImage() + " Url:"
                        + tempValues.getSubText(), Toast.LENGTH_LONG).show();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("storeData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public void checkinCurrentDevice() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Devices");
        query.whereEqualTo("deviceId", (String) ParseInstallation
                .getCurrentInstallation().get("deviceToken"));

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + list.size() + " scores");
                    if (list.size() != 0) {
                        ParseObject device = list.get(0);
                        if (device.get("deviceStatus").equals("Busy")) {
                            Log.d("com.parse.demo", (String) ParseInstallation
                                    .getCurrentInstallation()
                                    .get("deviceToken"));
                            Log.d("com.parse.demo", android.os.Build.MODEL);
                            device.put("deviceId", (String) ParseInstallation
                                    .getCurrentInstallation()
                                    .get("deviceToken"));
                            device.put("deviceName", android.os.Build.MODEL);
                            device.put("checkoutDate", JSONObject.NULL);
                            device.put("deviceCurrentUser", "");
                            device.put("deviceMake", "Android");
                            device.put("deviceModel", android.os.Build.DEVICE);
                            device.put("deviceStatus", "Free");
                            device.put(
                                    "deviceToken",
                                    (String) ParseInstallation
                                            .getCurrentInstallation().get(
                                                    "deviceToken"));
                            device.put("isCheckedOut", false);
                            device.put("deviceVersion",
                                    android.os.Build.VERSION.RELEASE);
                            device.put("checkinDate", new Date());

                            device.put("expectedCheckinDate", JSONObject.NULL);
                            mREgNumber = device.getString("registrationNumber");
                            device.saveInBackground(new SaveCallback() {

                                @Override
                                public void done(ParseException e) {
                                    // TODO Auto-generated method stub
                                    if (e == null) {
                                        showAlertDialog("Device Checked out successfully!");
                                        addTransactionToHistory(mUsername,
                                                "checkin", mREgNumber);
                                        mAction = "checkin";

                                        new RemoteDataTask().execute();
                                        adapter.notifyDataSetChanged();

                                    } else {
                                        Log.d("score",
                                                "Error: " + e.getMessage());
                                    }

                                }
                            });

                        } else {
                            String msg = "The device is already checked out by "
                                    + device.get("deviceCurrentUser")
                                    + ".Please let him check in!";
                            showAlertDialog(msg);
                        }

                    } else {
                        showAlertDialog("Ask Admin to register the device!!");

                    }

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void addTransactionToHistory(String username, String action,
                                        String registrationNumber) {
        ParseObject device = new ParseObject("DeviceHistory");
        device.put("deviceId", (String) ParseInstallation
                .getCurrentInstallation().get("deviceToken"));
        device.put("deviceName", android.os.Build.MODEL);
        device.put("deviceUser", username);
        device.put("deviceMake", "Android");
        device.put("deviceModel", android.os.Build.DEVICE);
        device.put("deviceToken", (String) ParseInstallation
                .getCurrentInstallation().get("deviceToken"));
        device.put("deviceVersion", android.os.Build.VERSION.RELEASE);
        device.put("registrationNumber", registrationNumber);
        if (action.equalsIgnoreCase("checkout")) {
            device.put("checkoutDate", new Date());
        } else if (action.equalsIgnoreCase("checkin")) {
            device.put("checkinDate", new Date());
        }
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yy");
        sdfDate.format(new Date());
        device.put("deviceDate", sdfDate.format(new Date()));
        device.put("action", action);
        device.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                // TODO Auto-generated method stub
                if (e == null) {
                    // showAlertDialog("Device Registered successfully!");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }

            }

        });
    }

    public void showAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                InitialListViewActivity.this);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("com.parse.demo", "result code"+resultCode);
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                mAction = "checkout";
                new RemoteDataTask().execute();
                adapter.notifyDataSetChanged();
//                Bundle b = data.getExtras();
//                mExpectedCheckinDate = (Date)b.get("extendDuration");
//                Log.d("com.parse.demo", "test"+data.getStringExtra("test"));
//                Log.d("com.parse.demo","extend"+data.getLongExtra("extend",0));
//                mExpectedCheckinDate = new Date(data.getLongExtra("extend",0));
//                countDownTimer = new MyCountDown(mExpectedCheckinDate.getTime()-new Date().getTime(), 1 * 1000);
//                countDownTimer.start();
                // Update List
            }
            if (resultCode == RESULT_CANCELED) {
                // Do nothing?
            }
        }
    }//

    public class MyCountDown extends CountDownTimer {

        public MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
            mTxtCountdown.setText(hms);
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            mTxtCountdown.setText("Time's up!");
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        Editor editor = prefs.edit();
        editor.putBoolean("isAdmin", isAdmin);
        editor.putString("currentUser", mUsername);
        editor.commit();
    }
}
