package com.doers.contactmanager;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * Contact Details Fragment
 * Created by Karna on 9/19/2015.
 */
public class ContactDetailsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mContactUri;
    private LinearLayout nameLayout;
    private LinearLayout emailLayout;
    private LinearLayout phoneLayout;

    public ContactDetailsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_details, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection =
                {
                        ContactsContract.Contacts.Entity.DISPLAY_NAME_PRIMARY,
                        ContactsContract.Contacts.Entity.DATA1,
                        ContactsContract.Contacts.Entity.MIMETYPE
                };

        String selection = ContactsContract.Contacts.Entity.CONTACT_ID + " = ? AND " + ContactsContract.Contacts.Entity.MIMETYPE+ " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'"
                + " OR " + ContactsContract.Contacts.Entity.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";
        String[] selectionArgs = {Long.toString(args.getLong("mContactId"))};

        return new CursorLoader(getActivity(), mContactUri, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            Resources res = getResources();
            float fontSize = res.getDimension(R.dimen.text_size);
            int padding_left = res.getDimensionPixelSize(R.dimen.padding_left);
            int padding_top_bottom = res.getDimensionPixelSize(R.dimen.padding_top_bottom);
            int zero = res.getDimensionPixelSize(R.dimen.zero);
            String email = "";
            String phone = "";
            int e = 0;
            int p = 0;

            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
            );
            params.setMargins(res.getDimensionPixelSize(R.dimen.margin), zero, res.getDimensionPixelSize(R.dimen.margin), zero);
            TextView nameText;
            if(nameLayout.getChildCount() > 1) {
                nameText = (TextView) nameLayout.getChildAt(1);
                nameLayout.removeView(nameText);
            }
            nameText = new TextView(getActivity());
            nameText.setText(cursor.getString(0));
            nameText.setPadding(padding_left, padding_top_bottom, zero, padding_top_bottom);
            nameText.setLayoutParams(params);
            nameText.setTextSize(fontSize);
            nameLayout.addView(nameText);
            do {
                if(cursor.getString(2).equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    if(!email.equals(cursor.getString(1))) {
                        TextView emailText;
                        if(emailLayout.getChildCount() > 1) {
                            emailText = (TextView) emailLayout.getChildAt(++e);
                            emailLayout.removeView(emailText);
                        }
                        emailText = new TextView(getActivity());
                        emailText.setText(cursor.getString(1));
                        emailText.setPadding(padding_left, padding_top_bottom, zero, padding_top_bottom);
                        emailText.setTextSize(fontSize);
                        emailText.setLayoutParams(params);
                        emailLayout.addView(emailText);
                        email = cursor.getString(1);
                    }
                } else {
                    if(!phone.equals(cursor.getString(1))) {
                        TextView phoneText;
                        if(phoneLayout.getChildCount() > 1) {
                            phoneText = (TextView) phoneLayout.getChildAt(++p);
                            phoneLayout.removeView(phoneText);
                        }
                        phoneText = new TextView(getActivity());
                        phoneText.setText(cursor.getString(1));
                        phoneText.setPadding(padding_left, padding_top_bottom, zero, padding_top_bottom);
                        phoneText.setTextSize(fontSize);
                        phoneText.setLayoutParams(params);
                        phoneLayout.addView(phoneText);
                        phone = cursor.getString(1);
                    }
                }
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        mContactUri = ContactsContract.Contacts.getLookupUri(args.getLong("mContactId"),
                args.getString("mContactKey"));
        mContactUri = Uri.withAppendedPath(mContactUri, ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);
        nameLayout = (LinearLayout)getActivity().findViewById(R.id.nameLayout);
        emailLayout = (LinearLayout)getActivity().findViewById(R.id.emailLayout);
        phoneLayout = (LinearLayout)getActivity().findViewById(R.id.phoneLayout);
        getLoaderManager().initLoader(0, args, this);
    }
}
