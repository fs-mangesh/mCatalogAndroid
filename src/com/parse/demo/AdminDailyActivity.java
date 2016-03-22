package com.parse.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AdminDailyActivity extends Activity {

    private List<ParseObject> mDeviceHistory;
    private Dialog mProgressDialog;
    ParseObject m_jArryObj;
    JSONArray m_jArry;
    ArrayList<ParseObject> mArrDeviceHistory;
    public ArrayList<DataModelDeviceHistory> customListViewValuesArr = new ArrayList<DataModelDeviceHistory>();
    ListView list;
    AdminDailyActivityAdapter mAdapter;
    public AdminDailyActivity CustomListView = null;
    Resources res;
    CustomSpinner mSpinName;
    String mSelectedStatus;
    String mSelectedDevice;
    LinearLayout mHeaderView;
    ArrayList<String> mArrModel;
    ArrayList<ParseObject> mFilteredArrDevices;
    boolean isSearchClickable;
    Button btnSearch;

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {
            // Gets the current list of todos in sorted order
            ToDoListApplication application = (ToDoListApplication)getApplicationContext();
            String companyName = application.getCompanyName();
            if(companyName != "" || companyName != null)
            {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "DeviceHistory");
                query.whereEqualTo("companyName", companyName);
                query.setLimit(1000);
                try {
                    mDeviceHistory = query.find();
                    if (mDeviceHistory.size() != 0) {
                        mArrDeviceHistory = new ArrayList<ParseObject>();
                        for (int i = 0; i < mDeviceHistory.size(); i++) {
                            ParseObject row = mDeviceHistory.get(i);
                            mArrDeviceHistory.add(row);
                        }

                    }
                } catch (ParseException e) {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            AdminDailyActivity.this.mProgressDialog = ProgressDialog.show(
                    AdminDailyActivity.this, "", "Loading...", true);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mArrDeviceHistory.size() == 0) {

            } else {
                Log.d("com.parse.demo", "count : " + mArrDeviceHistory.size());
                Log.d("com.parse.demo", "count mDeviceHistory : " + mDeviceHistory.size());
                AdminDailyActivity.this.mProgressDialog.dismiss();
                for (int i = 0; i < mArrDeviceHistory.size(); i++) {
                    ParseObject row = mArrDeviceHistory.get(i);
                    if (mArrModel != null) {
                        if (!mArrModel.contains(row.getString("deviceName"))) {
                            mArrModel.add(row.getString("deviceName"));
                        }
                    }

                }
                isSearchClickable = true;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        AdminDailyActivity.this,
                        android.R.layout.simple_spinner_item, mArrModel);
                mSpinName.setAdapter(adapter);
                mSpinName.setOnItemSelectedListener(new dailyOnClickListener());
                // setOnClickListenersforSpinners();
            }

        }
    }

    /****** Function to set data in ArrayList *************/
    public void setListData(ArrayList<ParseObject> lArrDevices) {
        Resources res = getResources();
        customListViewValuesArr.clear();

        for (int i = 0; i < lArrDevices.size(); i++) {

            final DataModelDeviceHistory sched = new DataModelDeviceHistory();
            ParseObject row = lArrDevices.get(i);
            /******* Firstly take data in model object ******/
            sched.setDeviceUser(row.getString("deviceUser"));
            sched.setDeviceMake(row.getString("deviceMake"));
            sched.setDeviceModel(row.getString("deviceModel"));
            sched.setDeviceName(row.getString("deviceName"));
            Log.d("com.parse.demo", row.getString("deviceName"));
            sched.setDeviceRegNumber(row.getString("registrationNumber"));

            sched.setDeviceToken(row.getString("deviceToken"));
            sched.setDeviceAction(row.getString("action"));
            sched.setDeviceCheckinDate(row.getDate("checkinDate"));
            sched.setDeviceCheckoutDate(row.getDate("checkoutDate"));
            sched.setDeviceToken(row.getString("deviceToken"));

            /******** Take Model Object in ArrayList **********/
            customListViewValuesArr.add(sched);

        }
        Log.d("com.parse.demo", customListViewValuesArr.toString());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindaily_layout);
        CustomListView = this;
        mHeaderView = (LinearLayout)findViewById(R.id.layoutHeader);
        new RemoteDataTask().execute();
        res = getResources();
        list = (ListView) findViewById(R.id.dailylist); // List defined in XML
        // ( See
        // Below )
        mFilteredArrDevices = new ArrayList<ParseObject>();
        mSpinName = (CustomSpinner) findViewById(R.id.dailySpinner);

        mArrModel = new ArrayList<String>();
        mSelectedStatus = "Free";



    }

    public void searchDevices(String selectedDevice)
    {
        mHeaderView.setVisibility(View.VISIBLE);
        mFilteredArrDevices.clear();
        for (int i = 0; i < mArrDeviceHistory.size(); i++) {
            ParseObject row = mArrDeviceHistory.get(i);
            if (row.getString("deviceName").equalsIgnoreCase(
                    selectedDevice)) {
                Log.d("com.parse.demo", selectedDevice+" equal "+(row.getString("deviceName")));
                mFilteredArrDevices.add(row);

            }
            else
            {
                Log.d("com.parse.demo", selectedDevice+" "+(row.getString("deviceName")));
            }

        }
        ArrayList<DataModelDeviceHistory> lCustomListViewValuesArr = new ArrayList<DataModelDeviceHistory>();
        setListData(mFilteredArrDevices);
        DataModelDeviceHistory dataModelHist = customListViewValuesArr
                .get(customListViewValuesArr.size() - 1);
        if (dataModelHist.getDeviceAction().equalsIgnoreCase(
                "checkout")) {
            DataModelDeviceHistory lHistory1 = new DataModelDeviceHistory();
            lHistory1.setDeviceUser(dataModelHist.getDeviceUser());
            lHistory1.setDeviceCheckoutDate(dataModelHist
                    .getDeviceCheckoutDate());
            lHistory1.setDeviceCheckinDate(null);
            lHistory1.setDeviceName(dataModelHist.getDeviceName());
            lCustomListViewValuesArr.add(lHistory1);

        }
        for (int i = customListViewValuesArr.size() - 1; i > 0; i--) {
            DataModelDeviceHistory lHistory = new DataModelDeviceHistory();
            DataModelDeviceHistory lHistoryNext = new DataModelDeviceHistory();
            lHistory = customListViewValuesArr.get(i - 1);
            lHistoryNext = customListViewValuesArr.get(i);

            if (lHistory.getDeviceAction().equalsIgnoreCase(
                    "checkout")
                    & lHistoryNext.getDeviceAction()
                    .equalsIgnoreCase("checkin")
                    & lHistory.getDeviceUser().equals(
                    lHistoryNext.getDeviceUser())) {
                DataModelDeviceHistory lHistory1 = new DataModelDeviceHistory();
                lHistory1.setDeviceUser(lHistory.getDeviceUser());
                lHistory1.setDeviceCheckoutDate(lHistory
                        .getDeviceCheckoutDate());
                lHistory1.setDeviceCheckinDate(lHistoryNext
                        .getDeviceCheckinDate());
                lHistory1.setDeviceName(lHistory.getDeviceName());
                lCustomListViewValuesArr.add(lHistory1);
            }
            else if(lHistory.getDeviceAction().equalsIgnoreCase("register"))
            {
                Log.d("com.parse.demo", lHistory.getDeviceRegNumber());
                DataModelDeviceHistory lHistoryNew = new DataModelDeviceHistory();
                lHistoryNew.setDeviceUser("Registered By "+lHistory.getDeviceUser());
                lHistoryNew.setDeviceCheckoutDate(null);
                lHistoryNew.setDeviceCheckinDate(null);
                lHistoryNew.setDeviceName(lHistory.getDeviceName());
                lCustomListViewValuesArr.add(lHistoryNew);
            }

        }
//					DataModelDeviceHistory ldataModelHist = customListViewValuesArr
//							.get(0);
//					if (ldataModelHist.getDeviceAction().equalsIgnoreCase(
//							"register")) {
//						DataModelDeviceHistory lHistory1 = new DataModelDeviceHistory();
//						lHistory1.setDeviceUser("Registered by "+ldataModelHist.getDeviceUser());
//						lCustomListViewValuesArr.add(lHistory1);
//
//
//					}
        mAdapter = new AdminDailyActivityAdapter(CustomListView,
                lCustomListViewValuesArr, res);
        list.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /***************** This function used by adapter ****************/
    public void onItemClick(int mPosition) {
        DataModelDeviceHistory tempValues = (DataModelDeviceHistory) customListViewValuesArr
                .get(mPosition);
        Toast.makeText(
                CustomListView,
                "" + tempValues.getDeviceUser() + " Image:"
                        + tempValues.getDeviceMake() + " Url:"
                        + tempValues.getDeviceCheckoutDate(), Toast.LENGTH_LONG)
                .show();
    }

    public class dailyOnClickListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos,
                                   long id) {

            switch (parent.getId()) {
                case R.id.dailySpinner: {

                    int position = mSpinName.getSelectedItemPosition();
                    Toast.makeText(getApplicationContext(),
                            "You have selected " + mArrModel.get(position),
                            Toast.LENGTH_LONG).show();
                    mSelectedDevice = mArrModel.get(position);
                    searchDevices(mSelectedDevice);
                }

                break;

                default:
                    break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

        public void showAlertDialog(String msg) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    AdminDailyActivity.this);
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

}
