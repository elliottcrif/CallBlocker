package com.bignerdranch.android.callblocker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.realm.Realm;

public class AddBlackActivity extends AppCompatActivity {

    private Button addToBlackListBtn;
    private TextView phone_number_text_view;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_black2);
        realm = Realm.getDefaultInstance();
        phone_number_text_view = (TextView) findViewById(R.id.phone_number_text_view);
        addToBlackListBtn = (Button) findViewById(R.id.addBlackbutton);
        addToBlackListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Blacklist blacklist = new Blacklist();
                blacklist.setId(realm.where(Blacklist.class).findAll().size()+1);
                blacklist.setPhoneNumber(phone_number_text_view.getText().toString());
                addToRealm(blacklist);
            }
        });


    }

    private void addToRealm(Blacklist blacklist) {
        // open realm
        realm.beginTransaction();
        // insert into realm
        realm.insert(blacklist);
        // commit the changes
        realm.commitTransaction();
    }

}
