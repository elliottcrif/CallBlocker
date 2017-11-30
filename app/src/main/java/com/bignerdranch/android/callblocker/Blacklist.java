package com.bignerdranch.android.callblocker;

import android.support.annotation.RequiresPermission;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by elliottcrifasi on 11/30/17.
 */

public class Blacklist extends RealmObject {


    public long id;
    @Required
    @PrimaryKey
    String phoneNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}