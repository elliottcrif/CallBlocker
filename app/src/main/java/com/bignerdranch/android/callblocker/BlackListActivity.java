package com.bignerdranch.android.callblocker;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;

public class BlackListActivity extends AppCompatActivity {

    private ListView blackList;

    // object to query database
    private Realm blackListDb;
    // It holds the list of Blacklist objects fetched from Database
    public static List<Blacklist> blockList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        blackList = (ListView) findViewById(R.id.blackList);
        blackListDb = Realm.getDefaultInstance();
        blockList = blackListDb.where(Blacklist.class).findAll();
        //Now, link the  CustomArrayAdapter with the ListView
        blackList.setAdapter(new BlackListAdapter(this, R.layout.black_list_item, blockList));
    }
    public class BlackListAdapter extends ArrayAdapter<String> {

        private LayoutInflater inflater;

        // This would hold the database objects i.e. Blacklist
        private List<Blacklist> records;

        @SuppressWarnings("unchecked")
        public BlackListAdapter(Context context, int resource, @SuppressWarnings("rawtypes") List objects) {
            super(context, resource, objects);

            this.records = objects;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Reuse the view to make the scroll effect smooth
            if(convertView == null)
                convertView = inflater.inflate(R.layout.black_list_item, parent, false);

            // Fetch phone number from the database object
            final Blacklist phoneNumber =  records.get(position);

            // Set to screen component to display results
            ((TextView)convertView.findViewById(R.id.phone_number_text_view)).setText(phoneNumber.phoneNumber);
            return convertView;
        }

    }
}
