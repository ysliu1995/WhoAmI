package com.ncbci.whoami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ncbci.whoami.dialog.ThresholdDialog;
import com.ncbci.whoami.fragment.Bluetooth;
import com.ncbci.whoami.fragment.Home;
import com.ncbci.whoami.fragment.Stream;
import com.ncbci.whoami.service.FCMService;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";
    private BottomNavigationView mBv;
    private ViewPager mVp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.inflateMenu(R.menu.nav_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new ThresholdDialog(MainActivity.this).show();
                return true;
            }
        });

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(MainActivity.this, FCMService.class));
    }

    private void initView() {
        mBv = findViewById(R.id.bv);
        mVp = findViewById(R.id.vp);
        mBv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        mVp.setCurrentItem(0);
                        return true;

                    case R.id.navigation_stream:
                        mVp.setCurrentItem(1);
                        return true;

                    case R.id.navigation_bluetooth:
                        mVp.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });

        setupViewPager(mVp);
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBv.getMenu().getItem(position).setChecked(true);
                mVp.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        BottomAdapter adapter = new BottomAdapter(getSupportFragmentManager());
        adapter.addFragment(new Home());
        adapter.addFragment(new Stream());
        adapter.addFragment(new Bluetooth());
        viewPager.setAdapter(adapter);
    }
}
