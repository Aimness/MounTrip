package com.company.android.myapplication.Search;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.company.android.myapplication.R;
import com.company.android.myapplication.SomeUserProfileFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyAdapter extends RecyclerView.ViewHolder {
    public CircleImageView avatar;
    public TextView nick, comment, date, reply;

    public ReplyAdapter(View itemView)
    {
        super(itemView);

        avatar = itemView.findViewById(R.id.commentator_avatar_reply);
        nick = itemView.findViewById(R.id.commentator_nick_reply);
        comment = itemView.findViewById(R.id.comment_text_reply);
        date = itemView.findViewById(R.id.reply_date);
        reply = itemView.findViewById(R.id.reply);
    }
}
