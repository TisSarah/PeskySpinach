package com.schandler.peskyspinach;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseOffense extends Activity {

	private SpinachWorker mSpinachWorker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.choose_offense); 
		mSpinachWorker = new SpinachWorker(this);
		
		// if contactId in Bundle
		if(savedInstanceState != null) {
			mSpinachWorker.setContactId(savedInstanceState.getString(mSpinachWorker.KEY_CONTACT_ID));
		}
		
		String contactId = mSpinachWorker.getContactId();
		
		ListView offenseList = (ListView) findViewById(R.id.offense_list); 
		String[] offenses = getResources().getStringArray(R.array.offense_array);
		offenseList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, offenses));
		offenseList.setOnItemClickListener(new MyOnItemClickListener());
		
		Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
		ImageView contactImage = (ImageView) findViewById(R.id.contact_image);
		InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(), contactUri);
		if (input != null) {
			contactImage.setImageBitmap(BitmapFactory.decodeStream(input));
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
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(mSpinachWorker.KEY_CONTACT_ID, mSpinachWorker.getContactId());
		
		super.onSaveInstanceState(savedInstanceState);
	}

	public class MyOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
			String message = parent.getItemAtPosition(pos).toString();
			mSpinachWorker.setMessage(message);
			
			Intent i = new Intent(ChooseOffense.this, SendOffense.class);
			startActivityForResult(i, 0);
			ChooseOffense.this.finish();
		}
	}
}
