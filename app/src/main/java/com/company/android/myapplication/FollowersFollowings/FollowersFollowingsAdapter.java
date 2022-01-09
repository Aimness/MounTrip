package com.company.android.myapplication.FollowersFollowings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.android.myapplication.R;
import com.company.android.myapplication.Users.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowersFollowingsAdapter extends RecyclerView.Adapter<FollowersFollowingsAdapter.FolowersFollowingsViewHolder> {
    private Context mContext;
    private List<Users>  mUsersList;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserData");

    public FollowersFollowingsAdapter(Context mContext, List<Users> mUsersList)
    {
        this.mContext = mContext;
        this.mUsersList = mUsersList;
     }

    @NonNull
    @Override
    public FollowersFollowingsAdapter.FolowersFollowingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(mContext).inflate(R.layout.followers_followings, parent, false);
        return new FolowersFollowingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersFollowingsAdapter.FolowersFollowingsViewHolder holder, int position) {
        Users fol = mUsersList.get(position);


        usersRef.orderByKey().equalTo(fol.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String nick = dataSnapshot.child("nickname").getValue(String.class);
                        String avatarUid = dataSnapshot.child("image").getValue(String.class);
                        String bio = dataSnapshot.child("bio").getValue(String.class);

                        holder.nick.setText(nick);
                        holder.bio.setText(bio);

                        if(avatarUid != null)
                            Glide.with(mContext).load(avatarUid).into(holder.avatar);
                        else
                            Glide.with(mContext).load(R.drawable.man).into(holder.avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FollowersFollowings", error.getDetails());
            }
        });


        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                    args.putString("UserData", fol.getUid());

                Navigation.findNavController(v).navigate(R.id.someUserProfileFragment, args);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public class FolowersFollowingsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView avatar;
        TextView nick, bio;
        LinearLayout mLinearLayout;
        public FolowersFollowingsViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.followers_followings_avatar);
            nick = itemView.findViewById(R.id.followers_followings_nick);
            mLinearLayout = itemView.findViewById(R.id.followers_following_linear);
            bio = itemView.findViewById(R.id.followers_followings_bio);
        }
    }

}
