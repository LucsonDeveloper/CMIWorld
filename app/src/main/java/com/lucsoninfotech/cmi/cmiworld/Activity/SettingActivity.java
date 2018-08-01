package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.helper.SessionManager;

public class SettingActivity extends AppCompatActivity {

    private LinearLayout logout;
    private LinearLayout change_profile;
    private LinearLayout payment_detail;
    private LinearLayout faq;
    private LinearLayout rate_us;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        logout = findViewById(R.id.logout);
        db = new SQLiteHandler(getApplicationContext());

        change_profile = findViewById(R.id.change_profile);
        payment_detail = findViewById(R.id.payment_detail);
        faq = findViewById(R.id.faq);
        rate_us = findViewById(R.id.rate_us);


        session = new SessionManager(getApplicationContext());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                session.logoutUser();
                db.deleteUsers();
                finishAffinity();
                finish();
            }
        });

        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SettingActivity.this, ProfileActivity.class);
                startActivity(i);

            }
        });

        payment_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(SettingActivity.this, PaymentDetailActivity.class);
                startActivity(i);

            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SettingActivity.this, FAQActivity.class);
                startActivity(i);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
