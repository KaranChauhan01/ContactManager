package com.doers.contactmanager;

import android.app.Fragment;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * Edit Contact Fragment
 * Created by Karna on 9/20/2015.
 */
public class ContactEditFragment extends Fragment {

    public ContactEditFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_details, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView msgView = (TextView) getActivity().findViewById(R.id.deails_msg);
        msgView.setText("Edit Details");
        LinearLayout nameLayout = (LinearLayout) getActivity().findViewById(R.id.nameLayout);
        LinearLayout emailLayout = (LinearLayout) getActivity().findViewById(R.id.emailLayout);
        LinearLayout phoneLayout = (LinearLayout) getActivity().findViewById(R.id.phoneLayout);
        Button saveButton = (Button) getActivity().findViewById(R.id.saveButton);
        saveButton.setVisibility(View.VISIBLE);
        Bundle args = getArguments();
        Resources res = getResources();
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        float fontSize = res.getDimension(R.dimen.text_size);
        int padding_left = res.getDimensionPixelSize(R.dimen.padding_left);
        int padding_top_bottom = res.getDimensionPixelSize(R.dimen.padding_top_bottom);
        int zero = res.getDimensionPixelSize(R.dimen.zero);
        params.setMargins(res.getDimensionPixelSize(R.dimen.margin), zero, res.getDimensionPixelSize(R.dimen.margin), zero);
        EditText nameText = new EditText(getActivity());
        nameText.setText(args.getString("name"));
        nameText.setPadding(padding_left, padding_top_bottom, zero, padding_top_bottom);
        nameText.setTextSize(fontSize);
        nameText.setLayoutParams(params);
        nameLayout.addView(nameText);
        for(String email : args.getStringArrayList("email")) {
            EditText emailText = new EditText(getActivity());
            emailText.setText(email);
            emailText.setPadding(padding_left, padding_top_bottom, zero, padding_top_bottom);
            emailText.setTextSize(fontSize);
            emailText.setLayoutParams(params);
            emailLayout.addView(emailText);
        }
        for(String phone : args.getStringArrayList("phone")) {
            EditText phoneText = new EditText(getActivity());
            phoneText.setText(phone);
            phoneText.setPadding(padding_left, padding_top_bottom, zero, padding_top_bottom);
            phoneText.setTextSize(fontSize);
            phoneText.setLayoutParams(params);
            phoneLayout.addView(phoneText);
        }
    }
}
