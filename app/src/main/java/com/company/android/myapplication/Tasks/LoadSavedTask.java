package com.company.android.myapplication.Tasks;

import android.content.Context;
import android.os.AsyncTask;
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

public class LoadSavedTask extends AsyncTask<Void, Integer, Integer> {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private DatabaseReference savedRef;
    private FirebaseUser mUser;
    private DatabaseReference postRef;

    public LoadSavedTask(Context mContext, RecyclerView mRecyclerView)
    {
        this.mContext =mContext;
        this.mRecyclerView = mRecyclerView;

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        savedRef = FirebaseDatabase.getInstance().getReference("SavedPosts");
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        ArrayList<String> saved = new ArrayList<>();

        savedRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    saved.add(dataSnapshot.getKey());

                fillAdapterSaved();
            }

            private void fillAdapterSaved() {
                ArrayList<Posts> mPostsList = new ArrayList<>();

                for(int i = 0; i < saved.size(); i++)
                {
                    Query query = postRef.orderByKey().equalTo(saved.get(i));
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    Posts posts = dataSnapshot.getValue(Posts.class);
                                    mPostsList.add(posts);
                                }

                                PostsAdapter postsAdapter = new PostsAdapter(mContext, mPostsList/*, mReadWritePermissions*/);
                                mRecyclerView.setAdapter(postsAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return null;
    }


}
