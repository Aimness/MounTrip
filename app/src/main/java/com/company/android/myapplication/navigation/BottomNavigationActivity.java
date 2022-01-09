package com.company.android.myapplication.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.company.android.myapplication.MainActivity;
import com.company.android.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class BottomNavigationActivity extends AppCompatActivity{
    private FirebaseAuth mFirebaseAuth;
    private BottomNavigationView mBottomNavigationView;
    private Context mContext;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private  NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mContext = getApplicationContext();

        isUserSignedIn();
        navController = Navigation.findNavController(this, R.id.frame_container);
        mBottomNavigationView = findViewById(R.id.bottomNavigation);

        hasPermissions();





        NavigationUI.setupWithNavController(mBottomNavigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.home_menu || destination.getId() == R.id.profile_menu
                    || destination.getId() == R.id.notification_menu || destination.getId() == R.id.search_menu)
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                else
                    mBottomNavigationView.setVisibility(View.GONE);
            }
        });


    }

    private void isUserSignedIn() {
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {

            FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + mFirebaseAuth.getCurrentUser().getUid());
            if(!mFirebaseUser.isEmailVerified()) {
                Toast.makeText(mContext, "Please verify your email", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(BottomNavigationActivity.this, MainActivity.class));
                finish();
            }

            if(!hasPermissions())
            {
                ActivityCompat.requestPermissions(this, permissions, 400);
            }

        } else {
            startActivity(new Intent(BottomNavigationActivity.this, MainActivity.class));
            finish();
        }
    }

    private boolean hasPermissions()
    {
       for(String permission : permissions)
       {
           if(ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED)
               return false;
       }

       return true;
    }


}