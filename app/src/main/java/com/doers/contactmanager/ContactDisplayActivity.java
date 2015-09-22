package com.doers.contactmanager;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Contact Details Page
 * Created by Karna on 9/19/2015.
 */
public class ContactDisplayActivity extends Activity {

    private static final String TAG = "ContactDisplayActivity";
    private String name;
    ArrayList<String> emailIds;
    ArrayList<String> phoneNo;

    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        Bundle bundle = new Bundle();
        bundle.putLong("mContactId", getIntent().getLongExtra("mContactId", 0L));
        bundle.putString("mContactKey", getIntent().getStringExtra("mContactKey"));
        saveButton = (Button) findViewById(R.id.saveButton);
        ContactDetailsFragment contactDetailsFragment = new ContactDetailsFragment();
        contactDetailsFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contactDetails_fragment, contactDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        if(getIntent().getBooleanExtra("startedByAddContact", false)) {
            Toast.makeText(getApplicationContext(), "Contact Added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 1) {
            Intent returnIntent = getIntent();
            if(returnIntent.getBooleanExtra("startedByAddContact", false)) {
                setResult(102, returnIntent);
            } else {
                setResult(101, returnIntent);
            }
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit     :   prepareEditFragment();
                                        return true;
            case R.id.menu_delete   :   deleteContact();
                                        return true;
            default                 :   return super.onOptionsItemSelected(item);
        }
    }

    private void prepareEditFragment() {
        LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        LinearLayout emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phoneLayout);

        emailIds = new ArrayList<>();
        phoneNo = new ArrayList<>();

        name = ((TextView)nameLayout.getChildAt(1)).getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        for(int i = 1; i < emailLayout.getChildCount(); i++) {
            emailIds.add(((TextView)emailLayout.getChildAt(i)).getText().toString());
        }
        for(int i = 1; i < phoneLayout.getChildCount(); i++) {
            phoneNo.add(((TextView)phoneLayout.getChildAt(i)).getText().toString());
        }

        bundle.putStringArrayList("email", emailIds);
        bundle.putStringArrayList("phone", phoneNo);
        ContactEditFragment contactEditFragment = new ContactEditFragment();
        contactEditFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contactDetails_fragment, contactEditFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void saveEditedDetails(View view){
        LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        LinearLayout emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phoneLayout);
        Map<String, String> editedDetails  = new HashMap<>();
        int i = 0;
        boolean flag = true;
        if(!name.equalsIgnoreCase(((TextView)nameLayout.getChildAt(++i)).getText().toString())) {
            editedDetails.put("name", ((TextView)nameLayout.getChildAt(i)).getText().toString());
            flag = false;
        }
        i=0;
        for (String phone : phoneNo) {
            if (!phone.equalsIgnoreCase(((TextView) phoneLayout.getChildAt(++i)).getText().toString())) {
                editedDetails.put("phone_"+i, ((TextView)phoneLayout.getChildAt(i)).getText().toString());
                flag = false;
            }
        }
        i = 0;
        for (String email : emailIds) {
            if (!email.equalsIgnoreCase(((TextView) emailLayout.getChildAt(++i)).getText().toString())) {
                editedDetails.put("email_"+i, ((TextView)emailLayout.getChildAt(i)).getText().toString());
                flag = false;
            }
        }
        //Intent intent = new Intent(this, ContactManager.class);
        if(!flag) {
            String idSelection = Data.CONTACT_ID + " = ? AND ";
            String nameSelection = Data.MIMETYPE + " = '" + StructuredName.CONTENT_ITEM_TYPE + "'";
            String phoneSelection = Data.MIMETYPE + " = '" + Phone.CONTENT_ITEM_TYPE + "'";
            String emailSelection = Data.MIMETYPE + " = '" + Email.CONTENT_ITEM_TYPE + "'";
            String[] selectionArgs = {Long.toString(getIntent().getLongExtra("mContactId", 0L))};
            ArrayList <ContentProviderOperation> ops = new ArrayList<>();

            for(String key : editedDetails.keySet()) {
                if(key.equalsIgnoreCase("name")) {
                    ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                            .withSelection(idSelection+nameSelection, selectionArgs)
                            .withValue(StructuredName.DISPLAY_NAME, editedDetails.get(key))
                            .build());
                } else if(key.contains("phone")) {
                    ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                            .withSelection(idSelection+phoneSelection, selectionArgs)
                            .withValue(Phone.NUMBER, editedDetails.get(key))
                            .build());
                } else {
                    ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                            .withSelection(idSelection+emailSelection, selectionArgs)
                            .withValue(Email.ADDRESS, editedDetails.get(key))
                            .build());
                }
            }

            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                getFragmentManager().popBackStack();
                Toast.makeText(getApplicationContext(), "Contact Edited", Toast.LENGTH_SHORT).show();
            } catch (RemoteException | OperationApplicationException e) {
                Log.e(TAG,e.getMessage());
            }
        } else {
            getFragmentManager().popBackStack();
            Toast.makeText(getApplicationContext(), "No Changes Made", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteContact() {
        String idSelection = RawContacts.CONTACT_ID + " = ? ";
        String[] selectionArgs = {Long.toString(getIntent().getLongExtra("mContactId", 0L))};
        ArrayList <ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
                .withSelection(idSelection, selectionArgs)
                .build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Intent returnIntent = getIntent();
            returnIntent.putExtra("Contact_Deleted", true);
            setResult(101, returnIntent);
            finish();
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG,e.getMessage());
        }
    }
}
