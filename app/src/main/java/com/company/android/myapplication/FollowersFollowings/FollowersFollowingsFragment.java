package com.company.android.myapplication.FollowersFollowings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.company.android.myapplication.R;
import com.company.android.myapplication.Users.Users;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersFollowingsFragment extends Fragment  {
    private String uid;
    private DatabaseReference followerRef, usersRef, followingRef, likesRef;
    private String followingsOrFollowers;
    private ImageView back;
    private RecyclerView mRecyclerView;
    private TextView title;

    private LinearLayoutManager mLinearLayoutManager;

    private BottomNavigationView mBottomNavigationView;
    private LinearLayout mLinearLayout;

    private Context mContext;

    public FollowersFollowingsFragment() {
        // Required empty public constructor
    }


    public static FollowersFollowingsFragment newInstance(String param1, String param2) {
        FollowersFollowingsFragment fragment = new FollowersFollowingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_followers_followings, container, false);

        mContext = container.getContext();



        followerRef = FirebaseDatabase.getInstance().getReference("Followers");
        followingRef = FirebaseDatabase.getInstance().getReference("Following");
        usersRef = FirebaseDatabase.getInstance().getReference("UserData");
        likesRef = FirebaseDatabase.getInstance().getReference("Likes");
        ArrayList<String> uids = new ArrayList<>();

        uid = getArguments().getString("uid");
        followingsOrFollowers = getArguments().getString("followingsOrFollowers");

        back = view.findViewById(R.id.followers_back);
        mRecyclerView = view.findViewById(R.id.followers_recycler);
        title = view.findViewById(R.id.followers_followings_text);

        mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });


        switch (followingsOrFollowers)
        {
            case "followers":
                title.setText("Followers");
                followerRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            uids.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                uids.add(dataSnapshot.getKey());

                            fillFollowersFollowing(uids);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case "following":
                uids.clear();
                title.setText("Following");
                followingRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                uids.add(dataSnapshot.getKey());

                            fillFollowersFollowing(uids);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case "like":
                uids.clear();
                title.setText("Likes");
                likesRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                uids.add(dataSnapshot.getKey());
                            fillFollowersFollowing(uids);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
        }

        return view;
    }

    private void fillFollowersFollowing(ArrayList<String> uids)
    {
        mLinearLayout = new LinearLayout(mContext);


        List<Users> usersList = new ArrayList<>();
        for(int i = 0; i < uids.size(); i++)
        {
            Query query = usersRef.orderByChild("uid").equalTo(uids.get(i));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            Users users = dataSnapshot.getValue(Users.class);
                            usersList.add(users);
                        }

                        FollowersFollowingsAdapter adapter = new FollowersFollowingsAdapter(mContext, usersList);
                        mRecyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}