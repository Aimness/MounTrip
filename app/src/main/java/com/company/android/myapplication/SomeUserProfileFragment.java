package com.company.android.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.company.android.myapplication.Firebase.FirebaseMethods;
import com.company.android.myapplication.Tasks.LoadPostsTask;
import com.company.android.myapplication.Posts.Posts;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SomeUserProfileFragment extends Fragment {
    private ImageView avatar, back, addPost;
    private TextView nickname, bio;
    private Context mContext;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private ImageButton follow;
    private TextView followers, followings, website, title;

    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
    private DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Following");
    private DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Followers");

    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private int followersCounter, followingsCounter;
    private boolean followButtonStatus;

    private String uid;

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private LinearLayoutManager mLinearLayoutManager;

    private RequestQueue mRequestQueue;
    private BottomNavigationView mBottomNavigationView;

    private boolean buttonStatus = true;

    public SomeUserProfileFragment() {

    }

    public static SomeUserProfileFragment newInstance(String param1, String param2) {
        SomeUserProfileFragment fragment = new SomeUserProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_some_user_profile, container, false);
        mContext = container.getContext();
        mFirebaseMethods = new FirebaseMethods(mContext);
        mRequestQueue = new Volley().newRequestQueue(mContext);
        mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);

        toolbar = view.findViewById(R.id.toolbar_some_user);
        avatar = view.findViewById(R.id.profile_avatar_some_user);
        bio = view.findViewById(R.id.profile_bio_some_user);
        mRecyclerView = view.findViewById(R.id.posts_recycler);
        follow = view.findViewById(R.id.follow_button);
        followings = view.findViewById(R.id.following_count_some_user);
        followers = view.findViewById(R.id.followers_count_some_user);
        nickname = view.findViewById(R.id.nickname_some_user);
        back = view.findViewById(R.id.some_user_back);
        website = view.findViewById(R.id.some_user_profile_website);
        title = view.findViewById(R.id.user_nick_toolbar);
        addPost = view.findViewById(R.id.add_post_some_user_profle);


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().popBackStackImmediate();
            }
        });

        uid = getArguments().getString("UserData");


        if(getArguments().getString("Profile") != null) {
            follow.setVisibility(View.GONE);
            addPost.setVisibility(View.VISIBLE);
            addPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CreatePostActivity.class);
                    startActivity(intent);
                }
            });
        }else {
            addPost.setVisibility(View.GONE);
            follow.setVisibility(View.VISIBLE);
        }

        loadPosts();
        setFollowingFollowersNumber();
        setFollowButtonStatus();

        mFirebaseMethods.getUserDataProfile(avatar, nickname, bio, uid, website, title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButtonStatus = true;

                followersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (followButtonStatus == true) {
                            if (snapshot.child(uid).hasChild(mFirebaseUser.getUid())) {
                                followersRef.child(uid).child(mFirebaseUser.getUid()).removeValue();
                                followingRef.child(mFirebaseUser.getUid()).child(uid).removeValue();
                                follow.setImageResource(R.drawable.ic_follow);
                                followButtonStatus = false;
                            } else {
                                followersRef.child(uid).child(mFirebaseUser.getUid()).setValue(true);
                                followingRef.child(mFirebaseUser.getUid()).child(uid).setValue(true);
                                follow.setImageResource(R.drawable.ic_unfollow);
                                followButtonStatus = false;
                                HashMap<String, Object> notificationData = new HashMap<>();

                                notificationData.put("text", " started following you");
                                notificationData.put("type", "follow");
                                notificationData.put("sendNotificationTo", uid);
                                mFirebaseMethods.getCurrentUserNick(notificationData, mRequestQueue);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("uid", uid);
                args.putString("followingsOrFollowers", "followers");

                Navigation.findNavController(v).navigate(R.id.followersFollowingsFragment, args);
            }
        });

        followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("uid", uid);
                args.putString("followingsOrFollowers", "followers");

                Navigation.findNavController(v).navigate(R.id.followersFollowingsFragment, args);
            }
        });

        return view;
    }

    private void loadPosts()
    {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postRef.orderByChild("uid").equalTo(uid), Posts.class)
                .build();
        Query query = postRef.orderByChild("uid").equalTo(uid);

        LoadPostsTask loadPostsTask = new LoadPostsTask(mContext /*,mReadWritePermissions*/, mRecyclerView, query);
        loadPostsTask.execute();

    }

    private void setFollowingFollowersNumber() {
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersCounter = (int) snapshot.child(uid).getChildrenCount();
                followers.setText(Integer.toString(followersCounter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingsCounter = (int) snapshot.child(uid).getChildrenCount();
                followings.setText(Integer.toString(followingsCounter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFollowButtonStatus() {
        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(mFirebaseUser.getUid()).hasChild(uid)) {
                    follow.setImageResource(R.drawable.ic_unfollow);
                } else
                    follow.setImageResource(R.drawable.ic_follow);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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