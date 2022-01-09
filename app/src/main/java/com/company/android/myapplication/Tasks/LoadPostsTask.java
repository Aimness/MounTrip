package com.company.android.myapplication.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.android.myapplication.Posts.Posts;
import com.company.android.myapplication.Posts.PostsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LoadPostsTask extends AsyncTask<Void, Integer,ArrayList<Posts>> {
    private ArrayList<Posts> mPostsList;
    private Context mContext;
    private DatabaseReference postRef;
    private FirebaseUser mUser;
    private PostsAdapter mPostsAdapter;
    private RecyclerView mRecyclerView;
    private DatabaseReference followingRef;
    private Query mQuery;

    public LoadPostsTask( Context mContext, RecyclerView mRecyclerView)
    {
        this.mContext =mContext;
        this.mRecyclerView = mRecyclerView;
        mPostsList = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
        followingRef = FirebaseDatabase.getInstance().getReference("Following");
    }

    public LoadPostsTask( Context mContext, RecyclerView mRecyclerView, Query mQuery)
    {
        this.mContext =mContext;
        this.mRecyclerView = mRecyclerView;

        mPostsList = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
        followingRef = FirebaseDatabase.getInstance().getReference("Following");

        this.mQuery = mQuery;
    }


    @Override
    protected ArrayList<Posts> doInBackground(Void... voids) {
        if(mQuery != null)
            fillAdapter();
        else {
            ArrayList<String> following = new ArrayList();
            followingRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        following.add(dataSnapshot.getKey());
                    }

                    fillAdapterHome(following);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        return null;
    }

    private void fillAdapterHome(ArrayList<String> following)
    {
        for(int i = 0; i < following.size(); i++)
        {
            final int counter = i;
            Log.i("User uid", following.get(i));
            Query query = postRef.orderByChild("uid").equalTo(following.get(i));
            ArrayList<String> finalFollowing = following;
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        mPostsList.clear();
                        Log.i("Following", "hahahah3");
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            Posts posts = dataSnapshot.getValue(Posts.class);
                            mPostsList.add(posts);
                        }
                        if(counter >= finalFollowing.size()-1)
                        {
                            Collections.sort(mPostsList);

                            mPostsAdapter = new PostsAdapter(mContext, mPostsList);
                            mRecyclerView.setAdapter(mPostsAdapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fillAdapter()
    {
        mPostsList.clear();

        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPostsList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Posts posts = dataSnapshot.getValue(Posts.class);
                    mPostsList.add(posts);
                }

                PostsAdapter postsAdapter = new PostsAdapter(mContext, mPostsList);
                mRecyclerView.setAdapter(postsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
