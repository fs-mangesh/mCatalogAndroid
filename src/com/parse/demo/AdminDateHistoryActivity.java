package com.parse.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminDateHistoryActivity extends Activity {

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    EditText edittext;
    private List<ParseObject> mDeviceHistory;
    private Dialog mProgressDialog;
    ParseObject m_jArryObj;
    JSONArray m_jArry;
    ArrayList<ParseObject> mArrDeviceHistory;
    public ArrayList<DataModelDeviceHistory> customListViewValuesArr = new ArrayList<DataModelDeviceHistory>();
    ListView list;
    AdminDateHistoryAdapter mAdapter;
    public AdminDateHistoryActivity CustomListView = null;
    Resources res;
    CustomSpinner mSpinName;
    String mSelectedStatus;
    String mSelectedDevice;
    ArrayList<String> mArrModel;
    ArrayList<ParseObject> mFilteredArrDevices;
    boolean isSearchClickable;
    Button btnSearch;
    String selectedDate;
    LinearLayout mHeaderView;

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {
            ToDoListApplication application = (ToDoListApplication)getApplicationContext();
            String companyName = application.getCompanyName();

            if(companyName != "" || companyName != null)
            {
                // Gets the current list of todos in sorted order
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "DeviceHistory");
                query.whereEqualTo("deviceDate", selectedDate);
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
            AdminDateHistoryActivity.this.mProgressDialog = ProgressDialog
                    .show(AdminDateHistoryActivity.this, "", "Loading...", true);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mArrDeviceHistory == null) {
                AdminDateHistoryActivity.this.mProgressDialog.dismiss();
                showAlertDialog("No records for today");
            } else {
                Log.d("com.parse.demo", "count : " + mArrDeviceHistory.size());
                Log.d("com.parse.demo", "count mDeviceHistory : "
                        + mDeviceHistory.size());
                setListData(mArrDeviceHistory);
                // if (mFilteredArrDevices != null) {
                // mFilteredArrDevices.clear();
                // }

                // for (int i = 0; i < mArrDeviceHistory.size(); i++) {
                // ParseObject row = mArrDeviceHistory.get(i);
                // if (row.getString("deviceName").equalsIgnoreCase(
                // mSelectedDevice)) {
                // Log.d("com.parse.demo", mSelectedDevice + " equal "
                // + (row.getString("deviceName")));
                // mFilteredArrDevices.add(row);
                //
                // } else {
                // Log.d("com.parse.demo",
                // mSelectedDevice + " "
                // + (row.getString("deviceName")));
                // }
                //
                // }
                ArrayList<DataModelDeviceHistory> lCustomListViewValuesArr = new ArrayList<DataModelDeviceHistory>();
                // setListData(mFilteredArrDevices);
                if (customListViewValuesArr.size() != 0) {
                    if (customListViewValuesArr.size() == 1) {
                        DataModelDeviceHistory dataModelHist = customListViewValuesArr
                                .get(0);
                        if (dataModelHist.getDeviceAction().equalsIgnoreCase(
                                "register")) {
                            DataModelDeviceHistory lHistoryNew = new DataModelDeviceHistory();
                            lHistoryNew.setDeviceUser("Registered By "
                                    + dataModelHist.getDeviceUser());
                            lHistoryNew.setDeviceCheckoutDate(null);
                            lHistoryNew.setDeviceCheckinDate(null);
                            lHistoryNew.setDeviceName(dataModelHist
                                    .getDeviceName());
                            lCustomListViewValuesArr.add(lHistoryNew);

                        }
                    } else {
                        DataModelDeviceHistory dataModelHist = customListViewValuesArr
                                .get(customListViewValuesArr.size() - 1);
                        if (dataModelHist.getDeviceAction().equalsIgnoreCase(
                                "checkout")) {
                            DataModelDeviceHistory lHistory1 = new DataModelDeviceHistory();
                            lHistory1.setDeviceUser(dataModelHist
                                    .getDeviceUser());
                            lHistory1.setDeviceCheckoutDate(dataModelHist
                                    .getDeviceCheckoutDate());
                            lHistory1.setDeviceCheckinDate(null);
                            lHistory1.setDeviceName(dataModelHist
                                    .getDeviceName());
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
                                lHistory1.setDeviceUser(lHistory
                                        .getDeviceUser());
                                lHistory1.setDeviceCheckoutDate(lHistory
                                        .getDeviceCheckoutDate());
                                lHistory1.setDeviceCheckinDate(lHistoryNext
                                        .getDeviceCheckinDate());
                                lHistory1.setDeviceName(lHistory
                                        .getDeviceName());
                                lCustomListViewValuesArr.add(lHistory1);
                            } else if (lHistory.getDeviceAction()
                                    .equalsIgnoreCase("register")) {
                                Log.d("com.parse.demo",
                                        lHistory.getDeviceRegNumber());
                                DataModelDeviceHistory lHistoryNew = new DataModelDeviceHistory();
                                lHistoryNew.setDeviceUser("Registered By "
                                        + lHistory.getDeviceUser());
                                lHistoryNew.setDeviceCheckoutDate(null);
                                lHistoryNew.setDeviceCheckinDate(null);
                                lHistoryNew.setDeviceName(lHistory
                                        .getDeviceName());
                                lCustomListViewValuesArr.add(lHistoryNew);
                            }

                        }
                    }
                    mAdapter = new AdminDateHistoryAdapter(CustomListView,
                            lCustomListViewValuesArr, res);
                    list.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                }

                // DataModelDeviceHistory ldataModelHist =
                // customListViewValuesArr
                // .get(0);
                // if (ldataModelHist.getDeviceAction().equalsIgnoreCase(
                // "register")) {
                // DataModelDeviceHistory lHistory1 = new
                // DataModelDeviceHistory();
                // lHistory1.setDeviceUser("Registered by "+ldataModelHist.getDeviceUser());
                // lCustomListViewValuesArr.add(lHistory1);
                //
                //
                // }

                AdminDateHistoryActivity.this.mProgressDialog.dismiss();

                // AdminDateHistoryActivity.this.mProgressDialog.dismiss();
                // for (int i = 0; i < mArrDeviceHistory.size(); i++) {
                // ParseObject row = mArrDeviceHistory.get(i);
                // if (mArrModel != null) {
                // if (!mArrModel.contains(row.getString("deviceName"))) {
                // mArrModel.add(row.getString("deviceName"));
                // }
                // }
                //
                // }
                isSearchClickable = true;

                // setOnClickListenersforSpinners();
            }

        }
    }

    /****** Function to set data in ArrayList *************/
    public void setListData(ArrayList<ParseObject> lArrDevices) {
        mHeaderView.setVisibility(View.VISIBLE);
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
        Log.d("com.parse.demo 123", customListViewValuesArr.toString());

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_datehistory);
        mHeaderView = (LinearLayout)findViewById(R.id.layoutHeader);
        list = (ListView) findViewById(R.id.datelist);
        CustomListView = this;
        calendar = Calendar.getInstance();
        mFilteredArrDevices = new ArrayList<ParseObject>();
        edittext = (EditText) findViewById(R.id.textDate);
        edittext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AdminDateHistoryActivity.this, date,
                        calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    private void updateLabel() {

        String myFormat = "dd.MM.yy"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edittext.setText(sdf.format(calendar.getTime()));
        selectedDate = (sdf.format(calendar.getTime()));
        Log.d("com.parse.demo", selectedDate);
        if (selectedDate != "") {
            new RemoteDataTask().execute();

        }

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

    public void showAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AdminDateHistoryActivity.this);
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
