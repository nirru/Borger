package com.oxilo.borger;

import com.util.Util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class Menu extends Activity {
	
	EditText ed_message;
	Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_menu);
		init();
	}
	
	/*-------------init VCaribale here -----------*/
	
	private void init()
	{
		mContext = Menu.this;
		Log.e("MESSAGE DETAIL", "" + Util.readFromPrefs(mContext));
		ed_message = (EditText)findViewById(R.id.message);
		ed_message.setText(Util.readFromPrefs(mContext));
	}

}
