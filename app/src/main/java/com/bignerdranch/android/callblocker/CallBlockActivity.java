package com.bignerdranch.android.callblocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CallBlockActivity extends AppCompatActivity {

    private Button showListButton;
    private Button addToListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_block);
        addToListButton = (Button) findViewById(R.id.addToList);
        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddBlackActivity.class));
            }
        });
        showListButton = (Button) findViewById(R.id.showList);
        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BlackListActivity.class));
            }
        });
    }

}
