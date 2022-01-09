package com.company.android.myapplication.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.android.myapplication.CreatePostActivity;
import com.company.android.myapplication.Firebase.FirebaseMethods;
import com.company.android.myapplication.R;
import com.company.android.myapplication.Tasks.LoadPostsTask;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private ImageView avatar, savedPosts;
    private TextView nickname, bio, website;
    private FirebaseMethods mFirebaseMethods;
    private Context mContext;
    private AppBarLayout mAppBarLayout;
    private Menu mMenu;
    private RecyclerView mRecyclerView;
    private FirebaseUser mCurrentUser;
    private TextView followers, followings;
    private LinearLayoutManager mLinearLayoutManager;

    private ImageView addPost, setting;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference followingRef;
    private DatabaseReference followersRef;

    private Parcelable mListState;

    private BottomNavigationView mBottomNavigationView;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);

        mContext = container.getContext();
        mFirebaseMethods = new FirebaseMethods(mContext);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        followingRef = FirebaseDatabase.getInstance().getReference("Following");
        followersRef = FirebaseDatabase.getInstance().getReference("Followers");


        avatar = view.findViewById(R.id.profile_avatar);
        nickname = view.findViewById(R.id.profile_nickname);
        bio = view.findViewById(R.id.profile_bio);
        website = view.findViewById(R.id.profile_website);
        website.setMovementMethod(LinkMovementMethod.getInstance());
        followers = view.findViewById(R.id.followers_count);
        followings = view.findViewById(R.id.following_count);
        website = view.findViewById(R.id.profile_website);
        savedPosts = view.findViewById(R.id.saved_posts);

        //addPost = view.findViewById(R.id.add_post_profile);
        //setting = view.findViewById(R.id.settings_profile);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mAppBarLayout = view.findViewById(R.id.app_bar);
        mRecyclerView = view.findViewById(R.id.posts_recycler);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        setHasOptionsMenu(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mFirebaseMethods.getUserDataProfile(avatar, nickname, bio, website);

        loadPosts();

        setFollowingFollowers();

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("uid", mCurrentUser.getUid());
                args.putString("followingsOrFollowers", "followers");

                Navigation.findNavController(v).navigate(R.id.followersFollowingsFragment, args);
            }
        });

        followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("uid", mCurrentUser.getUid());
                args.putString("followingsOrFollowers", "following");

                Navigation.findNavController(v).navigate(R.id.followersFollowingsFragment, args);
            }
        });

        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.savedPostsFragment);

            }
        });


        /*setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.settingsFragment);
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreatePostActivity.class);
                startActivity(intent);
            }
        });*/

        return view;
    }

    private void loadPosts()
    {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        Query query =  mDatabaseReference.orderByChild("uid").equalTo(mCurrentUser.getUid());
        LoadPostsTask loadHomePostsTask = new LoadPostsTask(mContext/*, mReadWritePermissions*/, mRecyclerView, query);
        loadHomePostsTask.execute();
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.mMenu = menu;
        inflater.inflate(R.menu.menu_profile, menu);
        hideShow();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addPost_profile:
                Intent intent = new Intent(mContext, CreatePostActivity.class);
                startActivity(intent);
                break;
            case R.id.edit_profile:
                NavHostFragment.findNavController(this).navigate(R.id.settingsFragment);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideShow() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                MenuItem menuItem = mMenu.findItem(R.id.addPost_profile);
                menuItem.setVisible(true);
                MenuItem menuItem1 = mMenu.findItem(R.id.nicknameTopBar);
                if(Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange())
                {
                    menuItem1.setVisible(true);
                    menuItem1.setTitle(nickname.getText());

                }else if (verticalOffset == 0)
                    menuItem1.setVisible(false);
            }
        });
    }



    private void setFollowingFollowers()
    {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(Integer.toString((int)snapshot.child(mCurrentUser.getUid()).getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followings.setText(Integer.toString((int)snapshot.child(mCurrentUser.getUid()).getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mListState = mLinearLayoutManager.onSaveInstanceState();
        outState.putParcelable("500", mListState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            mLinearLayoutManager.onRestoreInstanceState(mListState);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}