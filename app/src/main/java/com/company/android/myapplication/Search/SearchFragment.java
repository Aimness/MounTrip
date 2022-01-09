package com.company.android.myapplication.Search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.company.android.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class SearchFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager2;
    private SearchViewPagerAdapter searchAdapter;

    private BottomNavigationView mBottomNavigationView ;

    public SearchFragment() {
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mTabLayout = view.findViewById(R.id.search_posts_users);
        mViewPager2 = view.findViewById(R.id.view_pager);
        mViewPager2.setUserInputEnabled(false);

        mBottomNavigationView = mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);

        mTabLayout.addTab(mTabLayout.newTab().setText("Users"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Posts"));

        mViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        searchAdapter = new SearchViewPagerAdapter(getChildFragmentManager(),getLifecycle());

        searchAdapter.addFragment(new UsersSearchFragment());
        searchAdapter.addFragment(new PostsSearchFragment());

        mViewPager2.setAdapter(searchAdapter);
        mViewPager2.setPageTransformer(new MarginPageTransformer(1500));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position)
                {
                    case 0:
                        tab.setText("Users");
                        break;
                    case 1:
                        tab.setText("Posts");
                        break;
                }
            }
        });

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPager2.setPageTransformer(new MarginPageTransformer(1500));

        tabLayoutMediator.attach();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}