package com.company.android.myapplication;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.company.android.myapplication.Firebase.FirebaseMethods;


import com.hbb20.CountryCodePicker;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;


import java.util.ArrayList;
import java.util.List;


public class CreatePostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Context mContext;
    private SliderAdapter mSliderAdapter;
    private ArrayList<Slider> mSliderArrayList;
    private SliderView mSliderView;
    private int fileType;
    private CountryCodePicker mCountryCodePicker;

    private FirebaseMethods mFirebaseMethods;
    private ArrayList<Uri> mArrayListImages;

    private EditText descriptionET, kilometersET, daysET;
    private int numberOfImages;
    private ImageButton addMap;
    private Spinner mSpinner;
    private String countryName, spinnerSelected;
    private Uri mapUri;
    private String size, name;

    private String[] difficulty = {"Easy", "Difficult", "Hard"};
    private int[] images = {R.drawable.ic_easy, R.drawable.ic_difficult, R.drawable.ic_hard};
    private CustomSpinnerAdapter mCustomSpinnerAdapter;

    private static final int STORAGE_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mContext = CreatePostActivity.this;
        mFirebaseMethods = new FirebaseMethods(mContext);

        mSpinner = findViewById(R.id.spinner_difficulty_create_post);
        mSpinner.setOnItemSelectedListener(this);

        mCustomSpinnerAdapter = new CustomSpinnerAdapter(CreatePostActivity.this, images, difficulty);
        mSpinner.setAdapter(mCustomSpinnerAdapter);

        mSliderArrayList = new ArrayList<>();
        mArrayListImages = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar_create_post);
        mSliderView = findViewById(R.id.slider_view_create_post);
        addMap = findViewById(R.id.add_map);


        mCountryCodePicker = findViewById(R.id.find_country);

        descriptionET = findViewById(R.id.post_description);
        kilometersET = findViewById(R.id.distance_create_post);
        kilometersET.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_asterisk_red,0,0,0);
        daysET = findViewById(R.id.days_create_post);
        daysET.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_asterisk_red,0,0,0);

        setSupportActionBar(toolbar);

        countryName = mCountryCodePicker.getSelectedCountryName().toString();

        mCountryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryName = mCountryCodePicker.getSelectedCountryName();
            }
        });

        mSliderArrayList = new ArrayList<>();
        mSliderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileType = 0;
                requestStoragePermission();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Post");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileType = 1;
                requestStoragePermission();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerSelected = difficulty[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void requestStoragePermission()
    {
        if (ContextCompat.checkSelfPermission(CreatePostActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {

            Toast.makeText(mContext, "Storage permission denied", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(CreatePostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(CreatePostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_REQUEST_CODE);
            }
        } else {
            chooseFiles();
        }
    }

    private void chooseFiles()
    {
        switch(fileType)
        {
            case 0:
                pickStorage();
                break;
            case 1:
                pickStorageForMap();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length != permissions.length)
            Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext, "Permission granted", Toast.LENGTH_SHORT).show();

    }

    private void pickStorage()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.setAction(Intent.ACTION_OPEN_DOCUMENT);

        imageResultLauncher.launch(Intent.createChooser(gallery, "Select Picture"));
    }

    private void pickStorageForMap()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");

        mapResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> mapResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK)
                    {
                        if(result.getData() != null)
                        {
                            String fileExtension = "", extension = "gpx";
                            long bytes, kb, mb, gb;


                            Uri uri = result.getData().getData();
                            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                            cursor.moveToFirst();
                            name = cursor.getString(nameIndex);
                            bytes = Math.round(cursor.getDouble(sizeIndex));
                            kb = bytes/1024;
                            mb = kb/1024;
                            gb = mb/1024;

                            if(gb > 0)
                                size = gb + " GB";
                            else if(mb > 0)
                                size = mb + " MB";
                            else
                                size = kb + " KB";



                            int index = name.lastIndexOf('.');
                            if(index > 0)
                                fileExtension = name.substring(index+1);

                            Toast.makeText(mContext, "Extension: " + extension, Toast.LENGTH_SHORT).show();

                            if(extension.equalsIgnoreCase(fileExtension)) {
                                mapUri = null;
                                mapUri = result.getData().getData();
                                Toast.makeText(mContext, "Type " + extension, Toast.LENGTH_SHORT).show();
                                showFile();
                            }else
                                Toast.makeText(mContext, "Choose gpx file", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> imageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK)
                    {
                        if(result.getData() != null) {
                            int imageCount = result.getData().getClipData().getItemCount();
                            if (imageCount > 10) {
                                Toast.makeText(mContext, "You can pick max 10 images. First 10 images will be set", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < imageCount; i++) {
                                    Uri image = result.getData().getClipData().getItemAt(i).getUri();
                                    mArrayListImages.add(image);
                                }

                                Slider slider;
                                numberOfImages = mArrayListImages.size();
                                for (int i = 0; i < mArrayListImages.size(); i++) {
                                    slider = new Slider(mArrayListImages.get(i));
                                    Slider model = new Slider();

                                    model.setUrl(slider.getUrl());
                                    mSliderArrayList.add(model);

                                    mSliderAdapter = new SliderAdapter(mContext, mSliderArrayList);

                                }

                                mSliderView.setSliderAdapter(mSliderAdapter);
                                mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            }
                        }
                    }
                }
            });



    private void showFile()
    {
        View inflatedView = getLayoutInflater().inflate(R.layout.uploaded_files, null);
        LinearLayout linearLayout = findViewById(R.id.uploaded_files_layout);
        TextView fileName = inflatedView.findViewById(R.id.uploaded_file_name);
        TextView fileSize = inflatedView.findViewById(R.id.uploaded_file_size);
        ImageView imageView = inflatedView.findViewById(R.id.delete_file);

        fileName.setText(name);
        fileSize.setText(size);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout)inflatedView.getParent()).removeView(inflatedView);
                addMap.setEnabled(true);
                addMap.setImageResource(R.drawable.ic_map_black);
            }
        });

        linearLayout.addView(inflatedView);

        addMap.setEnabled(false);
        addMap.setImageResource(R.drawable.ic_map_grey);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_post_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {switch (item.getItemId())
    {
        case R.id.confrim_post:
            String  location = "", description = "", kilometers = "", days = "";
            description = descriptionET.getText().toString().trim();
            kilometers = kilometersET.getText().toString().trim();
            days = daysET.getText().toString().trim();
                if(checkForEmptyFields(kilometers, days, mapUri))
                {
                    String postTime = String.valueOf(System.currentTimeMillis());
                    mFirebaseMethods.addPost( location, description, kilometers,days,
                    spinnerSelected, mArrayListImages, postTime, numberOfImages, mapUri, countryName);
                    finish();
                }else
                    Toast.makeText(CreatePostActivity.this, "Fields with red asterisk are mandatory", Toast.LENGTH_SHORT).show();

            break;
    }


        return super.onOptionsItemSelected(item);
    }

    private Boolean checkForEmptyFields(String kilometers, String days, Uri uri)
    {

        if(kilometers.isEmpty())
        {
            kilometersET.setError("This field cannot be empty");
            kilometersET.setHint("Please enter the number of kilometers");
            return false;
        }

        if(days.isEmpty())
        {
            daysET.setError("This field cannot be empty");
            daysET.setHint("Please eneter the number of days");
            return false;
        }

        if(uri == null)
        {
            Toast.makeText(mContext, "Please upload map", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mArrayListImages.isEmpty())
        {
            Toast.makeText(mContext, "Please upload at least 2 images", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}


