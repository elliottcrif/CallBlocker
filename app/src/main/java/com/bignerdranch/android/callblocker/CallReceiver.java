package com.bignerdranch.android.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;


/**
 * Created by elliottcrifasi on 11/30/17.
 */

public class CallReceiver extends BroadcastReceiver {
    private final String TAG = "CallReceiver";
    private String number;
    @Override
    public void onReceive(Context context, Intent intent) {
        // If, the received action is not a type of "Phone_State", ignore it
        if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
            // Fetch the number of incoming call
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Blacklist blacklist = new Blacklist();
            blacklist.setPhoneNumber(number);
        // Check, whether this is a member of "Black listed" phone numbers stored in the database
        if(BlackListActivity.blockList.contains(blacklist)) {
            // If yes, invoke the method
            disconnectPhoneItelephony(context);
            return;
        }
        else{
            Log.d(TAG, "Not a Telephone Call");
            }
        }
    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context)
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
    }
    }

