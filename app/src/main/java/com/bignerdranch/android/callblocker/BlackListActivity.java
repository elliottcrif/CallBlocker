package com.bignerdranch.android.callblocker;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;

public class BlackListActivity extends AppCompatActivity {

    private static final String TAG = "BlackListActivity" ;
    private ListView blackList;
    private int currClicked;

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
        blackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currClicked = i;
                view.setSelected(true);
                Log.d(TAG, Integer.toString(i));
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_black_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_menu_item:
                Toast.makeText(getBaseContext(), "Clicked", Toast.LENGTH_LONG).show();
                BlackListAdapter blackListAdapter = (BlackListAdapter) blackList.getAdapter();
                Blacklist blacklist = null;
                if (blackListAdapter.records.size() != 0){
                   blacklist = blackListAdapter.records.get(currClicked);
                }
                if (blacklist != null) {
                    Log.d(TAG, blacklist.getPhoneNumber());
                    deleteFromRealm(blacklist);
                    blackListAdapter.notifyDataSetChanged();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void deleteFromRealm(final Blacklist blacklist) {
        try { // I could use try-with-resources here
            blackListDb = Realm.getDefaultInstance();
            blackListDb.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Blacklist deletedNumber = realm.where(Blacklist.class)
                            .equalTo("phoneNumber", blacklist.getPhoneNumber())
                            .findFirst();
                    if (deletedNumber != null) {
                        deletedNumber.deleteFromRealm();
                    }
                }

            });
        } finally {
            if(blackListDb != null) {
                blackListDb.close();
            }
        }
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
