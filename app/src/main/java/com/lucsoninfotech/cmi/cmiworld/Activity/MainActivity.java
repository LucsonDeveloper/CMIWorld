package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.lucsoninfotech.cmi.cmiworld.Fragment.HomeFragment;
import com.lucsoninfotech.cmi.cmiworld.Fragment.ProfileFragment;
import com.lucsoninfotech.cmi.cmiworld.Fragment.SearchFragment;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.helper.SessionManager;

public class MainActivity extends AppCompatActivity {
    private final int[] mTabsIcons = {
            R.drawable.home,
            R.drawable.search_icon,
            R.drawable.menubar,
    };
    int tabIconColor;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup the viewPager
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        ImageView donate = toolbar.findViewById(R.id.donate);
        ImageView create_project = toolbar.findViewById(R.id.create_project);
        setTitle("");
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CMIDonate.class);
                startActivity(i);
            }
        });

        create_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ApplyforSEMActivity.class);
                startActivity(i);

            }
        });
        ViewPager viewPager = findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        if (viewPager != null)
            viewPager.setAdapter(pagerAdapter);

        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(viewPager);

            //  setupTabIcons();
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(pagerAdapter.getTabView(i));
            }
            mTabLayout.getTabAt(0).getCustomView().setSelected(true);
        }
    }

/*
    private void setupTabIcons() {
        mTabLayout.getTabAt(0).setIcon(mTabsIcons[0]);
        mTabLayout.getTabAt(1).setIcon(mTabsIcons[1]);
        mTabLayout.getTabAt(2).setIcon(mTabsIcons[2]);
        mTabLayout.getTabAt(3).setIcon(mTabsIcons[3]);

        mTabLayout.getTabAt(0).getIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        mTabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
        mTabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
        mTabLayout.getTabAt(3).getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);


        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
*/

    private class MyPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 3;


        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);


            ImageView icon = view.findViewById(R.id.icon);
            icon.setImageResource(mTabsIcons[position]);
            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return new HomeFragment();
                case 1:
                    return new SearchFragment();
                case 2:
                    return new ProfileFragment();
                /*  return new BackedFragment();*/
         /*       case 3:
                    ProfileFragment profileFragment = new ProfileFragment();
                    return profileFragment;
*/
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

}