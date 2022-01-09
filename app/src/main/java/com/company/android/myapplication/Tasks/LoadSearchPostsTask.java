package com.company.android.myapplication.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.android.myapplication.Posts.Posts;

import com.company.android.myapplication.Posts.PostsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadSearchPostsTask extends AsyncTask<Void, Integer, ArrayList<Posts>> {
    private ArrayList<Posts> mPostsList;
    private Context mContext;
    //private ReadWritePermissions mReadWritePermissions;
    private DatabaseReference postRef;

    private RecyclerView mRecyclerView;

    private String selectedFilter, selectedParameter, selectedDate;

    public LoadSearchPostsTask( Context mContext, RecyclerView mRecyclerView,  String selectedFilter, String selectedParameter, String selectedDate)
    {
        this.mContext =mContext;
        this.mRecyclerView = mRecyclerView;
        this.selectedFilter = selectedFilter;
        this.selectedParameter = selectedParameter;
        this.selectedDate = selectedDate;
        mPostsList = new ArrayList<>();
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
    }

    @Override
    protected ArrayList<Posts> doInBackground(Void... voids) {
        switch(selectedFilter)
        {
            case "Country":
                if(selectedParameter != "Any")
                    showPostsByCountry();
                else
                   showAllCountries();

                break;
            case "Difficulty":
                showPostsByDifficulty();
                break;
        }

        return null;
    }

    private void showAllCountries()
    {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPostsList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Posts posts = dataSnapshot.getValue(Posts.class);
                    mPostsList.add(posts);
                }

                sortByDate();
                PostsAdapter postsAdapter = new PostsAdapter(mContext, mPostsList/*, mReadWritePermissions*/);
                mRecyclerView.setAdapter(postsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPostsByCountry()
    {
        Query query = postRef.orderByChild("country").equalTo(selectedParameter);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPostsList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    Posts posts = dataSnapshot.getValue(Posts.class);
                    mPostsList.add(posts);
                }

                sortByDate();

                PostsAdapter postsAdapter = new PostsAdapter(mContext, mPostsList/*, mReadWritePermissions*/);
                mRecyclerView.setAdapter(postsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortByDate()
    {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        if (selectedDate == "From new to old") {
            mLinearLayoutManager.setReverseLayout(true);
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
        } else {
            mLinearLayoutManager.setReverseLayout(false);
            mLinearLayoutManager.setStackFromEnd(false);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
        }
    }

    private void showPostsByDifficulty()
    {
        Query query = postRef.orderByChild("difficulty").equalTo(selectedParameter);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPostsList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Posts posts = dataSnapshot.getValue(Posts.class);
                    mPostsList.add(posts);
                }

                sortByDate();

                PostsAdapter homePostsAdapter = new PostsAdapter(mContext, mPostsList/*, mReadWritePermissions*/);
                mRecyclerView.setAdapter(homePostsAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
