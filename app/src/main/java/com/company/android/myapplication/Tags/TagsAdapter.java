package com.company.android.myapplication.Tags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.android.myapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TagsAdapter extends ArrayAdapter<Tags>{

    private ArrayList<Tags> mListTags = new ArrayList<>();
    private Context mContext;

    public TagsAdapter(@NonNull Context mContext, ArrayList<Tags> mListTags)
    {
        super(mContext, 0 , mListTags);
        this.mListTags = mListTags;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View items = convertView;
        if(items == null)
            items = LayoutInflater.from(getContext()).inflate(R.layout.tags, parent, false);

        Tags tags = getItem(position);

        CircleImageView avatar = items.findViewById(R.id.avatar_tags);
        TextView nick = items.findViewById(R.id.nickname_tags);
        String uid = tags.getUid();

        nick.setText(tags.getNickname());


        Glide.with(mContext).load(tags.getImage()).into(avatar);

        return items;
    }

    public Tags getItem(int position)
    {
        return mListTags.get(position);
    }
}
