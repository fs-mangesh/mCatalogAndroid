package com.parse.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {
	String usernametxt;
	String passwordtxt;
	String emailtxt;
    String confirmPasswordTxt;
	EditText username;
	EditText password;
	EditText email;
    EditText confirmPassword;
    private CheckBox adminChkBox;
    private CheckBox userChkBox;
    private EditText companyName;

	int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_layout);
		
		Button signup = (Button)findViewById(R.id.btnsignFrgtPasswd);
		username = (EditText)findViewById(R.id.signupUsername);
		password = (EditText)findViewById(R.id.signupPassword);
		email = (EditText)findViewById(R.id.signupEmailTxt);
        confirmPassword = (EditText)findViewById(R.id.signupConfirmPassword);
        adminChkBox = (CheckBox)findViewById(R.id.checkBtnAdmin);
        companyName = (EditText)findViewById(R.id.signupCompanyName);

		signup.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                emailtxt = email.getText().toString();
                confirmPasswordTxt = confirmPassword.getText().toString();
                String company = companyName.getText().toString();

				// Force user to fill up the form
				if (usernametxt.equals("") || passwordtxt.equals("") || emailtxt.equals("") || confirmPasswordTxt.equals("") || company.equals("")) {

                    showAlertDialog("Please complete the sign up form");

				}
                else {
					// Save new user data into Parse.com Data Storage
                    if(passwordtxt.equals(confirmPasswordTxt)) {
                        ParseUser user = new ParseUser();
                        user.setUsername(usernametxt);
                        user.setPassword(passwordtxt);
                        user.setEmail(emailtxt);
                        user.put("isAdmin",adminChkBox.isChecked());
                        user.put("companyName",companyName.getText().toString());
                        user.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    // Show a simple Toast message upon successful
                                    // registration
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Successfully Signed up, please log in.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    showAlertDialog(e.toString());
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                "Password does not match",
                                Toast.LENGTH_LONG).show();
                    }
				}

			}
		});



	}
    public void showAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SignUpActivity.this);
        alertDialogBuilder.setTitle("mCatalogue");
        alertDialogBuilder.setMessage(msg).setCancelable(true)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void adminBtnClicked(View v)
    {
        Log.d("com.parse.demo", "admin checkBTnclicked");
    }


}
