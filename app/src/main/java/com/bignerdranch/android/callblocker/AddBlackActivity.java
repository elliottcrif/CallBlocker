package com.bignerdranch.android.callblocker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.realm.Realm;

public class AddBlackActivity extends AppCompatActivity {

    private static final String TAG = "AddBlackActivity";
    private Button addToBlackListBtn;
    private TextView phone_number_text_view;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_black2);
        phone_number_text_view = (TextView) findViewById(R.id.phone_number_text_view);
        phone_number_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone_number_text_view.setText("");
            }
        });
        phone_number_text_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addToBlackListBtn = (Button) findViewById(R.id.addBlackbutton);
        addToBlackListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToRealm();
                showDialog();
            }
        });


    }

    private void addToRealm() {
        try { // I could use try-with-resources here
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Blacklist blacklist = new Blacklist();
                    blacklist.setPhoneNumber(phone_number_text_view.getText().toString());
                    realm.insert(blacklist);
                }

            });
        } finally {
            if(realm != null) {
                realm.close();
            }
        }
    }
    private void showDialog()
    {
        // After submission, Dialog opens up with "Success" message. So, build the AlartBox first
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the appropriate message into it.
        alertDialogBuilder.setMessage("Phone Number added to blacklist successfully");

        // Add a positive button and it's action. In our case action would be, just hide the dialog box ,
        // and erase the user inputs.
        alertDialogBuilder.setPositiveButton("Add Another",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        reset();
                    }
                });

        // Add a negative button and it's action. In our case, close the current screen
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        // Now, create the Dialog and show it.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    // Clear the entered text
    private void reset()
    {
        phone_number_text_view.setText(R.string.enter_number);
    }

}
