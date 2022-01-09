package com.company.android.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.company.android.myapplication.Firebase.FirebaseMethods;
import com.company.android.myapplication.Tasks.ImageTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostFragment extends Fragment {
    private CircleImageView avatar;
    private TextView nickname, country,  days, distance, likesNumber, description;
    private ImageView difficulty, map, comments, back;
    private SliderView images;
    private ImageButton like;
    private Context mContext;
    private BottomNavigationView mBottomNavigationView;

    private DatabaseReference postsRef, usersRef, likesRef, postPhotoRef;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseUser mFirebaseUser;

    private String postId;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
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
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        postId = getArguments().getString("postId");
        mContext = container.getContext();
        mFirebaseMethods = new FirebaseMethods(mContext);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mBottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);

        postsRef = FirebaseDatabase.getInstance().getReference("Posts");
        usersRef = FirebaseDatabase.getInstance().getReference("UserData");
        postPhotoRef = FirebaseDatabase.getInstance().getReference("PostsPhotos");
        likesRef =  FirebaseDatabase.getInstance().getReference("Likes");

        avatar = view.findViewById(R.id.avatar_post_fragment);
        nickname = view.findViewById(R.id.nickname_post_fragment);
        country = view.findViewById(R.id.country_post_fragment);
        difficulty = view.findViewById(R.id.difficulty_post_fragment);
        days = view.findViewById(R.id.days_post_fragment);
        distance = view.findViewById(R.id.distance_post_fragment);
        images = view.findViewById(R.id.images_post_fragment);
        like = view.findViewById(R.id.likes_post_fragment);
        comments = view.findViewById(R.id.comments_post_fragment);
        likesNumber = view.findViewById(R.id.likes_number_post_fragment);
        description = view.findViewById(R.id.description_post_fragment);
        map = view.findViewById(R.id.map_post_fragment);
        back = view.findViewById(R.id.back_post_fragment);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(v).popBackStack();
                    }
                });
            }
        });

        Query query = postsRef.orderByChild("postId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        setLikeButtonStatus(dataSnapshot.child("uid").getValue(String.class));
                        loadData(dataSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void loadData(DataSnapshot dataSnapshot)
    {
        Query query = usersRef.orderByChild("uid").equalTo(dataSnapshot.child("uid").getValue(String.class));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot snap : snapshot.getChildren())
                    {
                        Glide.with(mContext).load(snap.child("image").getValue()).into(avatar);
                        nickname.setText(snap.child("nickname").getValue(String.class));

                        country.setText(dataSnapshot.child("country").getValue(String.class));
                        days.setText(dataSnapshot.child("days").getValue(String.class));
                        distance.setText(dataSnapshot.child("kilometers").getValue(String.class));
                        description.setText(dataSnapshot.child("description").getValue(String.class));

                        map.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri mapUri = Uri.parse(dataSnapshot.child("map").getValue(String.class));
                                mFirebaseMethods.mapOptions(mapUri);
                            }
                        });

                        comments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle args = new Bundle();
                                args.putString("postId", postId);
                                args.putString("postCreator", dataSnapshot.child("uid").getValue(String.class));

                                Navigation.findNavController(v).navigate(R.id.commentsFragment, args);
                            }
                        });


                        mFirebaseMethods.setLikeButtonStatus(postId, like, likesNumber);

                        like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                likesRef.addValueEventListener(new ValueEventListener() {
                                    Boolean buttonStatus = true;
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(buttonStatus == true) {
                                            if (snapshot.child(postId).hasChild(mFirebaseUser.getUid())) {
                                                likesRef.child(postId).child(mFirebaseUser.getUid()).removeValue();
                                                buttonStatus = false;
                                            } else {
                                                likesRef.child(postId).child(mFirebaseUser.getUid()).setValue(true);
                                                buttonStatus = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        likesNumber.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle args = new Bundle();
                                args.putString("uid", postId);
                                args.putString("followingsOrFollowers", "like");

                                Navigation.findNavController(v).navigate(R.id.followersFollowingsFragment, args);
                            }
                        });

                        switch (dataSnapshot.child("difficulty").getValue(String.class))
                        {
                            case "Easy":
                                difficulty.setImageResource(R.drawable.ic_easy);
                                break;

                            case "Difficult":
                                difficulty.setImageResource(R.drawable.ic_difficult);
                                break;

                            case "Hard":
                                difficulty.setImageResource(R.drawable.ic_hard);
                                break;
                        }

                        Query queryImages = postPhotoRef.orderByKey().equalTo(postId);
                        ImageTask imageTask = new ImageTask(mContext, queryImages, images, dataSnapshot.child("numOfImages").getValue(Integer.class));
                        imageTask.execute();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setLikeButtonStatus(final String key)
    {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int likesCounter = (int)snapshot.child(key).getChildrenCount();
                if(snapshot.child(key).hasChild(mFirebaseUser.getUid()))
                {
                    like.setImageResource(R.drawable.ic_like);
                    likesNumber.setText(likesCounter + " Likes");
                }else if(likesCounter == 0)
                {
                    like.setImageResource(R.drawable.notification);
                    likesNumber.setText("");
                }else
                {
                    like.setImageResource(R.drawable.notification);
                    likesNumber.setText(likesCounter + " Likes");
                }
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