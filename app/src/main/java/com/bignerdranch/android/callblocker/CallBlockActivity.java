package com.bignerdranch.android.callblocker;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;
import static io.realm.RealmObject.deleteFromRealm;

public class CallBlockActivity extends AppCompatActivity
        implements AddNumberDialog.AddNumberDialogListener,
                    EditNumberDialog.EditNumberDialogListener,
                    NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavView;
    private String[] menuTitles;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ListView blackList;
    private int currClicked;
    private int currCount;
    // object to query database
    private Realm blackListDb;
    // It holds the list of Blacklist objects fetched from Database
    public static RealmResults<Blacklist> blockList;
    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    public final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 11;
    private static final String TAG = "CallBlockActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        mTitle = mDrawerTitle = getTitle();
        menuTitles = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.navigation);
        mNavView.setNavigationItemSelectedListener(this);
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
        blackList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                currClicked = i;
                showEditDialog();
                return false;
            }
        });
        ListAdapter blackListAdapter = blackList.getAdapter();
        currCount = blackListAdapter.getCount();
        Log.d(TAG, currCount+"");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) + ContextCompat
                .checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS },
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
            getContact();
    }
        if (currCount == 0) {
            showEmptyDialog();
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
                BlackListAdapter blackListAdapter = (BlackListAdapter) blackList.getAdapter();
                int count = blackListAdapter.getCount();
                Blacklist blacklist = null;
                if (count > currClicked) { // check if in range
                    blacklist = blackListAdapter.getItem(currClicked);
                }
                if (blacklist != null) { // if not null try to delete
                    Log.d(TAG, blacklist.getPhoneNumber());
                    deleteBlackList(blacklist.getPhoneNumber());
                }
                return true;
            case R.id.add_menu_item:
                DialogFragment newFragment = new AddNumberDialog();
                newFragment.show(getSupportFragmentManager(), "number");
                return true;
            case R.id.open_menu_item:
                mDrawerLayout.openDrawer(mNavView);
                return true;
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
            showDialog();
        } else {
            // TODO CREATE DIALOG THAT ASKS TO TRY AGAIN
            Toast.makeText(this, "Phone Number is Invalid", Toast.LENGTH_LONG).show();
            DialogFragment newFragment = new AddNumberDialog();
            newFragment.show(getSupportFragmentManager(), "number");
        }

    }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    /**
     * Edit dialog
     * @param dialog
     */
    @Override
    public void onEditDialogPositiveClick(DialogFragment dialog) {
        // Dialog
        Dialog editDialog = dialog.getDialog();
        // grab the edit text
        EditText phone_number_text_view = editDialog.findViewById(R.id.phone_number_text_view);
        // grab the new phone number
        String newNumber = phone_number_text_view.getText().toString();

        // grab the old number
        BlackListAdapter blackListAdapter =  (BlackListAdapter) blackList.getAdapter();
        Blacklist blacklist = blackListAdapter.getItem(currClicked);
        String currNumber = "";
        if (blacklist != null) {
            currNumber = blacklist.getPhoneNumber();
        }
        // if number checks out then add to list
        if (newNumber.length() == 10 && !currNumber.equals("")) {
            editBlackList(currNumber, newNumber);
        } else {
            // TODO CREATE DIALOG THAT ASKS TO TRY AGAIN
            Toast.makeText(this, "Phone Number is Invalid", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Edit dialog
     * @param dialog
     */
    @Override
    public void onEditDialogNegativeClick(DialogFragment dialog) {

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
                blacklist.setId(hashCode()+"");
                blacklist.setPhoneNumber(phoneNumber);
                realm.insert(blacklist);
                currCount++;
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
                Blacklist blacklist = realm.where(Blacklist.class).equalTo("phoneNumber", phoneNumber)
                        .findFirst();
                if (blacklist != null) {
                    realm.where(Blacklist.class).equalTo("phoneNumber", phoneNumber)
                            .findFirst()
                            .deleteFromRealm();
                }
                currCount--;
            }
        });
    }
    void editBlackList(final String currNumber, final String newNumber) {
        blackListDb.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Blacklist blacklist = realm.where(Blacklist.class)
                        .equalTo("phoneNumber", currNumber)
                        .findFirst();
                if (blacklist != null) {
                    blacklist.setPhoneNumber(newNumber);
                }
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
                        getContact();
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
                        DialogFragment newFragment = new AddNumberDialog();
                        newFragment.show(getSupportFragmentManager(), "number");
                    }
                });

        // Add a negative button and it's action. In our case, close the current screen
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // Now, create the Dialog and show it.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void showEmptyDialog()
    {
        // After submission, Dialog opens up with "Success" message. So, build the AlartBox first
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the appropriate message into it.
        alertDialogBuilder.setMessage("Your blackList is empty. Add a number?");

        // Add a positive button and it's action. In our case action would be, just hide the dialog box ,
        // and erase the user inputs.
        alertDialogBuilder.setPositiveButton("Add Number",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DialogFragment newFragment = new AddNumberDialog();
                        newFragment.show(getSupportFragmentManager(), "number");
                    }
                });

        // Add a negative button and it's action. In our case, close the current screen
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // Now, create the Dialog and show it.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void showEditDialog()
    {
        // After submission, Dialog opens up with "Success" message. So, build the AlertBox first
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the appropriate message into it.
        alertDialogBuilder.setMessage("Do you want to edit this number?");

        // Add a positive button and it's action. In our case action would be, just hide the dialog box ,
        // and erase the user inputs.
        alertDialogBuilder.setPositiveButton("Edit Number",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DialogFragment newFragment = new EditNumberDialog();
                        newFragment.show(getSupportFragmentManager(), "number");
                    }
                });

        // Add a negative button and it's action. In our case, close the current screen
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // Now, create the Dialog and show it.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);
        }
        switch (item.getItemId()) {
            case R.id.blockAllCallsToggle:
                CallBlockPreferences.setStoredBlockType(this, "all");
                Toast.makeText(this, "All call will be blocked", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "all");
                return true;
            case R.id.blockBlackListToggle:
                CallBlockPreferences.setStoredBlockType(this, "blacklist");
                Toast.makeText(this, "Blacklist will be blocked", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "blacklist");
                return true;
            case R.id.blockCancelToggle:
                CallBlockPreferences.setStoredBlockType(this, "cancel");
                Toast.makeText(this, "No calls will be blocked", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "cancel");
                return true;
            case R.id.blockUnsavedToggle:
                CallBlockPreferences.setStoredBlockType(this, "unsaved");
                getContact();
                Toast.makeText(this, "Unsaved Contacts will be blocked", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "unsaved");
                return true;
            default:
                mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }
    public void getContact() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            List<String> contacts = new ArrayList<>();
            // Get the ContentResolver
            ContentResolver cr = getContentResolver();
            // Get the Cursor of all the contacts
            Cursor cursor = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{PHONE_NUMBER,},
                    null,
                    null,
                    null
            );

            // Move the cursor to first. Also check whether the cursor is empty or not.
            if (cursor.moveToFirst()) {
                // Iterate through the cursor
                do {
                    // Get the contacts name
                    String number = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
                    number = number.replaceAll("[()\\s-]+", "");
                    contacts.add(number);
                } while (cursor.moveToNext());
            }
            // Close the cursor
            cursor.close();
            CallBlockPreferences.setContacts(contacts);
            Log.d(TAG, contacts.toString());
        }
    }

    }

