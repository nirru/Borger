package com.oxilo.borger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

	EditText ed_user_Name, ed_user_Password;
	Button submit_btn;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		init();
	}

	/*------------------init varibale here-------------------*/
	private void init() {
		// TODO Auto-generated method stub
		mContext = Login.this;
		ed_user_Name = (EditText) findViewById(R.id.user_name);
		ed_user_Password = (EditText) findViewById(R.id.user_password);
		submit_btn = (Button) findViewById(R.id.submit_btn);

		submit_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isUserNameEmpty()) {
					ed_user_Name.setError(getResources().getString(
							R.string.error_user_name_empty));
					return;
				}
				if (isUserPasswordEmpty()) {
					ed_user_Password.setError(getResources().getString(
							R.string.error_user_password_empty));
					return;
				} else {
					login();
				}
			}
		});
	}

	private boolean isUserNameEmpty() {
		if (ed_user_Name.getText().toString().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUserPasswordEmpty() {
		if (ed_user_Password.getText().toString().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private void login() {
		Intent i = new Intent(mContext, Menu.class);
		startActivity(i);
		finish();
	}

}
