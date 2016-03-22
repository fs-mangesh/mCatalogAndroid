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
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class SearchDevicesActivity extends Activity {
    private List<ParseObject> mDevices;
    private Dialog mProgressDialog;
    ParseObject m_jArryObj;
    JSONArray m_jArry;
    ArrayList<ParseObject> mArrDevices;
    public ArrayList<DataModelDevice> customListViewValuesArr = new ArrayList<DataModelDevice>();
    public ArrayList<DataModelDevice> customListViewValuesArrCopy = new ArrayList<DataModelDevice>();
    ListView list;
    SearchListActivityAdapter mAdapter;
    public SearchDevicesActivity CustomListView = null;
    Resources res;
    CustomSpinner mSpinMake;
    CustomSpinner mSpinModel;
    CustomSpinner mSpinStatus;
    private String defaultTextMake= "Make";
    private String defaultTextModel= "Model";
    private String defaultTextStatus= "Status";
    String[] mArrMake = { "Select Make","Apple", "Samsung", "Nexus", "Lenovo", "Sony" };
    String mSelectedMake;
    String mSelectedModel;
    String mSelectedStatus;
    ArrayList<String> mArrModel;
    String[] mArrStatus = {"Select Status", "Free", "Busy" };
    ArrayList<ParseObject> mFilteredArrDevices;
    boolean isInitialCallMake;
    boolean isInitialCallModel;
    boolean isInitialCallStatus;

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {
            // Gets the current list of todos in sorted order
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "Devices");
            try {
                mDevices = query.find();
                if (mDevices.size() != 0) {
                    mArrDevices = new ArrayList<ParseObject>();
                    for (int i = 0; i < mDevices.size(); i++) {
                        ParseObject row = mDevices.get(i);
                        mArrDevices.add(row);
                    }

                }
            } catch (ParseException e) {
                Log.d("score", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            SearchDevicesActivity.this.mProgressDialog = ProgressDialog.show(
                    SearchDevicesActivity.this, "", "Loading...", true);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mArrDevices.size() == 0) {

                showAlertDialog("No Devices Found!!");
            } else {
                Log.d("com.parse.demo", "count : " + mArrDevices.size());
                setListData(mArrDevices);
                /**************** Create Custom Adapter *********/
                mAdapter = new SearchListActivityAdapter(CustomListView,
                        customListViewValuesArr, res);
                list.setAdapter(mAdapter);
                SearchDevicesActivity.this.mProgressDialog.dismiss();
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);

                    mArrModel.add(row.getString("deviceModel"));

                }

                setListenerForMakeArr();
                Log.d("com.parse.demo","after setListenerForMakeArr selected Make"+mSelectedMake);
                //
                //setListenerForModelArr();
                //setListenerForStatusArr();
                // setOnClickListenersforSpinners();
            }

        }
    }

    /****** Function to set data in ArrayList *************/
    public void setListData(ArrayList<ParseObject> lArrDevices) {
        Resources res = getResources();
        customListViewValuesArr.clear();

        for (int i = 0; i < lArrDevices.size(); i++) {

            final DataModelDevice sched = new DataModelDevice();
            ParseObject row = lArrDevices.get(i);
            /******* Firstly take data in model object ******/
            sched.setDeviceCurrentUser(row.getString("deviceCurrentUser"));
            sched.setDeviceMake(row.getString("deviceMake"));
            sched.setDeviceModel(row.getString("deviceModel"));
            sched.setDeviceName(row.getString("deviceName"));
            sched.setDeviceRegistrationNumber(row
                    .getString("registrationNumber"));
            sched.setDeviceStatus(row.getString("deviceStatus"));
            sched.setDeviceVersion(row.getString("deviceVersion"));
            sched.setDevicveToken(row.getString("deviceToken"));

            /******** Take Model Object in ArrayList **********/
            customListViewValuesArr.add(sched);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdevice_layout);
        CustomListView = this;
        mArrModel = new ArrayList<String>();
        mSpinMake = (CustomSpinner) findViewById(R.id.spinner1);
        mSpinModel = (CustomSpinner) findViewById(R.id.spinner2);
        mSpinStatus = (CustomSpinner) findViewById(R.id.spinner3);
        mSelectedStatus = "status";
        mSelectedModel = "model";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mArrStatus);
        mSpinStatus.setAdapter(adapter);

        mFilteredArrDevices = new ArrayList<ParseObject>();
        new RemoteDataTask().execute();
        res = getResources();
        list = (ListView) findViewById(R.id.searchlist); // List defined in XML
        // ( See
        // Below )


    }

    public void filterListData(String option)
    {
        Log.d("com.parse.demo","mSelectedStatus "+mSelectedStatus+" mSelectedModel "+mSelectedModel);
        mFilteredArrDevices.clear();
        if(option.equalsIgnoreCase("make")) {

            if(mSelectedModel.equalsIgnoreCase("model") && !mSelectedStatus.equalsIgnoreCase("status"))
            {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);

                    if (row.getString("deviceMake").equalsIgnoreCase(
                            mSelectedMake) && row.getString("deviceStatus")
                            .equalsIgnoreCase(mSelectedStatus) ) {
                        mFilteredArrDevices.add(row);
                    }


                }
            }
            else if (!mSelectedModel.equalsIgnoreCase("model") && mSelectedStatus.equalsIgnoreCase("status"))
            {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);

                    if (row.getString("deviceMake").equalsIgnoreCase(
                            mSelectedMake) && row.getString("deviceModel")
                            .equalsIgnoreCase(mSelectedModel) ) {
                        mFilteredArrDevices.add(row);
                    }


                }

            }
            else if(mSelectedModel.equalsIgnoreCase("model") && mSelectedStatus.equalsIgnoreCase("status"))
            {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);

                    if (row.getString("deviceMake").equalsIgnoreCase(
                            mSelectedMake)) {
                        mFilteredArrDevices.add(row);
                    }


                }
            }




        }

        else if(option.equalsIgnoreCase("model")) {
            if(!mSelectedMake.equalsIgnoreCase("make") &&  !mSelectedStatus.equalsIgnoreCase("status"))
            {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);
                    if (row.getString("deviceModel")
                            .equalsIgnoreCase(mSelectedModel) && row.getString("deviceStatus")
                            .equalsIgnoreCase(mSelectedStatus)) {
                        mFilteredArrDevices.add(row);
                    }

                }
            }
            else if(!mSelectedMake.equalsIgnoreCase("make") &&  mSelectedStatus.equalsIgnoreCase("status"))
            {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);
                    if (row.getString("deviceModel")
                            .equalsIgnoreCase(mSelectedModel) ) {
                        mFilteredArrDevices.add(row);
                    }

                }
            }

            else{
                showAlertDialog("Please select a Make");
            }




        }
        else if(option.equalsIgnoreCase("status"))
        {
            if(mSelectedMake.equalsIgnoreCase("make") && mSelectedModel.equalsIgnoreCase("Model") ) {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);
                    if (row.getString("deviceStatus")
                            .equalsIgnoreCase(mSelectedStatus)) {
                        mFilteredArrDevices.add(row);
                    }
                }
            }
            else if(!mSelectedMake.equalsIgnoreCase("make") && mSelectedModel.equalsIgnoreCase("Model") )
            {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);
                    if (row.getString("deviceMake").equalsIgnoreCase(
                            mSelectedMake) && row.getString("deviceStatus")
                            .equalsIgnoreCase(mSelectedStatus)) {
                        mFilteredArrDevices.add(row);
                    }
                }


            }
            else if(mSelectedMake.equalsIgnoreCase("make") && !mSelectedModel.equalsIgnoreCase("Model") )
            {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);
                    if (row.getString("deviceModel")
                            .equalsIgnoreCase(mSelectedModel) && row.getString("deviceStatus")
                            .equalsIgnoreCase(mSelectedStatus)) {
                        mFilteredArrDevices.add(row);
                    }
                }

            }

            else if(!mSelectedMake.equalsIgnoreCase("make") && !mSelectedModel.equalsIgnoreCase("Model") )
            {
                for (int i = 0; i < mArrDevices.size(); i++) {
                    ParseObject row = mArrDevices.get(i);
                    if (row.getString("deviceMake").equalsIgnoreCase(
                            mSelectedMake) && row.getString("deviceModel")
                            .equalsIgnoreCase(mSelectedModel) && row.getString("deviceStatus")
                            .equalsIgnoreCase(mSelectedStatus)) {
                        mFilteredArrDevices.add(row);
                    }
                }

            }

        }
        if(!mFilteredArrDevices.isEmpty())
        {
            setListData(mFilteredArrDevices);
            mAdapter.notifyDataSetChanged();
        }
        else
        {
            setListData(mFilteredArrDevices);
            mAdapter.notifyDataSetChanged();
            showAlertDialog("No devices found!!");
        }
    }

    public void setListenerForMakeArr() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mArrMake);
        mSpinMake.setAdapter(adapter);

        mArrModel.clear();
        mArrModel.add("Select Model");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mArrModel);
        mSpinModel.setAdapter(adapter1);
