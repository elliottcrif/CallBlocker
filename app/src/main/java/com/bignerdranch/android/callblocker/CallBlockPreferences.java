package com.bignerdranch.android.callblocker;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by elliottcrifasi on 12/1/17.
 */

public class CallBlockPreferences {

    private static final String PREF_CALL_TYPE = "searchQuery";

    public static String getStoredBlockType(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CALL_TYPE, null);
    }

    public static void setStoredBlockType(Context context, String blockType) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_CALL_TYPE, blockType)
                .apply();
    }

}
