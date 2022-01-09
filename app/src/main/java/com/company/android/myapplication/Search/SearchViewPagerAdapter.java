package com.company.android.myapplication.Search;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class SearchViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> mFragmentArrayList = new ArrayList<>();


    public SearchViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UsersSearchFragment();
            case 1:
                return new PostsSearchFragment();

        }
        return null;
    }

    public void addFragment(Fragment fragment)
    {
        mFragmentArrayList.add(fragment);
    }

    @Override
    public int getItemCount() {
        return mFragmentArrayList.size();
    }
}
