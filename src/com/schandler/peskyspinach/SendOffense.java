/**
 * 
 */
package com.schandler.peskyspinach;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * @author Sarah
 *
 */
public class SendOffense extends Activity {

	private SpinachWorker mSpinachWorker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_where); 
		mSpinachWorker = new SpinachWorker(this);
		
		Spinner spinner = (Spinner) findViewById(R.id.number_spinner); 
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
		
		String[] projection = new String[] { Phone.NUMBER };
		Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, projection,  Phone.CONTACT_ID + " = " + mSpinachWorker.getContactId(), null, null);
		int numPhoneNumbers = cursor.getCount();
		String[] numbers = new String[numPhoneNumbers];
		for(int i = 0; i < numPhoneNumbers; i++) {
			cursor.moveToNext();
			int columnIndex = cursor.getColumnIndex(Phone.NUMBER);
			 numbers[i] = cursor.getString(columnIndex);
		}
		
		ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, numbers);
		spinner.setAdapter(spinnerAdapter);
		
		Button smsButton = (Button) findViewById(R.id.send_sms_button); 
		smsButton.setOnClickListener(new MyOnClickListener());
		Button callButton = (Button) findViewById(R.id.make_call_button);
		smsButton.setOnClickListener(new MyOnClickListener());
		
		smsButton.setEnabled(false);
		callButton.setEnabled(false);
		
		//Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, null,  Phone.CONTACT_ID + " = " + mSpinachWorker.getContactId(), null, null);
		//while (cursor.moveToNext()) {
		//	String phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
		//}
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			
			String selectedNumber = parent.getItemAtPosition(pos).toString();
			mSpinachWorker.setNumber(selectedNumber);
			
			Button smsButton = (Button) findViewById(R.id.send_sms_button); 
			Button callButton = (Button) findViewById(R.id.make_call_button);
			
			smsButton.setEnabled(true);
			//don't support phone calls yet
			//callButton.setEnabled(true);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// do nothing
		}
	}
	
	public class MyOnClickListener implements OnClickListener {

		public void onClick(View view){
			if(view.getId() == R.id.send_sms_button) {
				mSpinachWorker.sendTheSMS(mSpinachWorker.getNumber(), mSpinachWorker.getMessage());
			}
		}
	}
}
