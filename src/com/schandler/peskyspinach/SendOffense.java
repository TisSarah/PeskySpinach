/**
 * 
 */
package com.schandler.peskyspinach;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Menu;
import android.view.MenuInflater;
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
		
        // Make sure running HC or higher to use ActionBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	ActionBar actionBar = getActionBar();
        	actionBar.setHomeButtonEnabled(true);
        }
		
		mSpinachWorker = new SpinachWorker(this);
		
		if(savedInstanceState != null) {
			mSpinachWorker.setMessage(savedInstanceState.getString(mSpinachWorker.KEY_MESSAGE));
			mSpinachWorker.setContactId(savedInstanceState.getString(mSpinachWorker.KEY_CONTACT_ID));
		}
		
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
		callButton.setOnClickListener(new MyOnClickListener());
		
		smsButton.setEnabled(false);
		callButton.setEnabled(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.send_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(mSpinachWorker.KEY_CONTACT_ID, mSpinachWorker.getContactId());
		savedInstanceState.putString(mSpinachWorker.KEY_MESSAGE, mSpinachWorker.getMessage());
		
		super.onSaveInstanceState(savedInstanceState);
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			
			String selectedNumber = parent.getItemAtPosition(pos).toString();
			mSpinachWorker.setNumber(selectedNumber);
			
			Button smsButton = (Button) findViewById(R.id.send_sms_button); 
			Button callButton = (Button) findViewById(R.id.make_call_button);
			
			smsButton.setEnabled(true);
			callButton.setEnabled(true);
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
			if(view.getId() == R.id.make_call_button) {
				mSpinachWorker.makeTheCall(mSpinachWorker.getNumber(), mSpinachWorker.getMessage());
			}
			SendOffense.this.finish();
		}
	}
}
