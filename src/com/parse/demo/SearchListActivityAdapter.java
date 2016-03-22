package com.parse.demo;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchListActivityAdapter extends BaseAdapter implements OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    DataModelDevice tempValues=null;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public SearchListActivityAdapter(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView txtModel;
        public TextView txtRegistrationNo;
        public TextView txtStatus;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.listitem_search_layout, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtModel = (TextView) vi.findViewById(R.id.txtModel);
            holder.txtRegistrationNo=(TextView)vi.findViewById(R.id.txtRegsiterNo);
            holder.txtStatus=(TextView)vi.findViewById(R.id.txtStatus);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.txtModel.setText("No Data");
            holder.txtRegistrationNo.setText("");
            holder.txtStatus.setText("");
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( DataModelDevice ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.txtModel.setText( tempValues.getDeviceModel() );
            holder.txtRegistrationNo.setText( tempValues.getDeviceRegistrationNumber() );

            if(tempValues.getDeviceStatus().equalsIgnoreCase("Free"))
            {
                holder.txtStatus.setText(tempValues.getDeviceStatus());
            }
            else
            {
                holder.txtStatus.setText("Current User : "+tempValues.getDeviceCurrentUser());
            }

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            SearchDevicesActivity sct = (SearchDevicesActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }

}
