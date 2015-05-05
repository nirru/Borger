package com.oxilo.borger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.borger.volley.MyVolley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;

public class Login extends Activity {

	EditText ed_user_Name, ed_user_Password;
	Button submit_btn;
	Context mContext;
	ProgressDialog pDialog;
	static String TAG = "VOLLEY";
	String uid, gcm_id;

	/*--- GCM DETAIL--------*/
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "362562150738";

	/**
	 * Tag used on log messages.
	 */

	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;

	String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		init();
		checkGcmIsRegisteredOrNot();
	}

	/*------------------init varibale here-------------------*/
	private void init() {
		// TODO Auto-generated method stub
		mContext = Login.this;
		ed_user_Name = (EditText) findViewById(R.id.user_name);
		ed_user_Password = (EditText) findViewById(R.id.user_password);
		submit_btn = (Button) findViewById(R.id.submit_btn);

		// ed_user_Name.setText("ghanta@yahoo.com");
		// ed_user_Password.setText("123456s");

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Loading...");

		submit_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isUserNameEmpty()) {
					ed_user_Name.setError(getResources().getString(
							R.string.error_user_name_empty));
					return;
				}
				
				if (!checkEmailPattern()) {
					ed_user_Name.setError(getResources().getString(
							R.string.error_user_name_invalid));
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
	
	public boolean checkEmailPattern() {
        Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,100}"
                + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,10}" + "(" + "."
                + "[a-zA-Z0-9][a-zA-Z0-9-]{0,20}" + ")+");
        if (!EMAIL_PATTERN.matcher(ed_user_Name.getText().toString())
                .matches()) {
            return false;
        }
        return true;
    }

	private boolean isUserPasswordEmpty() {
		if (ed_user_Password.getText().toString().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private void login() {
		callLoginApi();

	}

	private void checkGcmIsRegisteredOrNot() {
		regid = getRegistrationId(getApplicationContext());
		Log.e("REGISTER ID IN BACKGROUND", " " + regid);
		if (!regid.isEmpty()) {
			Log.e("MAI CALL HO GAYA", "" + "HAHAH");
			goInsideApp();
		}
	}

	private void callLoginApi() {
		// Tag used to cancel the request
		RequestQueue queue = MyVolley.getRequestQueue(mContext);
		String url = "http://studentthadi.com/development/borger/administrator/Api/signin.php";

		pDialog.show();

		final String num1 = ed_user_Name.getText().toString();
		final String num2 = ed_user_Password.getText().toString();
		if (num1 != null && !num1.equals("") && num2 != null
				&& !num2.equals("")) {
			StringRequest myReq = new StringRequest(Method.POST, url,
					createMyReqSuccessListener(), createMyReqErrorListener()) {

				protected Map<String, String> getParams()
						throws com.android.volley.AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("email", num1);
					params.put("password", num2);
					return params;
				};
			};
			queue.add(myReq);
		}

	}

	private Response.Listener<String> createMyReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d(TAG, response.toString());
				try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if (status.toString().equals("1")) {
						uid = jsonObject.getString("uid");
						Log.e("MY UID TO SHOW", "" + uid);
						isRegisteredOnGCM(1);
					} else {
						showOKAleart(mContext, "Error", "Login Error");
						pDialog.cancel();
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(TAG, "Error: " + error.getMessage());
				pDialog.cancel();
			}
		};
	}

	private void isRegisteredOnGCM(int k) {
		// if (checkPlayServices()) {
		gcm = GoogleCloudMessaging.getInstance(this);
		regid = getRegistrationId(context);
		Log.e("REGISTER ID IN BACKGROUND", " " + regid);
		if (regid.isEmpty()) {
			Log.e("MAI CALL HO GAYA", "" + "HAHAH");
			registerInBackground(k);
		} else {
			goInsideApp();
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p/>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */

	private void registerInBackground(final int k) {

		new AsyncTask<String, String, String>() {

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					Log.e("ID RECEIVED IN BACK GROUND", "" + msg);
					// You should send the registration ID to your server over
					// HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your
					// app.
					// The request to your server should be authenticated if
					// your app
					// is using accounts.
					// sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device
					// will send upstream messages to a server that echo back
					// the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(getApplicationContext(), regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				sendRegistrationIdToBackend();
				// goInsideApp();
			}

		}.execute(null, null, null);

	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
		// Tag used to cancel the request
		RequestQueue queue = MyVolley.getRequestQueue(mContext);
		String url = "http://studentthadi.com/development/borger/administrator/Api/update.php";

		if (uid != null && !uid.equals("") && regid != null
				&& !regid.equals("")) {
			StringRequest myReq = new StringRequest(Method.POST, url,
					createUpdateReqSuccessListener(),
					createUpdateReqErrorListener()) {

				protected Map<String, String> getParams()
						throws com.android.volley.AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("uid", uid);
					params.put("gcm_id", regid);
					params.put("device_type", "Android");
					Log.e("UPDATWEDWEEDW", "" + params);
					return params;
				};
			};
			queue.add(myReq);
		}
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {

		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(Login.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {

			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p/>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private Response.Listener<String> createUpdateReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("MERA JADU CHAL GAYA", response.toString());
				try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if (status.toString().equals("1")) {
						pDialog.cancel();
						goInsideApp();
					} else if (status.toString().equals("0")){
						pDialog.cancel();
						goInsideApp();
					}
					else{
						showOKAleart(mContext, "Error", "Login Error");
						pDialog.cancel();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		};
	}

	private Response.ErrorListener createUpdateReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("WOW MERE CHALIYA", "Error: " + error.getMessage());
				pDialog.cancel();
			}
		};
	}

	private void goInsideApp() {
		Intent i = new Intent(mContext, Menu.class);
		startActivity(i);
		finish();
	}

	@SuppressWarnings("deprecation")
	public void showOKAleart(Context context, String title, String message) {
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
