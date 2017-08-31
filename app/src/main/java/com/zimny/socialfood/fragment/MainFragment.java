package com.zimny.socialfood.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zimny.socialfood.R;

import net.yanzm.mth.MaterialTabHost;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainFragment extends Fragment {


    @BindView(R.id.materialtabs)
    MaterialTabHost materialTabHost;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);
        ArrayList<Fragment> fragments = new ArrayList<>();
        FoodsFragment foodsFragment = new FoodsFragment();
        GroupsFragment groupsFragment = new GroupsFragment();
        fragments.add(foodsFragment);
        fragments.add(groupsFragment);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getFragmentManager(), fragments);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_sample, container, false);
            TextView tv = (TextView) view.findViewById(R.id.text);
            tv.setText("Here is page ");
            return view;
        }
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
                    return "food".toUpperCase(l);
                case 1:
                    return "groups".toUpperCase(l);
            }
            return null;
        }
    }

}
