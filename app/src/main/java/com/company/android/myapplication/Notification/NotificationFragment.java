package com.company.android.myapplication.Notification;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.company.android.myapplication.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationFragment extends Fragment {
    private TextView clear;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private FirebaseUser mUser;
    private LinearLayoutManager mLinearLayoutManager;

    private DatabaseReference notificationReference;

    private BottomNavigationView mBottomNavigationView;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);

        mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);

        mContext = container.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        notificationReference = FirebaseDatabase.getInstance().getReference("Notification").child(mUser.getUid());

        mRecyclerView = view.findViewById(R.id.recycler_notification);
        clear = view.findViewById(R.id.clear_notification);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Notifications>()
                .setQuery(notificationReference, Notifications.class)
                .build();

        NotificationsAdapter adapter = new NotificationsAdapter(options, mContext);
        mRecyclerView.setAdapter(adapter);

        adapter.startListening();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}