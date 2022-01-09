package com.company.android.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.company.android.myapplication.Firebase.FirebaseMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {
    private  ImageView avatar;
    private EditText nickname, bio, website;
    private  Context mContext;
    private Uri avatarImage;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int READ_PERMISSION_CODE = 200;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserData");
    private boolean exists;

    private ImageView back, check;

    public EditProfileFragment() {

    }

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mContext = container.getContext();
        mFirebaseMethods = new FirebaseMethods(mContext);

        avatar = view.findViewById(R.id.avatar_edit_profile);
        nickname = view.findViewById(R.id.nicknameEditProfile);
        bio = view.findViewById(R.id.bioEditProfile);
        website = view.findViewById(R.id.websiteEditProfile);
        back = view.findViewById(R.id.edit_profile_back);
        check = view.findViewById(R.id.edit_profile_check);

        mFirebaseMethods.setDataForEditing(nickname, bio, website, avatar);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChoosePhoto();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameExistsOrNot(nickname);
            }
        });

        return view;
    }

    public void usernameExistsOrNot(EditText loginEt)
    {
        String usernick = loginEt.getText().toString();

        Query query = usersRef.orderByChild("nickname").equalTo(usernick);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    loginEt.setError("This username already exists");
                    loginEt.setFocusable(true);
                }else
                    mFirebaseMethods.updateInfo(nickname.getText().toString(), bio.getText().toString(), website.getText().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogChoosePhoto() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.camera_profile_dialog);

        LinearLayout gallery = bottomSheetDialog.findViewById(R.id.choosegallery);
        LinearLayout camera = bottomSheetDialog.findViewById(R.id.choosecamera);


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkReadPermission()) {
                    pickStorage();
                    bottomSheetDialog.dismiss();
                }

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkCameraPermission())
                {
                    pickCamera();
                    bottomSheetDialog.dismiss();
                }
            }
        });


        bottomSheetDialog.show();
    }

    private boolean checkCameraPermission()
    {
        if(mContext.checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return false;
        }
    }

    private boolean checkReadPermission()
    {
        if(mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            return false;
        }

    }

    private void pickCamera()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Pic title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Pic description");

        avatarImage = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, avatarImage);
        cameraLauncher.launch(camera);
    }


    private void pickStorage()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        galleryLauncher.launch(gallery);
    }



    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK)
                    {
                            mFirebaseMethods.uploadProfilePicture(avatarImage);

                    }
                }
            }
    );

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK)
                    {
                        avatarImage = result.getData().getData();
                        mFirebaseMethods.uploadProfilePicture(avatarImage);

                    }
                }
            }
    );
}