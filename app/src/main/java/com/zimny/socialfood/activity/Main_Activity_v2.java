package com.zimny.socialfood.activity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.zimny.socialfood.R;
import com.zimny.socialfood.fragment.AdminOrderAddFragment;
import com.zimny.socialfood.fragment.MainFragment;
import com.zimny.socialfood.fragment.OrderAndHistoryOrderFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main_Activity_v2 extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.food: {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    MainFragment fragment = new MainFragment();
                    ft.replace(R.id.content, fragment);
                    Toast.makeText(getApplicationContext(), "text", Toast.LENGTH_SHORT).show();
                    ft.commit();
                }
                case R.id.orders: {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    OrderAndHistoryOrderFragment fragment = new OrderAndHistoryOrderFragment();
                    Toast.makeText(getApplicationContext(), "text", Toast.LENGTH_SHORT).show();
                    ft.replace(R.id.content, fragment);
                    ft.commit();
                }
                case R.id.social: {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    OrderAndHistoryOrderFragment fragment = new OrderAndHistoryOrderFragment();
                    Toast.makeText(getApplicationContext(), "text", Toast.LENGTH_SHORT).show();
                    ft.replace(R.id.content, fragment);
                    ft.commit();
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        AdminOrderAddFragment fragment = new AdminOrderAddFragment();
        ft.add(R.id.content, fragment);
        ft.commit();
        bottomNavigationView.setSelectedItemId(R.id.food);
        // bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout");
        return super.onCreateOptionsMenu(menu);
    }
}
