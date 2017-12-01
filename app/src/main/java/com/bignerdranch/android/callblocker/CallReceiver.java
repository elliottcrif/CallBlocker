package com.bignerdranch.android.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import com.android.internal.telephony.ITelephony;


/**
 * Created by elliottcrifasi on 11/30/17.
 */

public class CallReceiver extends BroadcastReceiver {
    private final String TAG = "CallReceiver";
    private String number;
    public static List<Blacklist> blockList;
    private Realm realm;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received a Broadcast");
        // If, the received action is not a type of "Phone_State", ignore it
        String action = intent.getAction();
        realm = Realm.getDefaultInstance();
        if (action != null) {
            if (action.equals("android.intent.action.PHONE_STATE"))
                // Fetch the number of incoming call
                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d(TAG, number);
                if (CallBlockPreferences.getStoredBlockType(context).equals("blacklist")) {
                    // place holder for curr number
                    String currNumber = null;
                    currNumber = searchRealm();
                    // Check, whether this is a member of "Black listed" phone numbers stored in the database
                    if (currNumber != null) {
                        // If yes, invoke the method
                        disconnectPhone(context);
                    } else {
                        Log.d(TAG, "Not a Telephone Call");
                    }
                } else if (CallBlockPreferences.getStoredBlockType(context).equals("all")) {
                        disconnectPhone(context);
                }
        }
        }

    private String searchRealm() {
        String currNumber = null;
        RealmResults<Blacklist> results = realm.where(Blacklist.class)
                .equalTo("phoneNumber", number)
                .or()
                .equalTo("phoneNumberPlus", number)
                .or()
                .equalTo("phoneNumberPlusCountry", number)
                .findAll();
        blockList = realm.copyFromRealm(results);
        for (Blacklist blackList : blockList) {
            String blackListNumber = blackList.getPhoneNumber();
            String blackListPlusCountry = blackList.getPhoneNumberPlusCountryCode();
            String blackListPlus = blackList.getPhoneNumberPlus();
            if (blackListNumber.equals(number)
                    || blackListPlusCountry.equals(number)
                    || blackListPlus.equals(number)) {
                currNumber = blackList.getPhoneNumber();
            }
        }
        realm.close();
        return currNumber;
    }

    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhone(Context context)
    {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try
        {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Call Blocked Successfully");
    }
    }

