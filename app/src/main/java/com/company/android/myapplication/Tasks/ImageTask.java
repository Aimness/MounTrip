package com.company.android.myapplication.Tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.company.android.myapplication.Slider;
import com.company.android.myapplication.SliderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageTask extends AsyncTask<Void, Integer, Bitmap> {
    private Context mContext;
    private Query mQuery;
    private SliderView mSliderView;
    private ArrayList<Slider> sliderArrayList;
    private int numOfImages;

    public ImageTask(Context mContext, Query mQuery, SliderView mSliderView, int numOfImages)
    {
        this.mContext = mContext;
        this.mQuery = mQuery;
        this.mSliderView = mSliderView;
        this.numOfImages = numOfImages;
        sliderArrayList = new ArrayList<>();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    for(int i = 0; i < numOfImages; i++)
                    {
                        sliderArrayList.add(new Slider(Uri.parse(dataSnapshot.child("image" + (i+1)).getValue(String.class))));
                    }
                }

                SliderAdapter adapter = new SliderAdapter(mContext, sliderArrayList);

                mSliderView.setSliderAdapter(adapter);
                mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
