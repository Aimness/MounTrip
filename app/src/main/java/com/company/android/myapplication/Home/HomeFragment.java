package com.company.android.myapplication.Home;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.android.myapplication.R;
import com.company.android.myapplication.Tasks.LoadPostsTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;

    private ActivityResultContracts.RequestMultiplePermissions mRequestMultiplePermissions= new ActivityResultContracts.RequestMultiplePermissions();

    private BottomNavigationView mBottomNavigationView;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = container.getContext();
        mRecyclerView = view.findViewById(R.id.posts_recycler);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        getFollowing();

        mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);

        return view;
    }

    private void getFollowing()
    {
        LoadPostsTask loadPostsTask = new LoadPostsTask(mContext, mRecyclerView);
        loadPostsTask.execute();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}