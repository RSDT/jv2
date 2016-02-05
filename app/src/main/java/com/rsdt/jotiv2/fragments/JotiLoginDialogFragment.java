package com.rsdt.jotiv2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.rsdt.jotial.JotiApp;
import com.rsdt.jotiv2.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 15-1-2016
 * Description...
 */
public class JotiLoginDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_login, null))
                // Add action buttons
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        JotiApp.Auth.auth(((EditText) getDialog().findViewById(R.id.username)).getText().toString(), ((EditText) getDialog().findViewById(R.id.password)).getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JotiLoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Try to get the existing username.
         * */
        String username = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).getString("pref_account_username", null);

        /**
         * Check if the username is not null or empty.
         * */
        if(username != null && !username.isEmpty())
        {
            ((EditText)getDialog().findViewById(R.id.username)).setText(username);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JotiApp.Auth.setAuthDialogActive(false);
    }
}
