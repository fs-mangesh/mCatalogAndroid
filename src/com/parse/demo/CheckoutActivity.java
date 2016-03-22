package com.parse.demo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends Activity {
	String optionText;
	EditText inputText;
    EditText inputUsername;
	Button btnCheck;
	String mAction;
	String mUsername;
	String checkoutDuration;
	String retainDuration;
	String extendDuration;
	String mREgNumber;
    String mDeviceMake;
	Date selectedDate;
	private Calendar calendar;
	private PendingIntent pendingIntent;
    ImageView imageView;
    TextView screenName;
    ImageView usernameLine;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkout);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mAction = extras.getString("action");
		}

	//	mUsername = extras.getString("username");


		inputText = (EditText) findViewById(R.id.inputText);
        inputUsername = (EditText)findViewById(R.id.inputUsername);
        imageView = (ImageView)findViewById(R.id.imageCheckout);
        screenName = (TextView)findViewById(R.id.screenName);
        usernameLine = (ImageView)findViewById(R.id.imgLineUsername);

		if (mAction.equalsIgnoreCase("retain")) {

			calendar = Calendar.getInstance();
			inputText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new DatePickerDialog(CheckoutActivity.this, date, calendar
							.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			});
		}
		btnCheck = (Button) findViewById(R.id.checkBtn);
		btnCheck.setText(extras.getString("action"));

        if (mAction.equalsIgnoreCase("register")) {
            screenName.setText("Register");
            inputText.setHint("Enter registration identifier");
            inputText.setInputType(InputType.TYPE_CLASS_TEXT);
            imageView.setImageResource(R.drawable.register);
            inputUsername.setHint("Enter device make");
        }

        else if (mAction.equalsIgnoreCase("checkout")) {
            inputUsername.setVisibility(View.VISIBLE);
            usernameLine.setVisibility(View.VISIBLE);
        }

        else if (mAction.equalsIgnoreCase("retain")) {
            screenName.setText("Retain");
            imageView.setImageResource(R.drawable.retain);
            inputUsername.setVisibility(View.INVISIBLE);
            usernameLine.setVisibility(View.INVISIBLE);
            inputText.setHint("Enter date");
        }

        else if (mAction.equalsIgnoreCase("extend")) {
            screenName.setText("Extend");
            imageView.setImageResource(R.drawable.extend);
            inputUsername.setVisibility(View.INVISIBLE);
            usernameLine.setVisibility(View.INVISIBLE);
        }

		btnCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                mUsername = inputUsername.getText().toString();

				if (mAction.equalsIgnoreCase("register")) {
                    mDeviceMake = inputUsername.getText().toString();
					optionText = inputText.getText().toString();

                    if(optionText.matches("")){
                        Toast.makeText(getApplicationContext(),"Please enter registration identifier",Toast.LENGTH_SHORT).show();
                    }
                    else {
					registerDevice();
                    }
				}

                else if (mAction.equalsIgnoreCase("checkout")) {
					checkoutDuration = inputText.getText().toString();
                    if(checkoutDuration.matches("")||mUsername.matches("")){
                        Toast.makeText(getApplicationContext(),"Please enter all fields",Toast.LENGTH_SHORT).show();
                    }else {
                        checkoutCurrentDevice();
                    }
				}

                else if (mAction.equalsIgnoreCase("retain")) {
					retainDuration = inputText.getText().toString();
                    if(retainDuration.matches("")){
                        Toast.makeText(getApplicationContext(),"Please enter duration",Toast.LENGTH_SHORT).show();
                    }else {
                        retainCurrentDevice();
                    }
				}

                else if (mAction.equalsIgnoreCase("extend")) {
					extendDuration = inputText.getText().toString();
                    if (extendDuration.matches("")){
                        Toast.makeText(getApplicationContext(),"Please enter duration",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        extendCurrentDevice();
                    }
				}

			}
		});

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

	private void updateLabel() {

		String myFormat = "dd.MM.yy"; // In which you need put here
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		inputText.setText(sdf.format(calendar.getTime()));
		selectedDate = (calendar.getTime());
		

	}

	private void registerForLocalNotification() {
		Intent myIntent = new Intent(CheckoutActivity.this, TimeAlarm.class);
		pendingIntent = PendingIntent.getBroadcast(CheckoutActivity.this, 0,
				myIntent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (mAction.equalsIgnoreCase("retain")) {
			alarmManager.set(AlarmManager.RTC, selectedDate.getTime(),
					pendingIntent);
		} else if (mAction.equalsIgnoreCase("extend")) {
			
			alarmManager.set(AlarmManager.RTC, new Date().getTime()+1*60000,
					pendingIntent);//Integer.parseInt(extendDuration)*60*60
		}

	}

	public void registerDevice() {

		if (optionText != null && !optionText.isEmpty()) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Devices");
			query.whereEqualTo("deviceId", (String) ParseInstallation
					.getCurrentInstallation().get("deviceToken"));
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> list, ParseException e) {
					if (e == null) {
						Log.d("score", "Retrieved " + list.size() + " scores");
						if (list.size() == 0) {
							ParseObject device = new ParseObject("Devices");
							Log.d("com.parse.demo", (String) ParseInstallation
									.getCurrentInstallation()
									.get("deviceToken"));
							Log.d("com.parse.demo", android.os.Build.MODEL);
							device.put("deviceId", (String) ParseInstallation
									.getCurrentInstallation()
									.get("deviceToken"));
							device.put("deviceName", android.os.Build.MODEL);
							device.put("checkoutDate", new Date());
							device.put("deviceCurrentUser", "admin");
							device.put("deviceMake", mDeviceMake);
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
                            device.put("companyName",getCompanyName());

							device.put("expectedCheckinDate", new Date());
							device.put("registrationNumber", optionText);
                            device.put("companyName","");
							device.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									// TODO Auto-generated method stub
									if (e == null) {
										showAlertDialog("Device Registered successfully!");
										addTransactionToHistory(mUsername,
												"register", optionText);
									} else {
										Log.d("score",
												"Error: " + e.getMessage());
									}

								}
							});

						} else {
							showAlertDialog("Device Already Registered");

						}

					} else {
						Log.d("score", "Error: " + e.getMessage());
					}
				}
			});

		}
		// TODO Auto-generated method stub

	}

	public void checkoutCurrentDevice() {

		if (checkoutDuration != null && !checkoutDuration.isEmpty()) {


            Log.d("com.parse.demo","checkout "+mUsername);
            ParseQuery query1 = ParseUser.getQuery();
            query1.whereEqualTo("username", mUsername);
            query1.findInBackground(new FindCallback() {


                @Override
                public void done(List list, ParseException e) {
                    ParseUser user = (ParseUser) list.get(0);
                    Log.d("com.parse.demo", "Usernme"+user.getString("username"));
                    Log.d("com.parse.demo","Company Name"+user.getString("companyName"));
                    if(user.getString("companyName").equals(getCompanyName()))
                    {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Devices");
                        query.whereEqualTo("deviceId", (String) ParseInstallation
                                .getCurrentInstallation().get("deviceToken"));
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    Log.d("score", "Retrieved " + list.size() + " scores");
                                    if (list.size() != 0) {
                                        ParseObject device = list.get(0);
                                        if (device.get("deviceStatus").equals("Free")) {
                                            Log.d("com.parse.demo",
                                                    (String) ParseInstallation
                                                            .getCurrentInstallation().get(
                                                                    "deviceToken"));
                                            Log.d("com.parse.demo", android.os.Build.MODEL);
                                            device.put(
                                                    "deviceId",
                                                    (String) ParseInstallation
                                                            .getCurrentInstallation().get(
                                                                    "deviceToken"));
                                            device.put("deviceName", android.os.Build.MODEL);
                                            device.put("checkoutDate", new Date());
                                            device.put("deviceCurrentUser", mUsername);
//								device.put("deviceMake", "Android");
                                            device.put("deviceModel",
                                                    android.os.Build.DEVICE);
                                            device.put("deviceStatus", "Busy");
                                            device.put(
                                                    "deviceToken",
                                                    (String) ParseInstallation
                                                            .getCurrentInstallation().get(
                                                                    "deviceToken"));
                                            device.put("isCheckedOut", true);
                                            device.put("companyName",getCompanyName());
                                            device.put("deviceVersion",
                                                    android.os.Build.VERSION.RELEASE);
                                            Date a = new Date();
                                            a.setTime(System.currentTimeMillis()
                                                    + Integer.parseInt(checkoutDuration)
                                                    * (60 * 60 * 1000));

                                            device.put("expectedCheckinDate", a);
                                            mREgNumber = device
                                                    .getString("registrationNumber");
                                            device.saveInBackground(new SaveCallback() {

                                                @Override
                                                public void done(ParseException e) {
                                                    // TODO Auto-generated method stub
                                                    if (e == null) {

                                                        showAlertDialog("Device Checked out successfully!");
                                                        addTransactionToHistory(mUsername,
                                                                "checkout", mREgNumber);
                                                        Intent returnIntent = new Intent();
                                                        returnIntent.putExtra("result", 1);
                                                        setResult(RESULT_OK, returnIntent);
                                                        finish();
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
                    else
                    {
                        showAlertDialog("Please enter a valid username");
                    }


                }
            });









		}
		// TODO Auto-generated method stub

	}

	public void retainCurrentDevice() {

		if (retainDuration != null && !retainDuration.isEmpty()) {
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
								Log.d("com.parse.demo",
										(String) ParseInstallation
												.getCurrentInstallation().get(
														"deviceToken"));
								Log.d("com.parse.demo", android.os.Build.MODEL);
								device.put(
										"deviceId",
										(String) ParseInstallation
												.getCurrentInstallation().get(
														"deviceToken"));
								device.put("deviceName", android.os.Build.MODEL);
								device.put("checkoutDate", new Date());
								device.put("deviceCurrentUser", mUsername);
								device.put("deviceMake", "Android");
								device.put("deviceModel",
										android.os.Build.DEVICE);
								device.put("deviceStatus", "Busy");
								device.put(
										"deviceToken",
										(String) ParseInstallation
												.getCurrentInstallation().get(
														"deviceToken"));
								device.put("isCheckedOut", true);
                                device.put("companyName",getCompanyName());
								device.put("deviceVersion",
										android.os.Build.VERSION.RELEASE);
								// Date a=new Date();
								// a.setTime(System.currentTimeMillis()+Integer.parseInt(retainDuration)*(60*60*1000));

								device.put("expectedCheckinDate", selectedDate);
								mREgNumber = device
										.getString("registrationNumber");
								device.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException e) {
										// TODO Auto-generated method stub
										if (e == null) {

											showAlertDialog("Device retained successfully!");
											addTransactionToHistory(mUsername,
													"retain", mREgNumber);
											registerForLocalNotification();
											Intent returnIntent = new Intent();
											returnIntent.putExtra("result", 1);
											setResult(RESULT_OK, returnIntent);
											finish();
										} else {
											Log.d("score",
													"Error: " + e.getMessage());
										}

									}
								});

							} else {
								String msg = "Ask Admin to checkout the device!!";
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
		// TODO Auto-generated method stub

	}

	public void extendCurrentDevice() {

		if (extendDuration != null && !extendDuration.isEmpty()) {
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
								Log.d("com.parse.demo",
										(String) ParseInstallation
												.getCurrentInstallation().get(
														"deviceToken"));
								Log.d("com.parse.demo", android.os.Build.MODEL);
								device.put(
										"deviceId",
										(String) ParseInstallation
												.getCurrentInstallation().get(
														"deviceToken"));
								device.put("deviceName", android.os.Build.MODEL);
								device.put("checkoutDate", new Date());
								device.put("deviceCurrentUser", mUsername);
								device.put("deviceMake", "Android");
								device.put("deviceModel",
										android.os.Build.DEVICE);
								device.put("deviceStatus", "Busy");
								device.put(
										"deviceToken",
										(String) ParseInstallation
												.getCurrentInstallation().get(
														"deviceToken"));
								device.put("isCheckedOut", true);
                                device.put("companyName",getCompanyName());
								device.put("deviceVersion",
										android.os.Build.VERSION.RELEASE);
								Date a = new Date();
								a.setTime(System.currentTimeMillis()
										+ Integer.parseInt(extendDuration)
										* (60 * 60 * 1000));

								device.put("expectedCheckinDate", a);
								mREgNumber = device
										.getString("registrationNumber");
								device.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException e) {
										// TODO Auto-generated method stub
										if (e == null) {

											showAlertDialog("Device Checked out successfully!");
											addTransactionToHistory(mUsername,
													"extend", mREgNumber);
											registerForLocalNotification();
											Intent returnIntent = new Intent();
											returnIntent.putExtra("result", 1);
											setResult(RESULT_OK, returnIntent);
											//finish();
										} else {
											Log.d("score",
													"Error: " + e.getMessage());
										}

									}
								});

							} else {
								String msg = "Ask Admin to checkout the device!!";
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
		// TODO Auto-generated method stub

	}

	public void showAlertDialog(String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				CheckoutActivity.this);
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

	/**
	 * -(void)addTransactionToHistory:(NSString*)username
	 * action:(NSString*)action { PFObject *lNewHistoryRow = [[PFObject
	 * alloc]initWithClassName:@"DeviceHistory"]; [lNewHistoryRow
	 * setObject:[[[UIDevice currentDevice] identifierForVendor]
	 * UUIDString]forKey:@"deviceId"]; [lNewHistoryRow setObject:[UIDevice
	 * currentDevice].name forKey:@"deviceName"]; if([action
	 * isEqualToString:@"checkout"]) { [lNewHistoryRow setObject:[NSDate date]
	 * forKey:@"checkoutDate"]; } else if([action isEqualToString:@"checkin"]) {
	 * [lNewHistoryRow setObject:[NSDate date] forKey:@"checkinDate"]; }
	 * 
	 * [lNewHistoryRow setObject:username forKey:@"deviceUser"]; [lNewHistoryRow
	 * setObject:@"Apple" forKey:@"deviceMake"]; [lNewHistoryRow setObject:[self
	 * platformNiceString] forKey:@"deviceModel"]; AppDelegate *appDelegate =
	 * [[UIApplication sharedApplication]delegate]; [lNewHistoryRow
	 * setObject:appDelegate.mDeviceToken forKey:@"deviceToken"];
	 * [lNewHistoryRow setObject:action forKey:@"action"]; // if([action
	 * isEqualToString:@"register"]) // { [lNewHistoryRow
	 * setObject:self.mRegistrartionNumber forKey:@"registrationNumber"]; //}
	 * [lNewHistoryRow saveInBackgroundWithBlock:^(BOOL succeeded, NSError
	 * *error) { if(succeeded) { NSLog(@"Transction Saved"); } else {
	 * NSLog(@"Error while saving %@",[[error userInfo] objectForKey:@"error"]
	 * ); } }];
	 * 
	 * }
	 */

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
        device.put("companyName",getCompanyName());
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

    public String getCompanyName()
    {
        ToDoListApplication application = (ToDoListApplication)getApplicationContext();
        String companyName = application.getCompanyName();
        if(companyName != "" || companyName != null)
        {
            return application.getCompanyName();
        }
        else
        {
            return "No Valid Company";
        }

    }
}
