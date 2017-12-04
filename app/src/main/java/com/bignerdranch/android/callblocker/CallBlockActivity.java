package com.bignerdranch.android.callblocker;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;
import static io.realm.RealmObject.deleteFromRealm;

public class CallBlockActivity extends AppCompatActivity implements AddNumberDialog.AddNumberDialogListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] menuTitles;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ListView blackList;
    private int currClicked;

    // object to query database
    private Realm blackListDb;
    // It holds the list of Blacklist objects fetched from Database
    public static RealmResults<Blacklist> blockList;
    public final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 11;
    private static final String TAG = "CallBlockActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        mTitle = mDrawerTitle = getTitle();
        menuTitles = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, menuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.open_drawer,  /* "open drawer" description for accessibility */
                R.string.close_drawer  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        blackList = (ListView) findViewById(R.id.blackList);
        blackListDb = Realm.getDefaultInstance();
        blockList = blackListDb.where(Blacklist.class).findAll();
        //Now, link the  CustomArrayAdapter with the ListView
        blackList.setAdapter(new BlackListAdapter(this, blockList));
        blackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currClicked = i;
                view.setSelected(true);
                Log.d(TAG, Integer.toString(i));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_nav_drawer, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete_menu_item:
                Toast.makeText(getBaseContext(), "Clicked", Toast.LENGTH_LONG).show();
                BlackListAdapter blackListAdapter = (BlackListAdapter) blackList.getAdapter();
                Blacklist blacklist = blackListAdapter.getItem(currClicked);
                if (blacklist != null) {
                    Log.d(TAG, blacklist.getPhoneNumber());
                    deleteBlackList(blacklist.getPhoneNumber());

                }
                return true;
            case R.id.add_menu_item:
                DialogFragment newFragment = new AddNumberDialog();
                newFragment.show(getSupportFragmentManager(), "number");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Dialog addDialog = dialog.getDialog();
        // grab the edit text
        EditText phone_number_text_view = addDialog.findViewById(R.id.phone_number_text_view);
        // grab phone number
        String phoneNumber = phone_number_text_view.getText().toString();
        // if number checks out then add to list
        // TODO CHECK IF NUMBER IS IN REALM
        if (phoneNumber.length() == 10) {
            addToRealm(phoneNumber);
        } else {
            // TODO CREATE DIALOG THAT ASKS TO TRY AGAIN
            Toast.makeText(this, "Phone Number is Invalid", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           selectItem(position);
           onRadioButtonClicked(view);
        }
    }

    private void selectItem(int position) {
        RadioButton radioButton = (RadioButton) mDrawerList.getAdapter().getItem(position);
        Log.d(TAG, radioButton.getText().toString());
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * Adds a phoneNumber to the database
     * @param phoneNumber
     */
    private void addToRealm(final String phoneNumber) {
        blackListDb.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Blacklist blacklist = new Blacklist();
                blacklist.setPhoneNumber(phoneNumber);
                realm.insert(blacklist);
            }
        });
    }

    /**
     * Deletes a number from the dataBase using Realm
     * @param phoneNumber
     */
    private void deleteBlackList(final String phoneNumber) {
        blackListDb.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Blacklist.class).equalTo("phoneNumber", phoneNumber)
                        .findFirst()
                        .deleteFromRealm();
            }
        });
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
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /**
     * This Handles the event when the user toggles the radio buttons
     * @param view
     */
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
