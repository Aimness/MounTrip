package com.company.android.myapplication.Posts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.company.android.myapplication.Firebase.FirebaseMethods;
import com.company.android.myapplication.R;
import com.company.android.myapplication.Tasks.ImageTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.smarteist.autoimageslider.SliderView;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {
    private Context mContext;
    private List<Posts> posts;
    private Uri mapUri;

    private boolean buttonStatus, saveButtonStatus;
    private RequestQueue mRequestQueue;

    private DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes");
    private DatabaseReference postPhotoRef = FirebaseDatabase.getInstance().getReference("PostsPhotos");
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserData");
    private DatabaseReference savedRef = FirebaseDatabase.getInstance().getReference("SavedPosts");
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseMethods mFirebaseMethods;

    public PostsAdapter(Context mContext, List<Posts> posts) {
        this.mContext = mContext;
        this.posts = posts;
        mRequestQueue = new Volley().newRequestQueue(mContext);
        mFirebaseMethods = new FirebaseMethods(mContext);
    }
    

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.posts, parent, false);
        return new PostsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        Posts posts = this.posts.get(position);

        holder.setIsRecyclable(false);
        Log.i("PostPosition", Integer.toString(position));
        String postId = posts.getPostId();

        String country = posts.getCountry();
        String days = posts.getDays();
        String difficulty = posts.getDifficulty();
        String kilometers = posts.getKilometers();
        String description = posts.getDescription();
        Integer numOfImages = posts.getNumOfImages();
        String uid = posts.getUid();

        holder.country.setText(country);
        holder.days.setText(days);
        holder.kilometers.setText(kilometers);
        holder.description.setText(description);

        holder.likesNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("uid", postId);
                args.putString("followingsOrFollowers", "like");

                Navigation.findNavController(v).navigate(R.id.followersFollowingsFragment, args);


            }
        });

        holder.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("UserData", uid);

                Navigation.findNavController(v).navigate(R.id.someUserProfileFragment, args);
            }
        });

        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapUri = Uri.parse(posts.getMap());
                mFirebaseMethods.mapOptions(mapUri);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("postId", postId);
                args.putString("postCreator", uid);

                Navigation.findNavController(v).navigate(R.id.commentsFragment, args);
            }
        });

        mFirebaseMethods.setLikeButtonStatus(postId, holder.likes, holder.likesNum);

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStatus = true;

                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (buttonStatus == true) {
                            if (snapshot.child(postId).hasChild(mFirebaseUser.getUid())) {
                                likesRef.child(postId).child(mFirebaseUser.getUid()).removeValue();
                                buttonStatus = false;
                            } else {
                                likesRef.child(postId).child(mFirebaseUser.getUid()).setValue(true);
                                buttonStatus = false;

                                if(!uid.equals(mFirebaseUser.getUid())) {
                                    HashMap<String, Object> notificationData = new HashMap<>();
                                    notificationData.put("text", " liked your post");
                                    notificationData.put("sendNotificationTo", uid);
                                    notificationData.put("type", "like");
                                    notificationData.put("postId", postId);


                                    mFirebaseMethods.getCurrentUserNick(notificationData, mRequestQueue);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        setSavedButtonStatus(postId, holder.save);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonStatus = true;

                savedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(saveButtonStatus == true)
                        {
                            if(snapshot.child(mFirebaseUser.getUid()).hasChild(postId))
                            {
                                savedRef.child(mFirebaseUser.getUid()).child(postId).removeValue();
                                saveButtonStatus = false;
                            }else
                            {
                                savedRef.child(mFirebaseUser.getUid()).child(postId).setValue(true);
                                saveButtonStatus = false;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        switch (difficulty) {
            case "Easy":
                holder.difficulty.setImageResource(R.drawable.ic_easy);
                break;

            case "Difficult":
                holder.difficulty.setImageResource(R.drawable.ic_difficult);
                break;

            case "Hard":
                holder.difficulty.setImageResource(R.drawable.ic_hard);
                break;
        }


        Query quer = usersRef.orderByKey().equalTo(posts.getUid());
        quer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String imageUri = dataSnapshot.child("image").getValue(String.class);
                        String nickname = dataSnapshot.child("nickname").getValue(String.class);


                        holder.nickname.setText(nickname);
                        Glide.with(mContext).load(imageUri).into(holder.avatarUri);

                        Query query = postPhotoRef.orderByKey().equalTo(postId);
                        ImageTask imageTask = new ImageTask(mContext, query, holder.mSliderView, numOfImages);
                        imageTask.execute();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setSavedButtonStatus(String postId, ImageView save)
    {
        savedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(mFirebaseUser.getUid()).hasChild(postId))
                    save.setImageResource(R.drawable.save);
                else
                    save.setImageResource(R.drawable.unsave);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatarUri;
        public TextView nickname, country, days, likesNum, kilometers;
        public SliderView mSliderView;
        public ImageView comments, save, difficulty, map;
        public ImageButton likes;
        public ImageButton more;
        public ExpandableTextView description;

        private DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes");


        public PostsViewHolder(@NonNull View view) {
            super(view);
            avatarUri = view.findViewById(R.id.posts_profile_picture);
            nickname = view.findViewById(R.id.posts_nickname);
            country = view.findViewById(R.id.posts_country);
            days = view.findViewById(R.id.posts_days);
            difficulty = view.findViewById(R.id.posts_difficulty);
            mSliderView = view.findViewById(R.id.posts_images);
            likes = view.findViewById(R.id.posts_likes);
            comments = view.findViewById(R.id.posts_comments);
            save = view.findViewById(R.id.posts_save);
            likesNum = view.findViewById(R.id.posts_likes_number);
            description =  view.findViewById(R.id.posts_description);
            more = view.findViewById(R.id.posts_more);
            kilometers = view.findViewById(R.id.posts_distance);
            map = view.findViewById(R.id.posts_uploaded_map);
        }
    }
}
