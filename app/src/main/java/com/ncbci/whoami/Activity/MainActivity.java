package com.ncbci.whoami.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.ncbci.whoami.Adapter.BottomAdapter;
import com.ncbci.whoami.R;
import com.ncbci.whoami.dialog.ClassRoomDialog;
import com.ncbci.whoami.fragment.Bluetooth;
import com.ncbci.whoami.fragment.Home;
import com.ncbci.whoami.fragment.Stream;
import com.ncbci.whoami.service.FCMService;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";
    private BottomNavigationView mBv;
    private ViewPager mVp;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
//        toolbar.inflateMenu(R.menu.nav_menu);
        getSupportActionBar().setTitle("");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new ClassRoomDialog(MainActivity.this).show();
                return true;
            }
        });
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigation_view = findViewById(R.id.navigation_view);
        // 將drawerLayout和toolbar整合，會出現「三」按鈕
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawer(GravityCompat.START);
                int id = menuItem.getItemId();
                if (id == R.id.action_ble_setting) {
                    startActivity(new Intent(MainActivity.this, DeviceSettingActivity.class));
                    return true;
                }
//                else if (id == R.id.action_threshold) {
//                    startActivity(new Intent(MainActivity.this, AirControllActivity.class));
//                    return true;
//                }
                return false;
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

//                    case R.id.navigation_bluetooth:
//                        mVp.setCurrentItem(2);
//                        return true;
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
//        adapter.addFragment(new Bluetooth());
        viewPager.setAdapter(adapter);
    }

    public void setActionBarColor(int color) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }
}
