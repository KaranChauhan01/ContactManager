package com.doers.contactmanager;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Display Contact Search Page
 * Created by Karna on 9/17/2015.
 */
public class SearchContactActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);

        SearchFragment searchFragment = new SearchFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contactList_fragment, searchFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void showSearchFragment(View view) {
        EditText name = (EditText) findViewById(R.id.search_name);
        EditText email = (EditText) findViewById(R.id.search_email);
        EditText phone = (EditText) findViewById(R.id.search_phone);
        if (name.getText().toString().isEmpty() && email.getText().toString().isEmpty() && phone.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Details to be searched", Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("name", name.getText().toString());
            bundle.putString("email", email.getText().toString());
            bundle.putString("phone", phone.getText().toString());
            SearchContactFragment searchFrag = new SearchContactFragment();
            searchFrag.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.contactList_fragment, searchFrag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
