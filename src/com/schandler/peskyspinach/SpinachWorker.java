package com.schandler.peskyspinach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SpinachWorker {

	private static final String TAG = "SpinachDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE =
			"create table notes (_id integer primary key autoincrement, "
					+ "title text not null, body text not null);";

	private static final String DATABASE_NAME = "spinach";
	private static final String DATABASE_TABLE = "messages";
	private static final int DATABASE_VERSION = 2;
	public static final String KEY_CONTACT_ID = "contact_id";
	public static final String KEY_CONTACT_PHONE = "contact_phone";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_MEDIUM = "medium";
	public static final String KEY_ROWID = "_id";

	public static final String PREFS_NAME = "SpinachPrefsFile";
	public static String TWILIO_ACCOUNT_SID;
	public static String TWILIO_AUTH_TOKEN;
	public static String TWILIO_NUMBER;
	private SharedPreferences mSpinachPreferences;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper { 
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public SpinachWorker(Context ctx) {
		this.mCtx = ctx;
		mSpinachPreferences = ctx.getSharedPreferences(PREFS_NAME, 0);
		TWILIO_ACCOUNT_SID = mCtx.getString(R.string.twilio_auth_token);
		TWILIO_AUTH_TOKEN = mCtx.getString(R.string.twilio_account_sid);
		TWILIO_NUMBER = mCtx.getString(R.string.twilio_number);
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public SpinachWorker open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void setContactId(String contactId) {
		//    	ContentValues initialValues = new ContentValues();
		//    	initialValues.put(KEY_contactId)
		//      mDb.insert(DATABASE_TABLE, null, initialValues);
		SharedPreferences.Editor editor = mSpinachPreferences.edit();
		editor.putString(KEY_CONTACT_ID, contactId);
		editor.commit();
	}

	public String getContactId() {
		return (mSpinachPreferences.getString(KEY_CONTACT_ID, "-1"));
	}
	
	public void setNumber(String number) {
		SharedPreferences.Editor editor = mSpinachPreferences.edit();
		editor.putString(KEY_CONTACT_PHONE, number);
		editor.commit();
	}
	
	public String getNumber() {
		return (mSpinachPreferences.getString(KEY_CONTACT_PHONE, "-1"));
	}
	
	public void setMessage(String message) {
		SharedPreferences.Editor editor = mSpinachPreferences.edit();
		editor.putString(KEY_MESSAGE, message);
		editor.commit();
	}
	
	public String getMessage() {
		return (mSpinachPreferences.getString(KEY_MESSAGE, "-1"));
	}

 	public void sendTheSMS (String contactPhone, String message) {  
		SharedPreferences.Editor editor = mSpinachPreferences.edit();
		contactPhone = contactPhone.replaceAll("\\D","");
		editor.putString(KEY_CONTACT_PHONE, contactPhone);
		editor.putString(KEY_MESSAGE, message);
		editor.commit();
		new Thread(new Runnable() {
			public void run(){

				String message = mSpinachPreferences.getString(KEY_MESSAGE, "-1");
				String contactPhone = mSpinachPreferences.getString(KEY_CONTACT_PHONE, "-1");
				HttpClient httpClient = new DefaultHttpClient();
				String result = "";
				String apiUrl = "https://api.twilio.com";
				String smsPath = "/2010-04-01/Accounts/" + TWILIO_ACCOUNT_SID + "/SMS/Messages.json";
				String deviceId = "xxxxx" ;

				HttpPost httpPost = new HttpPost(apiUrl + smsPath );
				httpPost.addHeader("deviceId", deviceId);

				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("From", SpinachWorker.TWILIO_NUMBER));
					nameValuePairs.add(new BasicNameValuePair("To", contactPhone));
					nameValuePairs.add(new BasicNameValuePair("Body", message));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					((DefaultHttpClient) httpClient).getCredentialsProvider().setCredentials(
							new AuthScope(null, -1),
							new UsernamePasswordCredentials(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN));
					
					// Execute HTTP Post Request
					HttpResponse response = httpClient.execute(httpPost);
					 HttpEntity resEntity = response.getEntity();  
				        if (resEntity != null) {  
				                    //do something with the response
				                    Log.i("GET RESPONSE",EntityUtils.toString(resEntity));
				        }
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("EXCEPTION", e.toString());
				}
				
				System.out.println(httpPost.toString());
				httpClient.getConnectionManager().shutdown();
			}
		}).start();

	}
}
