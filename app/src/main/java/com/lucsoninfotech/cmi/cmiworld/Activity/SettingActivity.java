package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.helper.SessionManager;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

public class SettingActivity extends AppCompatActivity {

    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        LinearLayout logout = findViewById(R.id.logout);
        db = new SQLiteHandler(getApplicationContext());

        LinearLayout change_profile = findViewById(R.id.change_profile);
        LinearLayout payment_detail = findViewById(R.id.payment_detail);
        LinearLayout faq = findViewById(R.id.faq);
        LinearLayout rate_us = findViewById(R.id.rate_us);


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
        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isOnline(getApplicationContext())) {
                    Intent i = new Intent(SettingActivity.this, Rateus.class);
                    startActivity(i);
                } else {
                    Toast.makeText(SettingActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
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
