package com.zimny.socialfood.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zimny.socialfood.R;

import net.yanzm.mth.MaterialTabHost;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderAndHistoryOrderFragment extends Fragment {
    @BindView(R.id.materialTabsOrderHistory)
    MaterialTabHost materialTabHost;
    @BindView(R.id.viewPagerOrderHistory)
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_and_history_order, container, false);
        ButterKnife.bind(this, v);
        ArrayList<Fragment> fragments = new ArrayList<>();
        ShoppingBasketFragment shoppingBasketFragment = new ShoppingBasketFragment();
        HistoryOrderFragment historyOrderFragment = new HistoryOrderFragment();
        fragments.add(shoppingBasketFragment);
        fragments.add(historyOrderFragment);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments);
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            materialTabHost.addTab(pagerAdapter.getPageTitle(i));
        }


        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(materialTabHost);
        materialTabHost.setOnTabChangeListener(new MaterialTabHost.OnTabChangeListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        return v;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Basket".toUpperCase(l);
                case 1:
                    return "History".toUpperCase(l);
            }
            return null;
        }
    }


}
