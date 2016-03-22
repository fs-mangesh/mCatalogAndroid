package com.parse.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdminDateHistoryAdapter extends BaseAdapter implements OnClickListener {

	/*********** Declare Used Variables *********/
	private Activity activity;
	private ArrayList data;
	private static LayoutInflater inflater = null;
	public Resources res;
	DataModelDeviceHistory tempValues = null;
	int i = 0;

	/************* CustomAdapter Constructor *****************/
	public AdminDateHistoryAdapter(Activity a, ArrayList d, Resources resLocal) {

		/********** Take passed values **********/
		activity = a;
		data = d;
		res = resLocal;

		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/******** What is the size of Passed Arraylist Size ************/
	public int getCount() {

		if (data.size() <= 0)
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
	public static class ViewHolder {

		public TextView txtUser;
		public TextView txtCheckout;
		public TextView txtCheckin;
		public TextView txtCheckoutTime;
		public TextView txtCheckinTime;
		public TextView txtDevice;
	}

	/****** Depends upon data size called for each row , Create each ListView row *****/
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		ViewHolder holder;
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
		if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.listitem_admindaily, parent, false);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.txtUser = (TextView) vi.findViewById(R.id.txtUser);
			holder.txtCheckout = (TextView) vi
					.findViewById(R.id.txtCheckoutDate);
			holder.txtCheckoutTime = (TextView) vi.findViewById(R.id.txtCheckoutTime);
			holder.txtCheckin = (TextView) vi.findViewById(R.id.txtCheckinDate);
			holder.txtCheckinTime = (TextView) vi.findViewById(R.id.txtCheckinTime);
			holder.txtDevice = (TextView) vi.findViewById(R.id.txtDevice);
			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if (data.size() <= 0) {
			holder.txtUser.setText("No Data");

		} else {
			/***** Get each Model object from Arraylist ********/
			tempValues = null;
			tempValues = (DataModelDeviceHistory) data.get(position);

			/************ Set Model values in Holder elements ***********/

			holder.txtUser.setText(tempValues.getDeviceUser());
			holder.txtDevice.setText(tempValues.getDeviceName());
			if(tempValues
					.getDeviceCheckoutDate() != null)
			{
				Date date = tempValues
						.getDeviceCheckoutDate();
				String time1 = sdfDate.format(date);
				holder.txtCheckout.setText(time1);
				String time2 = sdfTime.format(date);
				holder.txtCheckoutTime.setText(time2);
			}
			else
			{
				holder.txtCheckout.setText("--");
                holder.txtCheckoutTime.setText("--");
			}

			if(tempValues
					.getDeviceCheckinDate() != null)
			{
				Date date = tempValues
						.getDeviceCheckinDate();
				String time1 = sdfDate.format(date);
				holder.txtCheckin.setText(time1);
				String time2 = sdfTime.format(date);
				holder.txtCheckinTime.setText(time2);
			}
			else
			{
				holder.txtCheckin.setText("--");
                holder.txtCheckinTime.setText("--");
			}

			

			/******** Set Item Click Listner for LayoutInflater for each row *******/

			vi.setOnClickListener(new OnItemClickListener(position));
		}
		return vi;
	}

	@Override
	public void onClick(View v) {
		Log.v("CustomAdapter", "=====Row button clicked=====");
	}

	/********* Called when Item click in ListView ************/
	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {

			AdminDateHistoryActivity sct = (AdminDateHistoryActivity) activity;

			/****
			 * Call onItemClick Method inside CustomListViewAndroidExample Class
			 * ( See Below )
			 ****/

			 sct.onItemClick(mPosition);
		}
	}

}