//        mSpinMake.setAdapter(new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, mArrMake, defaultTextMake));
        addListenerOnMakeSelection();


    }

    public void addListenerOnMakeSelection() {
        mSelectedMake = "make";
        mSpinMake.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                if(pos==0)
                {
                    mSelectedMake = "make";
                    return;
                }
                else {
                    mSelectedMake = mArrMake[pos];
                    Log.d("com.parse.demo","selected Make"+mSelectedMake);
                    addListenerOnModelSelection();
                    filterListData("make");


                }



            }

            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }


    public void addListenerOnModelSelection() {

        mSelectedModel = "model";
        mArrModel.clear();
        mArrModel.add("Select Model");
        if(!mSelectedMake.equalsIgnoreCase("Make"))
        {
            for (int i = 0; i < mArrDevices.size(); i++) {
                ParseObject row = mArrDevices.get(i);
                if (row.getString("deviceMake").equalsIgnoreCase(
                        mSelectedMake)) {
                    mArrModel.add(row.getString("deviceModel"));
                }
            }
        }
//        else if(mSelectedMake.equalsIgnoreCase("make"))
//        {
//            mArrModel.add("Model");
//        }
//        if(mArrModel.size() == 0)
//        {
//            mArrModel.add("Model");
//        }
        Log.d("com.parse.demo","mArrModel size "+mArrModel.size());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mArrModel);
        mSpinModel.setAdapter(adapter);

        mSpinModel.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(pos ==0)
                {
                    mSelectedModel = "model";
                    return;
                }
                else
                {
                    mSelectedModel = mArrModel.get(pos);
                    Log.d("com.parse.demo","selected Model"+mSelectedModel);
                    filterListData("model");
                    addListenerOnStatusSelection();



                }


                //setListenerForStatusArr();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }


    public void addListenerOnStatusSelection() {



        mSpinStatus.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(pos ==0)
                {
                    mSelectedStatus = "status";
                    return;
                }
                else {
                    mSelectedStatus = mArrStatus[pos];
                    Log.d("com.parse.demo","selected Status"+mSelectedStatus);
                    filterListData("status");
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

    }



    /***************** This function used by adapter ****************/

    public void onItemClick(int mPosition) {
        DataModelDevice tempValues = (DataModelDevice) customListViewValuesArr
                .get(mPosition);
        Toast.makeText(
                CustomListView,
                "" + tempValues.getDeviceCurrentUser() + " Image:"
                        + tempValues.getDeviceMake() + " Url:"
                        + tempValues.getDeviceModel(), Toast.LENGTH_LONG)
                .show();

        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo("deviceToken", tempValues.getDevicveToken());

        ParsePush.sendMessageInBackground(tempValues.getDeviceCurrentUser()
                + " Has requested for this device!", query);
    }


    public void showAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SearchDevicesActivity.this);
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
