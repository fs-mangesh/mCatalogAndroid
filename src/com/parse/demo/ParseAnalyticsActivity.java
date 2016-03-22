package com.parse.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ParseAnalyticsActivity extends Activity {

    ArrayList<String> mDeviceList;
    private  ListView list;
    String[] planets = new String[] { "Apple", "Banana", "Mango", "Grapes"
    };
    private String mSortOption="";

    private TextView txtInventoryUtilise;
    private TextView txtMostCheckedDevice1;
    private TextView txtMostCheckedDevice2;
    private TextView txtMostCheckedDevice3;
    private TextView txtLeastCheckedDevice1;
    private TextView txtLeastCheckedDevice2;
    private TextView txtLeastCheckedDevice3;
    private TextView txtMostRequestedDevice1;
    private TextView txtMostRequestedDevice2;
    private TextView txtMostRequestedDevice3;

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ParseAnalyticsActivity.this,
//                    android.R.layout.simple_list_item_1, android.R.id.text1, mDeviceList);
//
//        }
//    };

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.getData()!= null)
            {
                Bundle bundle = msg.getData();
                if(bundle != null)
                {
                    String jsonStr = bundle.getString(ParseAnalyticsService.RESULT);
                    try {
                        JSONArray array = new JSONArray(jsonStr);
                        mSortOption = "checkout";
                        JSONArray lSortedArrayByCheckoutNumber =getSortedListByRequestNo(array);
                        mSortOption="request";
                        JSONArray lSortedArrayByRequestNumber =getSortedListByRequestNo(array);
                        Log.d("com.parse.demo","Activity Result"+lSortedArrayByCheckoutNumber);
                        Log.d("com.parse.demo","Array Sorted based on checkout"+lSortedArrayByCheckoutNumber);
                        Log.d("com.parse.demo","Array Sorted based on request"+lSortedArrayByRequestNumber);
                        if(lSortedArrayByCheckoutNumber != null)
                        {
                            if(lSortedArrayByCheckoutNumber.length()<=2)
                            {
//                                switch (lSortedArrayByCheckoutNumber.length())
//                                {
//                                    case 1:
//
//
//                                }


                            }
                            else
                            {

                            }
                        }
                        else
                        {

                        }
//                        if(lSortedArrayByCheckoutNumber)
//                        for(int i =0; i<lSortedArrayByCheckoutNumber.length();i++)
//                        {
//                            JSONObject row = lSortedArrayByCheckoutNumber.getJSONObject(i);
//                            JSONObject lArr = new JSONObject();
//                            lArr.put("Name",row.getString("deviceName"));
//                            lArr.put("CheckoutNumber",row.getString("checkoutNumber"));
//                            mDeviceList.add(lArr.toString());
//
//                        }
//                        Log.d("com.parse.demo","mDeviceList Result"+mDeviceList);
//
//                        if(mDeviceList != null)
//                        {
//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ParseAnalyticsActivity.this,
//                    android.R.layout.simple_list_item_1, android.R.id.text1, mDeviceList);
//                            list.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();
//
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


            Toast.makeText(ParseAnalyticsActivity.this, "Download complete!",
                    Toast.LENGTH_LONG).show();
        }
    };



    private  JSONArray getSortedListByRequestNo(JSONArray array) throws JSONException {
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i));
        }

        Collections.sort(list, new SortBasedOnOption());

        JSONArray resultArray = new JSONArray(list);

        return resultArray;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_analytics);

        txtInventoryUtilise = (TextView)findViewById(R.id.analytTxtUtilisation);



//        mDeviceList = new ArrayList<String>();
//        ArrayAdapter adapter = new ArrayAdapter(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, planets);
//        list = (ListView)findViewById(R.id.listViewAnalytic);
//        list.setAdapter(adapter);

        Intent i=new Intent(this, ParseAnalyticsService.class);

        i.putExtra(ParseAnalyticsService.EXTRA_MESSENGER, new Messenger(handler));

        startService(i);




    }

    private class SortBasedOnOption implements Comparator<JSONObject> {
        @Override
        public int compare(JSONObject lhs, JSONObject rhs) {
            try {
                if(mSortOption.equals("request"))
                {
                    return lhs.getInt("requestNumber") > rhs.getInt("requestNumber") ? 1 : (lhs
                            .getInt("requestNumber") < rhs.getInt("requestNumber") ? -1 : 0);
                }
                else if(mSortOption.equals("checkout"))
                {
                    return lhs.getInt("checkoutNumber") > rhs.getInt("checkoutNumber") ? 1 : (lhs
                            .getInt("checkoutNumber") < rhs.getInt("checkoutNumber") ? -1 : 0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;

        }
    }





//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(receiver, new IntentFilter(ParseAnalyticsService.NOTIFICATION));
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(receiver);
//    }
}
