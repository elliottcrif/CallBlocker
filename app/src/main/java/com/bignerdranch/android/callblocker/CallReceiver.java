package com.bignerdranch.android.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by elliottcrifasi on 11/30/17.
 */

public class CallReceiver extends BroadcastReceiver {
    private final String TAG = "CallReceiver";
    private String number;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received a Broadcast");
        // If, the received action is not a type of "Phone_State", ignore it
        String action = intent.getAction();
        if (action != null) {
            if (action.equals("android.intent.action.PHONE_STATE"))
                // Fetch the number of incoming call
                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Blacklist blacklist = new Blacklist();
            blacklist.setPhoneNumber(number);
            // Check, whether this is a member of "Black listed" phone numbers stored in the database
            if(BlackListActivity.blockList.contains(blacklist)) {
                // If yes, invoke the method
                disconnectPhoneItelephony(context);
            }
            else{
                Log.d(TAG, "Not a Telephone Call");
            }
        }
        }

    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm); // Get the internal ITelephony object
            c = Class.forName(telephonyService.getClass().getName()); // Get its class
            m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
            m.setAccessible(true); // Make it accessible
            m.invoke(telephonyService); // invoke endCall()
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    }

