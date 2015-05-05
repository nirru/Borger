package com.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.oxilo.borger.constant.AppConstant;

/**
 * Created by C-ShellWin on 12/12/2014.
 */
public class Util {

	public static void writeToPrefrefs(Context mContext, String result) {
		SharedPreferences.Editor prefs = mContext.getSharedPreferences(
				AppConstant.PREFS_USER_MESSAGE, Context.MODE_PRIVATE).edit();
		prefs.putString(AppConstant.PREFS_MESSAGE_DETAILS, result);
		prefs.commit();
	}

	public static String readFromPrefs(Context mContext) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				AppConstant.PREFS_USER_MESSAGE, mContext.MODE_PRIVATE);
		return sharedPreferences.getString(AppConstant.PREFS_MESSAGE_DETAILS,
				"");
	}

	@SuppressWarnings("deprecation")
	public static void showOKAleart(Context context, String title,
			String message) {
		final AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setTitle("Message");
		alertDialog.setMessage(message);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}
}
