package com.doers.contactmanager;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for searching Contacts
 * Created by Karna on 9/13/2015.
 */
public class SearchContactFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY :
                    Contacts.DISPLAY_NAME
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            R.id.contactName
    };

    // Define a ListView object
    ListView mContactsList;
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {

                    Data._ID,
                    // The primary display name
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                            Data.DISPLAY_NAME_PRIMARY :
                            Data.DISPLAY_NAME,
                    // The contact's _ID, to construct a content URI
                    Data.CONTACT_ID,
                    // The contact's LOOKUP_KEY, to construct a content URI
                    Data.LOOKUP_KEY
            };
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 2;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 3;

    private String SELECTION = "";

    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static String NAME_SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    Data.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    Data.DISPLAY_NAME + " LIKE ?";


    /*
     * Constructs search criteria from the search string
     * and email MIME type
     */
    private static final String EMAIL_SELECTION = Email.ADDRESS + " LIKE ? ";

    private static final String PHONE_SELECTION = Phone.NUMBER+ " LIKE ? ";

    private static final String EMAIL_MIME = Data.MIMETYPE + " = '" + Email.CONTENT_ITEM_TYPE + "'";

    private static final String PHONE_MIME = Data.MIMETYPE + " = '" + Phone.CONTENT_ITEM_TYPE + "'";

    private static final String SORT_BY = Data.CONTACT_ID + " ASC";

    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = new String[1];

    // Empty public constructor, required by the system
    public SearchContactFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_results,
                container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String name, email, phone;
        String fieldCount="";
        List<String> argsList = new ArrayList<>();
        if(!(name = args.getString("name")).isEmpty()) {
            argsList.add("%" +name+ "%");
            fieldCount = fieldCount + "n";
        }
        if(!(email=args.getString("email")).isEmpty()) {
            argsList.add("%" +email+ "%");
            fieldCount = fieldCount + "e";
        }
        if(!(phone=args.getString("phone")).isEmpty()) {
            argsList.add("%" +phone+ "%");
            fieldCount = fieldCount + "p";
        }

        switch (fieldCount) {
            case "n" :  SELECTION = SELECTION + NAME_SELECTION + " AND " + "(" + PHONE_MIME + " OR " + EMAIL_MIME + ")";
                        break;
            case "e" :  SELECTION = SELECTION + EMAIL_SELECTION + " AND " + EMAIL_MIME;
                        break;
            case "p" :  SELECTION = SELECTION + PHONE_SELECTION + " AND " + PHONE_MIME;
                        break;
            case "ne" : SELECTION = SELECTION + NAME_SELECTION + " AND " + EMAIL_SELECTION + " AND "
                        + "(" + PHONE_MIME + " OR " + EMAIL_MIME + ")";
                        break;
            case "np" : SELECTION = SELECTION + NAME_SELECTION + " AND " + PHONE_SELECTION + " AND "
                        + PHONE_MIME;
                        break;
            case "ep" : SELECTION = SELECTION + EMAIL_SELECTION + " OR " + PHONE_SELECTION + " AND "
                        + "(" + PHONE_MIME + " OR " + EMAIL_MIME + ")";
                        break;
            case "nep" :SELECTION = SELECTION + NAME_SELECTION + " AND " + "(" + "(" + EMAIL_SELECTION + " AND " + EMAIL_MIME + ")" + " OR "
                        + "(" + PHONE_SELECTION + " AND " + PHONE_MIME + ")" + ")";
        }

        mSelectionArgs = argsList.toArray(mSelectionArgs);

        // Starts the query
        return new CursorLoader(getActivity(), Data.CONTENT_URI, PROJECTION, SELECTION, mSelectionArgs, SORT_BY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            MatrixCursor newCursor = new MatrixCursor(PROJECTION);
            String contactId = "";
            do {
                if (!cursor.getString(cursor.getColumnIndex(Data.CONTACT_ID)).equalsIgnoreCase(contactId)) {
                    newCursor.addRow(new Object[]{cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)});
                    contactId=cursor.getString(cursor.getColumnIndex(Data.CONTACT_ID));
                }
            } while (cursor.moveToNext());
            mCursorAdapter.swapCursor(newCursor);
        } else {
            Toast.makeText(getActivity(), "No Matching Contact Found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = ((CursorAdapter)parent.getAdapter()).getCursor();
        cursor.moveToPosition(position);
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);

        Intent intent = new Intent(getActivity(), ContactDisplayActivity.class);
        intent.putExtra("mContactId", mContactId);
        intent.putExtra("mContactKey", mContactKey);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == 101) && (data.getBooleanExtra("Contact_Deleted", false))) {
            Toast.makeText(getActivity(), "Contact Deleted", Toast.LENGTH_LONG).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContactsList = (ListView) getActivity().findViewById(R.id.listView);
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.search_contact_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        mContactsList.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);
        mContactsList.setOnItemClickListener(this);
    }
}


