package com.company.android.myapplication.Search;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.company.android.myapplication.R;
import com.company.android.myapplication.Users.Users;
import com.company.android.myapplication.Users.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersSearchFragment extends Fragment implements AdapterView.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private UsersAdapter mUsersAdapter;
    private List<Users> mUsersList;
    private SearchView mSearchView;
    private LinearLayoutManager mLinearLayoutManager;

    public UsersSearchFragment() {

    }

    public static UsersSearchFragment newInstance(String param1, String param2) {
        UsersSearchFragment fragment = new UsersSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_search, container, false);
        mContext = requireContext();

        mRecyclerView = view.findViewById(R.id.search_users_recycler_view);
        mSearchView = view.findViewById(R.id.search_view_users);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mUsersList = new ArrayList<>();

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!TextUtils.isEmpty(query.trim()))
                    searchUsers(query);
                else
                    mUsersList.clear();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim()))
                    searchUsers(newText);
                else
                    mUsersList.clear();


                return false;
            }
        });


        return view;

    }


    private void searchUsers(final String string)
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsersList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Users user = dataSnapshot.getValue(Users.class);

                    if(!user.getUid().equals(firebaseUser.getUid()))
                    {
                        if(user.getNickname().toLowerCase().contains(string.toLowerCase())
                        || user.getBio().toLowerCase().contains(string.toLowerCase()))
                            mUsersList.add(user);

                        mUsersAdapter = new UsersAdapter(mContext, mUsersList);
                        mRecyclerView.setAdapter(mUsersAdapter);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(mContext, "Hello", Toast.LENGTH_SHORT).show();

    }
}