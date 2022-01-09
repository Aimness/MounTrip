package com.company.android.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.android.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.ViewHolder{
    CircleImageView avatar;
    TextView nick, comment, date, reply;
    RecyclerView mRecyclerView;
   ImageView expand;

    public CommentAdapter(View view)
    {
        super(view);

        avatar = view.findViewById(R.id.commentator_avatar);
        nick = view.findViewById(R.id.commentator_nick);
        comment = view.findViewById(R.id.comment_text);
        date = view.findViewById(R.id.comment_date);
        reply = view.findViewById(R.id.comment_reply);
        mRecyclerView = view.findViewById(R.id.reply_recycler_view);
        expand = view.findViewById(R.id.expand_button);
    }



}
