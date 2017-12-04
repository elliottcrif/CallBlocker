package com.bignerdranch.android.callblocker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

/**
 * Created by elliottcrifasi on 12/3/17.
 */

public class AddNumberDialog extends DialogFragment {

    private Realm realm;
    private TextView phone_number_text_view;
    // Use this instance of the interface to deliver action events
    AddNumberDialogListener mListener;

    public interface AddNumberDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.activity_add_black2)
                .setMessage("Add a number to the BlackList").setPositiveButton(R.string.add_to_blacklist, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(AddNumberDialog.this);
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(AddNumberDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddNumberDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
