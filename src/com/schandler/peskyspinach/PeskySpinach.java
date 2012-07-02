package com.schandler.peskyspinach;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class PeskySpinach extends Activity {
	
	private static final int CONTACT_PICKER_RESULT = 1001;
	private SpinachWorker mSpinachWorker;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the user interface layout
        setContentView(R.layout.main);
        
        // Make sure running HC or higher to use ActionBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
        	ActionBar actionBar = getActionBar();
        	actionBar.setHomeButtonEnabled(false);
        }
        
        mSpinachWorker = new SpinachWorker(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.who_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override public boolean onOptionsItemSelected(MenuItem item) { 
    	switch(item.getItemId()) {
	    	case R.id.pick_contact: 
	    		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
	    		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	    		return true;
	    	default: return super.onOptionsItemSelected(item);
    	}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
				Uri result = data.getData();
				mSpinachWorker.setContactId(result.getLastPathSegment());
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
