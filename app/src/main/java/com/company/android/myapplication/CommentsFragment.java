package com.company.android.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.company.android.myapplication.Firebase.FirebaseMethods;
import com.company.android.myapplication.Search.ReplyAdapter;
import com.company.android.myapplication.Tags.Tags;
import com.company.android.myapplication.Tags.TagsAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;



public class CommentsFragment extends Fragment {
    private ImageView back;
    private CircleImageView avatar;
    private TextView nickname,  date;
    private ExpandableTextView text;
    private RecyclerView comments, replies;
    private ListView tagsListView;
    private EditText writeComment;
    private ImageView send;
    private LinearLayoutManager mLinearLayoutManager, replyLinearLayoutManager;

    private Context mContext;

    private DatabaseReference postsReference;
    private DatabaseReference commentReference, replyReference;
    private DatabaseReference userReference;
    private DatabaseReference postIdRef;
    private FirebaseUser mUser;
    private FirebaseRecyclerAdapter adapterComments, adapterReply;
    private FirebaseMethods mFirebaseMethods;

    private RelativeLayout replyLayout;
    private ImageButton closeReply;
    private TextView replyTo;
    private String commentID;

    private ArrayList<Tags> mTagsList;
    private TagsAdapter mTagsAdapter;
    private int start, end;
    private String postAuthorUid;
    private String strReplyTo;
    private RequestQueue mRequestQueue;
    boolean isExpand = false;

    private Integer replyPosition, commentPosition, scrollCommentPosition;
    private int sum;

    public CommentsFragment() {

    }

    public static CommentsFragment newInstance(String param1, String param2) {
        CommentsFragment fragment = new CommentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_comments, container, false);


        String postId = getArguments().getString("postId");
        String postCreator = getArguments().getString("postCreator");
        scrollCommentPosition = getArguments().getInt("commentPosition");
        replyPosition = getArguments().getInt("replyPosition");

        mContext = container.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseMethods = new FirebaseMethods(mContext);
        mRequestQueue = new Volley().newRequestQueue(mContext);

        mTagsList = new ArrayList<>();

        userReference = FirebaseDatabase.getInstance().getReference("UserData");
        postsReference = FirebaseDatabase.getInstance().getReference("Posts");
        commentReference = FirebaseDatabase.getInstance().getReference("Comments");

        back = view.findViewById(R.id.comments_back);
        avatar = view.findViewById(R.id.avatar_comments);
        nickname = view.findViewById(R.id.nick_comments);
        text = view.findViewById(R.id.text_comments);
        date = view.findViewById(R.id.post_date_comments);
        comments = view.findViewById(R.id.comments_recycler);
        writeComment = view.findViewById(R.id.enter_comment);
        send = view.findViewById(R.id.send_comment);
        replyLayout = view.findViewById(R.id.reply_layout);
        closeReply = view.findViewById(R.id.close_reply);
        replyTo = view.findViewById(R.id.reply_to);
        tagsListView = view.findViewById(R.id.tags);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        comments.setLayoutManager( mLinearLayoutManager);

        setPostInfo(postId);

        loadComments(postId);
        scrollToPositionComments();


        tagsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tags tag = (Tags) parent.getItemAtPosition(position);
                writeComment.getText().replace(start, end, tag.getNickname());
            }
        });

        closeReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyLayout.setVisibility(View.INVISIBLE);
                replyLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWHite));
                replyTo.setText(null);
                writeComment.setText(null);
            }
        });

        writeComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                Pattern pattern = Pattern.compile("[@][a-zA-Z0-9-.]+");
                Matcher matcher = pattern.matcher(text);
                if(text.isEmpty())
                {
                    tagsListView.setVisibility(View.INVISIBLE);
                }

                int cursor = writeComment.getSelectionStart();
                while(matcher.find())
                {
                    if(cursor >= matcher.start() && cursor <= matcher.end())
                    {
                        start = matcher.start() + 1;
                        end = matcher.end();
                        showTagsResult(text.substring(start, end));
                        break;
                    }

                    mTagsList.clear();
                    tagsListView.setVisibility(View.INVISIBLE);
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentStr = writeComment.getText().toString();
                if (!TextUtils.isEmpty(commentStr)) {
                    replyOrNot(commentStr, postId, postCreator);
                    writeComment.setText("");
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    replyLayout.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(mContext, "Please write something", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private FirebaseRecyclerAdapter neededAdapter;

    private void loadComments(String postId)
    {
        postIdRef = commentReference.child(postId);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setLifecycleOwner((LifecycleOwner) mContext)
                .setQuery(postIdRef, new SnapshotParser<DataSnapshot>() {
                    @NonNull
                    @Override
                    public DataSnapshot parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return snapshot;
                    }
                }).build();

        adapterComments = new FirebaseRecyclerAdapter<DataSnapshot, CommentAdapter>(options) {
            @NonNull
            @Override
            public CommentAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comments, parent, false);

                return new CommentAdapter(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentAdapter holder, int position, @NonNull DataSnapshot model) {


                String comment = model.child("comment").getValue(String.class);
                Long dateLong = model.child("commentTime").getValue(Long.class);
                String commentId = model.child("commentId").getValue(String.class);
                String uid = model.child("uid").getValue(String.class);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
                Date date = new Date(dateLong);

                Query query = userReference.orderByChild("uid").equalTo(uid);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String image = dataSnapshot.child("image").getValue(String.class);
                            String nickname = dataSnapshot.child("nickname").getValue(String.class);

                            Glide.with(mContext).load(image).into(holder.avatar);
                            holder.nick.setText(nickname);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.nick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(uid != mUser.getUid()) {
                                        Bundle args = new Bundle();
                                        args.putString("UserData", uid);

                                        if(uid.equals(mUser.getUid()))
                                            args.putString("Profile", "true");
                                        Navigation.findNavController(v).navigate(R.id.someUserProfileFragment, args);
                                    }
                                    else {
                                        back.performClick();
                                    }
                                }
                            });

                holder.comment.setText(makeSpannable(comment));
                holder.comment.setMovementMethod(LinkMovementMethod.getInstance());
                holder.date.setText(simpleDateFormat.format(date));

                holder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentID = model.child("commentId").getValue(String.class);
                        writeComment.setText("@" + holder.nick.getText() + " ");
                        replyLayout.setVisibility(View.VISIBLE);
                        replyLayout.setBackgroundColor(Color.parseColor("#FF262726"));
                        replyTo.setText("Replying to " + holder.nick.getText());
                        commentPosition = position;
                        strReplyTo = uid;
                    }
                });


                setRepliesButton(postId, holder, commentId);
                if(position == scrollCommentPosition)
                    if(replyPosition > 0)
                    {
                        replies = holder.mRecyclerView;
                        holder.expand.setImageResource(R.drawable.ic_collapse);
                        holder.mRecyclerView.setVisibility(View.VISIBLE);
                        loadReplies(holder,  postId, commentId, true);
                    }
                else
                    loadReplies(holder,  postId, commentId, true);

             }
        };

        comments.setAdapter(adapterComments);
        adapterComments.startListening();
    }

    private void setRepliesButton(String postId, CommentAdapter holder, String commentID)
    {
        commentReference.child(postId).child(commentID).child("Reply").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int)snapshot.getChildrenCount();
                if(count > 0) {
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.expand.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isExpand) {
                                isExpand = false;
                                holder.expand.setImageResource(R.drawable.ic_expand);
                                holder.mRecyclerView.setVisibility(View.VISIBLE);
                            }else {
                                isExpand = true;
                                holder.expand.setImageResource(R.drawable.ic_collapse);
                                holder.mRecyclerView.setVisibility(View.GONE);
                            }
                        }
                    });
                }else
                    holder.expand.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadReplies(CommentAdapter commentHolder,  String postId, String commentId, Boolean val)
    {
        Log.i("Comment", commentId);
        replyReference = commentReference.child(postId).child(commentId).child("Reply");

        replyLinearLayoutManager = new LinearLayoutManager(mContext);
        commentHolder.mRecyclerView.setLayoutManager(replyLinearLayoutManager);

        FirebaseRecyclerOptions optionsReply = new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setLifecycleOwner((LifecycleOwner)mContext)
                .setQuery(replyReference, new SnapshotParser<DataSnapshot>() {
                    @NonNull
                    @Override
                    public DataSnapshot parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return snapshot;
                    }
                }).build();

        adapterReply = new FirebaseRecyclerAdapter<DataSnapshot,ReplyAdapter>(optionsReply)
        {

            @NonNull
            @Override
            public ReplyAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.reply, parent, false);

                return new ReplyAdapter(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReplyAdapter holder, int position, @NonNull DataSnapshot model) {
                String comment = model.child("comment").getValue(String.class);
                String uid = model.child("uid").getValue(String.class);

                Query query = userReference.orderByChild("uid").equalTo(uid);;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                String image = dataSnapshot.child("image").getValue(String.class);
                                String nickname = dataSnapshot.child("nickname").getValue(String.class);

                                Glide.with(mContext).load(image).into(holder.avatar);
                                holder.nick.setText(nickname);

                                holder.nick.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle args = new Bundle();
                                        args.putString("UserData", uid);

                                        if(uid.equals(mUser.getUid()))
                                            args.putString("Profile", "true");
                                        Navigation.findNavController(v).navigate(R.id.someUserProfileFragment, args);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
                Date date = new Date(model.child("commentTime").getValue(Long.class));

                holder.comment.setText(makeSpannable(comment));
                holder.comment.setMovementMethod(LinkMovementMethod.getInstance());
                holder.date.setText(simpleDateFormat.format(date));

                holder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentID = model.child("commentId").getValue(String.class);
                        writeComment.setText("@" + holder.nick.getText()+" ");
                        replyLayout.setVisibility(View.VISIBLE);
                        replyLayout.setBackgroundColor(Color.parseColor("#FF262726"));
                        replyTo.setText("Replying to " + holder.nick.getText());
                        commentPosition = position;
                        strReplyTo = uid;
                    }
                });
            }
        };

        commentHolder.mRecyclerView.setAdapter(adapterReply);
        if(val)
            neededAdapter = adapterReply;
        adapterReply.startListening();
        scrollToPositionReplies();
    }


    private void checkForTags(String type, String comment, String postId)
    {
        Pattern pattern = Pattern.compile("[@][a-zA-Z0-9-.]+");
        SpannableString spannableString = new SpannableString(comment);
        Matcher matcher = pattern.matcher(spannableString);

        HashMap<String, Object> notificationData = new HashMap<>();

        while(matcher.find())
        {
            int start = matcher.start();
            int end = matcher.end();
            String tag = comment.substring(start+1, end).trim();

             Query query = userReference.orderByChild("nickname").equalTo(tag);

             query.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     if(snapshot.exists()) {
                         String uid = null;
                         for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                             uid = dataSnapshot.child("uid").getValue(String.class);
                         }

                         if(type.equals("reply") && !uid.equals(strReplyTo) && !uid.equals(mUser.getUid()))
                         {
                             notificationData.put("text", " tagged you in a comment");
                             notificationData.put("postAuthorUid", postAuthorUid);
                             notificationData.put("type", "tag");
                             notificationData.put("postId", postId);
                             getRepliesCount(notificationData, postId);
                             notificationData.put("commentPosition", commentPosition);
                             notificationData.put("sendNotificationTo", uid);

                             mFirebaseMethods.getCurrentUserNick(notificationData, mRequestQueue);
                         }else if(type.equals("comment"))
                         {
                             notificationData.clear();
                             notificationData.put("text", " tagged you in a comment");
                             notificationData.put("sendNotificationTo", uid);
                             notificationData.put("postAuthorUid", postAuthorUid);
                             getCommentsCount(postId, notificationData);
                             notificationData.put("type", "comment");
                             notificationData.put("postId", postId);
                             notificationData.put("commentPosition", commentPosition);

                             mFirebaseMethods.getCurrentUserNick(notificationData, mRequestQueue);
                         }
                     }else
                     {
                         Toast.makeText(mContext, "User not found", Toast.LENGTH_SHORT).show();
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {
                     Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                 }
             });

        }
    }

    private SpannableString makeSpannable(String comment)
    {
        Pattern pattern = Pattern.compile("[@][a-zA-Z0-9-.]+");
        SpannableString spannableString = new SpannableString(comment);
        Matcher matcher = pattern.matcher(spannableString);

        while (matcher.find())
        {
            int start = matcher.start();
            int end = matcher.end();

            ClickableSpan span = new ClickableSpan() {

                String uid;
                @Override
                public void onClick(@NonNull View widget) {

                    Query query = userReference.orderByChild("nickname").equalTo(comment.substring(start+1, end).trim());

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    uid = dataSnapshot.child("uid").getValue(String.class);
                                }
                                Bundle args = new Bundle();
                                args.putString("UserData", uid);

                                if(uid.equals(mUser.getUid()))
                                    args.putString("Profile", "true");
                                Navigation.findNavController(widget).navigate(R.id.someUserProfileFragment, args);
                            }else
                            {
                                Toast.makeText(mContext, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(ContextCompat.getColor(mContext, R.color.appColor));
                    ds.setUnderlineText(false);
                }
            };

            spannableString.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return  spannableString;
    }

    private void showTagsResult(String tag)
    {
        tagsListView.setVisibility(View.VISIBLE);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mTagsList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Tags tags = dataSnapshot.getValue(Tags.class);

                    if(!tags.getUid().equals(firebaseUser.getUid()))
                    {
                        if(tags.getNickname().toLowerCase().contains(tag.toLowerCase()))
                            mTagsList.add(tags);
                    }
                }

                if(mTagsList.isEmpty()) {
                    tagsListView.setVisibility(View.INVISIBLE);
                }else{
                    mTagsAdapter = new TagsAdapter(mContext, mTagsList);
                    tagsListView.setAdapter(mTagsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }


    private void replyOrNot(String comment, String postId, String postCreator)
    {
        String key = commentReference.push().getKey();
        HashMap<String, Object> comments = new HashMap<>();
        HashMap<String, Object> notificationData = new HashMap<>();

        if(replyLayout.getVisibility() == View.VISIBLE)
        {
            replyReference = commentReference.child(postId).child(commentID).child("Reply");
            String replyKey = replyReference.push().getKey();

            comments.put("uid", mUser.getUid());
            comments.put("comment", comment);
            comments.put("commentTime", System.currentTimeMillis());
            comments.put("commentId", commentID);
            comments.put("replyId", replyKey);

            replyReference.child(replyKey).setValue(comments)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

              notificationData.clear();

            checkForTags("reply", comment, postId);

            if(!strReplyTo.equals(mUser.getUid())) {
                notificationData.put("text", " replied your comment");
                notificationData.put("postAuthorUid", postAuthorUid);
                notificationData.put("type", "reply");
                notificationData.put("postId", postId);
                getRepliesCount(notificationData, postId);
                notificationData.put("commentPosition", commentPosition);
                notificationData.put("sendNotificationTo", strReplyTo);

                mFirebaseMethods.getCurrentUserNick(notificationData, mRequestQueue);
            }
        }else
        {
            comments.put("uid", mUser.getUid());
            comments.put("comment", comment);
            comments.put("commentTime", System.currentTimeMillis());
            comments.put("commentId", key);

            commentReference.child(postId).child(key).setValue(comments)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            checkForTags("comment", comment, postId);

            if(!postCreator.equals(mUser.getUid())) {
                notificationData.clear();
                notificationData.put("text", " commented your post");
                notificationData.put("sendNotificationTo", postAuthorUid);
                getCommentsCount(postId, notificationData);
                notificationData.put("type", "comment");
                notificationData.put("postId", postId);
                notificationData.put("commentPosition", commentPosition);
                mFirebaseMethods.getCurrentUserNick(notificationData,  mRequestQueue);
            }

            writeComment.setText("");
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
        comments.clear();
    }

    private void getCommentsCount(String postId, HashMap<String, Object> notificationData)
    {

        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationData.put("commentPosition", (int) snapshot.child(postId).getChildrenCount() -1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRepliesCount(HashMap<String, Object> notificationData, String postId)
    {
        commentReference.child(postId).child(commentID).child("Reply").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationData.put("replyPosition", (int)snapshot.getChildrenCount() -1);
                    Log.i("countReplies", Integer.toString((int)snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPostInfo(String postId)
    {
        Query query = postsReference.orderByKey().equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String textStr =  dataSnapshot.child("description").getValue(String.class);
                    postAuthorUid = dataSnapshot.child("uid").getValue(String.class);
                    String stringTime = dataSnapshot.child("time").getValue(String.class);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
                    Date dateConvert = new Date(Long.valueOf(stringTime));

                    date.setText(simpleDateFormat.format(dateConvert));
                    text.setText(textStr);

                    Query query = userReference.orderByKey().equalTo(postAuthorUid);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                String nick = dataSnapshot.child("nickname").getValue(String.class);
                                String avatarUri = dataSnapshot.child("image").getValue(String.class);
                                Glide.with(mContext).load(avatarUri).into(avatar);
                                nickname.setText(nick);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToPositionReplies()
    {
        if(scrollCommentPosition != 0 )
        {
            neededAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    replies.scrollToPosition(replyPosition-1);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    replies.smoothScrollToPosition(replyPosition-1);
                }
            });
        }
    }

    private void scrollToPositionComments()
    {
        if(scrollCommentPosition != 0)
        {
            adapterComments.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    comments.scrollToPosition(replyPosition);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    comments.smoothScrollToPosition(scrollCommentPosition);
                }
            });
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        adapterComments.startListening();
        Log.i("Comments", "OnStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapterComments.stopListening();
        Log.i("Comments", "OnDestroy");
    }


    @Override
    public void setInitialSavedState(@Nullable SavedState state) {
        super.setInitialSavedState(state);
    }
}