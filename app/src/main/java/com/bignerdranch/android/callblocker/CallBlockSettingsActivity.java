package com.bignerdranch.android.callblocker;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import static android.content.ContentValues.TAG;

/**
 * Created by elliottcrifasi on 12/2/17.
 */

public class CallBlockSettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
            case R.id.blockCancelToggle:
                if (checked)
                    CallBlockPreferences.setStoredBlockType(this, "cancel");
                Log.d(TAG, "blacklist will be blocked");
                break;
        }
    }
}
