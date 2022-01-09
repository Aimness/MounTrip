package com.company.android.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.company.android.myapplication.Tasks.LoadSavedTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SavedPostsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ImageView back;
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private BottomNavigationView mBottomNavigationView;

    public SavedPostsFragment() {

    }

    public static SavedPostsFragment newInstance(String param1, String param2) {
        SavedPostsFragment fragment = new SavedPostsFragment();
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
        View view = inflater.inflate(R.layout.fragment_saved_posts, container, false);

        mContext = container.getContext();
        mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);

        mRecyclerView = view.findViewById(R.id.saved_posts_recycler);
        back = view.findViewById(R.id.saved_posts_back);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

        LoadSavedTask loadSavedTask = new LoadSavedTask(mContext/*, mReadWritePermissions*/, mRecyclerView);
        loadSavedTask.execute();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}