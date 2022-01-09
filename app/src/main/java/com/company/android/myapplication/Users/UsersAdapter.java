package com.company.android.myapplication.Users;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.android.myapplication.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Holder>{
    private final LayoutInflater mLayoutInflater;
    private final List <Users> mUsersList;
    private Context mContext;

    public UsersAdapter(Context context, List<Users> usersList) {
        this.mUsersList = usersList;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.show_users, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Users user = mUsersList.get(position);

        String avatar = user.getImage();
        final String nickname = user.getNickname();
        String bio = user.getBio();

        holder.nickname.setText(nickname);
        holder.bio.setText(bio);

        Glide.with(mContext).load(avatar).into(holder.avatar);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("UserData", user.getUid());

                Navigation.findNavController(v).navigate(R.id.someUserProfileFragment, args);
                InputMethodManager inputManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    class Holder extends  RecyclerView.ViewHolder
    {
        ImageView avatar;
        TextView bio, nickname;

        public Holder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar_search_users);
            bio = itemView.findViewById(R.id.bio_search_users);
            nickname = itemView.findViewById(R.id.nickname_search_users);
        }
    }
}
