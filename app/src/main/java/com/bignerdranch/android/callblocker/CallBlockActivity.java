package com.bignerdranch.android.callblocker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class CallBlockActivity extends AppCompatActivity {

    private Button showListButton;
    private Button addToListButton;
    public final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 11;
    public final static int MY_PERMISSIONS_REQUEST_CALL_PHONE_STATE = 12;
    private static final String TAG = "CallBlockActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_block);
        addToListButton = (Button) findViewById(R.id.addToList);
        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddBlackActivity.class));
            }
        });
        showListButton = (Button) findViewById(R.id.showList);
        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BlackListActivity.class));
            }
        });
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) + ContextCompat
                .checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE },
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
    }
}
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }
        }
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.blockAllCallsToggle:
                if (checked)
                    CallBlockPreferences.setStoredBlockType(this, "all");
                    Log.d(TAG, "all");
                    break;
            case R.id.blockBlackListToggle:
                if (checked)
                    CallBlockPreferences.setStoredBlockType(this, "blacklist");
                    Log.d(TAG, "blacklist will be blocked");
                    break;
        }
    }
}
