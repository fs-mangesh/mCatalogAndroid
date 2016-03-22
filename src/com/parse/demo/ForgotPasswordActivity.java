package com.parse.demo;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends Activity {

	String emailtxt;
	EditText email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);

		Button btnReset = (Button) findViewById(R.id.btnResetPasswd);
		email = (EditText) findViewById(R.id.frgtPasswdTxt);

		btnReset.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				emailtxt = email.getText().toString();
				// Force user to fill up the form
				if (emailtxt.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please enter valid email id",
							Toast.LENGTH_LONG).show();

				} else {
					// Save new user data into Parse.com Data Storage
					ParseUser user = new ParseUser();
					user.setEmail(emailtxt);
					ParseUser.requestPasswordResetInBackground(emailtxt,
							new RequestPasswordResetCallback() {
								public void done(ParseException e) {
									if (e == null) {
										// Show a simple Toast message upon
										// successful
										// registration
										Toast.makeText(
												getApplicationContext(),
												"Email has been sent to "+emailtxt+".Please check your mailbox",
												Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(getApplicationContext(),
												"Error",
												Toast.LENGTH_LONG).show();
									}
								}
							});

				}

			}
		});

	}
}
