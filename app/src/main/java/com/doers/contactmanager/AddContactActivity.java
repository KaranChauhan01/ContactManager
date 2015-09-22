package com.doers.contactmanager;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Activity to add new Contacts
 * Created by Karna on 9/13/2015.
 */
public class AddContactActivity extends Activity {

    private static final String TAG = "AddContactActivity";

    private EditText name;
    private EditText email;
    private EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        name = (EditText) findViewById(R.id.addContact_name);
        email = (EditText) findViewById(R.id.addContact_email);
        phone = (EditText) findViewById(R.id.addContact_phone);
    }

    public void addNewContact(View view) {
        if (name.getText().toString().isEmpty() && email.getText().toString().isEmpty() && phone.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Contact's Details", Toast.LENGTH_SHORT).show();
        } else if(email.getText().toString().equals("") && phone.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Either Phone or Email must be entered.", Toast.LENGTH_SHORT).show();
        } else {
            String name = this.name.getText().toString();
            String phone = this.phone.getText().toString();
            String email = this.email.getText().toString();

            ArrayList < ContentProviderOperation > ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                    .withValue(RawContacts.ACCOUNT_NAME, null)
                    .build());
            if (!name.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.DISPLAY_NAME, name).build());
            }

            if (!phone.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                        .withValue(Phone.NUMBER, phone)
                        .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                        .build());
            }

            if (!email.isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                        .withValue(Email.DATA, email)
                        .withValue(Email.TYPE, Email.TYPE_WORK)
                        .build());
            }

            try {
                ContentProviderResult[] c = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                String [] newContact = getLookUpKey(c[1].uri.getPath());
                if(newContact != null) {
                    Intent intent = new Intent(this, ContactDisplayActivity.class);
                    intent.putExtra("startedByAddContact", true);
                    intent.putExtra("mContactId", Long.valueOf(newContact[0]));
                    intent.putExtra("mContactKey", newContact[1]);
                    startActivityForResult(intent, 102);
                }
                /*Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra("Contact_Created", true);
                NavUtils.navigateUpTo(this, intent);*/
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private String[] getLookUpKey(String path) {
        String [] splitArray = path.split("/");
        Cursor cursor = getContentResolver().query(Data.CONTENT_URI,
                new String[]{Data.CONTACT_ID, Data.LOOKUP_KEY},
                Data._ID + " = ?",
                new String[]{splitArray[2]},
                null);
        if(cursor.moveToFirst()) {
            return new String[]{Long.toString(cursor.getLong(0)), cursor.getString(1)};
        } else {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 102) {
            finish();
        }
    }
}
