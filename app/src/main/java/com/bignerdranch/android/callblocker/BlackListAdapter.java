package com.bignerdranch.android.callblocker;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;

/**
 * Created by elliottcrifasi on 12/2/17.
 */

public class BlackListAdapter extends RealmBaseAdapter<Blacklist> implements ListAdapter {

    private LayoutInflater inflater;
    private CallBlockActivity activity;


    private static class ViewHolder {
        TextView phoneNumber;
    }
    BlackListAdapter(CallBlockActivity activity, OrderedRealmCollection<Blacklist> data) {
        super(data);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.black_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_number_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            Blacklist blacklist = adapterData.get(position);
            viewHolder.phoneNumber.setText(blacklist.getPhoneNumber());

        }

        return convertView;
    }
}
