package com.schandler.peskyspinach;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.QuickContactBadge;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseOffense extends Activity {

	private SpinachWorker mSpinachWorker;
	private String mContactId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_offense); 
		mSpinachWorker = new SpinachWorker(this);
		mContactId = mSpinachWorker.getContactId();
		
		Spinner spinner = (Spinner) findViewById(R.id.offense_spinner); 
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
		
		Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, mContactId);
		QuickContactBadge contactBadge = (QuickContactBadge) findViewById(R.id.contact_badge);
		contactBadge.assignContactUri(contactUri);
		contactBadge.setMode(ContactsContract.QuickContact.MODE_LARGE);
		InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(), contactUri);
		if (input != null) {
		contactBadge.setImageBitmap(BitmapFactory.decodeStream(input));
		}
		TextView contactName = (TextView) findViewById(R.id.contact_name);
		Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		try {
			int nameIndex = people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
		    people.moveToFirst();
		    contactName.setText(people.getString(nameIndex));
		} finally {
		    people.close();
		}
		

	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String message = parent.getItemAtPosition(pos).toString();
			String phoneNumber = "";
			Toast.makeText(parent.getContext(), "something: " + 
				message, Toast.LENGTH_LONG).show();
//			String[] projection = new String[] { Phone.NUMBER };
			Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, null,  Phone.CONTACT_ID + " = " + mSpinachWorker.getContactId(), null, null);
			while (cursor.moveToNext()) {
				phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
				// int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
				// switch (type) {
				// case Phone.TYPE_HOME: break;
				// case Phone.TYPE_MOBILE: break;
				// case Phone.TYPE_WORK: break;
				// }
			}

			mSpinachWorker.sendTheSMS(phoneNumber, message);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// do nothing
		}
	}
	

}
