package com.company.android.myapplication.Search;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.company.android.myapplication.R;
import com.company.android.myapplication.Tasks.LoadSearchPostsTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

public class PostsSearchFragment extends Fragment {
    private Button search;
    private Spinner parameters, filter, date;
    private RecyclerView mRecyclerView;
    private Context mContext;

    private ArrayAdapter arrayAdapterParameters, arrayAdapterFilters, arrayAdapterDate;
    private SortedSet<String> countries = new TreeSet<>();
    private List<String> spinnerCountryValues;
    private String selectedParameter, selectedDate, selectedFilter;
    private LinearLayoutManager mLinearLayoutManager;

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private ActivityResultContracts.RequestMultiplePermissions mRequestMultiplePermissions= new ActivityResultContracts.RequestMultiplePermissions();

    private String[] filters = { "Country", "Difficulty"};
    private String[] difficultyFilters = {"Easy", "Difficult", "Hard"};
    private String[] dateFilters = {"From old to new", "From new to old"};


    public PostsSearchFragment() {

    }

    public static PostsSearchFragment newInstance(String param1, String param2) {
        PostsSearchFragment fragment = new PostsSearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_posts_search, container, false);
        mContext = requireContext();

        filter = view.findViewById(R.id.filter_picker_post);
        parameters = view.findViewById(R.id.filter_parameters);
        date = view.findViewById(R.id.filter_date);
        search = view.findViewById(R.id.search_post_button);
        mRecyclerView = view.findViewById(R.id.search_post_recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        arrayAdapterFilters = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, filters);
        arrayAdapterFilters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(arrayAdapterFilters);


        arrayAdapterDate = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, dateFilters);
        arrayAdapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        date.setAdapter(arrayAdapterDate);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        for(Locale locale : Locale.getAvailableLocales())
        {
            if(!TextUtils.isEmpty(locale.getDisplayCountry()))
                countries.add(locale.getDisplayCountry());
        }

        spinnerCountryValues = new ArrayList<>(countries);
        countries.clear();
        spinnerCountryValues.add(0, "Any");


        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = parent.getSelectedItem().toString();

                switch(position) {
                   case 0:
                        arrayAdapterParameters = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, spinnerCountryValues);
                        arrayAdapterParameters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        parameters.setAdapter(arrayAdapterParameters);
                        break;
                    case 1:
                        arrayAdapterParameters = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, difficultyFilters);
                        arrayAdapterParameters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        parameters.setAdapter(arrayAdapterParameters);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        parameters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedParameter = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadSearchPostsTask loadSearchPostsTask = new LoadSearchPostsTask(mContext/*, mReadWritePermissions*/, mRecyclerView, selectedFilter, selectedParameter, selectedDate );
                loadSearchPostsTask.execute();

            }
        });



        return view;
    }

}