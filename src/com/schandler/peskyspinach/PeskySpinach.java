package com.schandler.peskyspinach;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.view.View;

public class PeskySpinach extends Activity {
	
	private static final int CONTACT_PICKER_RESULT = 1001;
	private SpinachWorker mSpinachWorker;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSpinachWorker = new SpinachWorker(this);
        // mSpinachWorker.open();
    }
    
    public void doLaunchContactPicker(View view) {
    	Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
    	startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle = data.getExtras();
		
		if(resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
				Uri result = data.getData();
				mSpinachWorker.setTargetContactId(result.getLastPathSegment());
				Intent i = new Intent(this, ChooseOffense.class);
				i.putExtra(SpinachWorker.KEY_CONTACT_ID, result);
				startActivityForResult(i, 0);
				break;
			}
		} else {
			// todo:: handle activity result not okay
		}
	}
    
	
}
