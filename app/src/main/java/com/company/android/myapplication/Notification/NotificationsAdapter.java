package com.company.android.myapplication.Notification;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.android.myapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class NotificationsAdapter extends FirebaseRecyclerAdapter<Notifications, NotificationsAdapter.NotificationViewHolder> {
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserData");
    private FirebaseUser mUser;
    private Context mContext;
    private DatabaseReference postsReference;

    public NotificationsAdapter(@NonNull FirebaseRecyclerOptions<Notifications> options, Context mContext) {
        super(options);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationsAdapter.NotificationViewHolder holder, int position, @NonNull Notifications model) {

        int pos = model.getPosition();
        String type = model.getType();
        String uid = model.getUid();
        Long dateLong = Long.parseLong(model.getDate());
        String postId = model.getPostId();

        postsReference = FirebaseDatabase.getInstance().getReference("Posts");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
        Date date = new Date(dateLong);

        holder.date.setText(simpleDateFormat.format(date));

        Query query = usersRef.orderByKey().equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String imageUrl = dataSnapshot.child("image").getValue(String.class);
                    if(imageUrl != null)
                        Glide.with(mContext).load(imageUrl).into(holder.avatar);
                    else
                        Glide.with(mContext).load(R.drawable.man).into(holder.avatar);

                    holder.date.setText(simpleDateFormat.format(date));
                    String nickname = dataSnapshot.child("nickname").getValue(String.class);

                    switch (type)
                    {
                        case "like":
                                holder.notification.setText(nickname + " liked your post");
                            break;
                        case "follow":
                            holder.notification.setText(nickname + " started following you");
                            break;
                        case "comment":
                            holder.notification.setText(nickname + " commented your post");
                            break;
                        case "reply":
                            holder.notification.setText(nickname + " replied to your comment");
                            break;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!uid.equals(mUser.getUid())) {
                    Bundle args = new Bundle();
                    args.putString("UserData", uid);
                    args.putInt("Position", pos);

                    Navigation.findNavController(v).navigate(R.id.someUserProfileFragment, args);
                }
            }
        });


        holder.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                NavController nav = Navigation.findNavController(v);
                switch (type)
                {
                    case "like":
                        args.clear();
                        args.putString("postId", postId);

                        nav.navigate(R.id.postFragment, args);
                        break;
                    case "follow":
                        args.clear();
                        args.putString("UserData", uid);

                        nav.navigate(R.id.someUserProfileFragment, args);
                        break;
                    case "comment":
                        args.clear();
                        getCreatorUid(postId, args, model, "comment", nav);
                        break;
                    case "reply":
                        args.clear();
                        getCreatorUid(postId, args, model, "reply", nav);
                        break;


                }
            }
        });
    }

    private void getCreatorUid(String postId, Bundle args, Notifications model, String type, NavController nav)
    {
        Query query = postsReference.orderByChild("postId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {

                        args.putString("postId", postId);
                        args.putString("postCreator", dataSnapshot.child("uid").getValue(String.class));
                        args.putInt("commentPosition", model.getCommentPosition());
                        if(type == "reply")
                            args.putInt("replyPosition", model.getReplyPosition()+1);

                        nav.navigate(R.id.commentsFragment, args);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @NonNull
    @Override
    public NotificationsAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification, parent,false);
        return new NotificationsAdapter.NotificationViewHolder(view);
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        ImageView avatar;
        TextView notification;
        TextView date;
        LinearLayout mLinearLayout;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.user_image_notification);
            notification = itemView.findViewById(R.id.text_notification);
            date = itemView.findViewById(R.id.date_notification);
            mLinearLayout = itemView.findViewById(R.id.notification_layout);
        }
    }
}
