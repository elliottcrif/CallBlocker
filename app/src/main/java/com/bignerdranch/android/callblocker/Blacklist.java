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
    String phoneNumber;

    String phoneNumberPlus;
    String phoneNumberPlusCountry;

    @PrimaryKey
    String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
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