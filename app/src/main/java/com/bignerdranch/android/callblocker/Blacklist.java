package com.bignerdranch.android.callblocker;

import android.support.annotation.RequiresPermission;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by elliottcrifasi on 11/30/17.
 */

public class Blacklist extends RealmObject {


    @Required
    @PrimaryKey
    String phoneNumber;
    String phoneNumberPlus;
    String phoneNumberPlusCountry;


    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getPhoneNumberPlusCountryCode() {
        return "+1"+phoneNumber;
    }
    public String getPhoneNumberPlus() {
        return "+"+phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.phoneNumberPlus = getPhoneNumberPlus();
        this.phoneNumberPlusCountry = getPhoneNumberPlusCountryCode();
    }

    @Override
    public boolean equals(Object obj) {
        obj = (Blacklist) obj;
        return this.getPhoneNumber().equals(((Blacklist) obj).getPhoneNumber());
    }
}