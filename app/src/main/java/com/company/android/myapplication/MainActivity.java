package com.company.android.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ActivityNavigator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.company.android.myapplication.Login.LoginActivity;
import com.company.android.myapplication.Registration.RegistrationActivity;
import com.company.android.myapplication.navigation.BottomNavigationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    private Button registration, login;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
      //  isUserSignedIn();



        registration = findViewById(R.id.registartion_button);
        login = findViewById(R.id.login_button);

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void isUserSignedIn() {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mFirebaseUser != null)
        {

            if(!mFirebaseUser.isEmailVerified()) {
                Toast.makeText(mContext, "Please verify your email", Toast.LENGTH_SHORT).show();
            }else
            {


                //ActivityNavigator activityNavigator = new ActivityNavigator(this);
                //activityNavigator.navigate(activityNavigator.createDestination().setIntent(this, BottomNavigationActivity.class)), null, null, null);

                //FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                //startActivity(new Intent(MainActivity.this, BottomNavigationActivity.class));
                //finish();
            }

        }

        /*if (mFirebaseUser != null) {

            FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        }*/
    }

}