package com.company.android.myapplication.Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.company.android.myapplication.Firebase.FirebaseMethods;
import com.company.android.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt, loginEt, passwordEt2;
    private Button regBut;
    private RadioButton maleRadio, femaleRadio;
    private ProgressBar mProgressBar;
    private FirebaseMethods mFirebaseMethods;
    private String   gender;
    private Context mContext;
    private boolean exists;
    private ImageView back;

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mContext = RegistrationActivity.this;
        mFirebaseMethods = new FirebaseMethods(mContext);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        passwordEt2 = findViewById(R.id.passwordEt2);
        loginEt = findViewById(R.id.loginEt);
        regBut = findViewById(R.id.regbutton);
        mProgressBar = findViewById(R.id.progressBar);
        maleRadio = findViewById(R.id.maleButton);
        femaleRadio = findViewById(R.id.femaleButton);
        back = findViewById(R.id.back_button_registration);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        regBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( chooseGender() || checkNickname() || isNicknameValid() || checkEmail() || isPasswordValid()  || usernameExistsOrNot()) {

                    if (passwordMatch()) {
                        mFirebaseMethods.registerNewUser(emailEt.getText().toString(), passwordEt.getText().toString(), gender, loginEt.getText().toString());
                    } else {
                        passwordEt.setError("Incorrect password");
                        passwordEt.setFocusable(true);
                        return;
                    }
                }
            }
        });
    }


    private boolean checkNickname()
    {
        if(loginEt.getText().toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Nickname", Toast.LENGTH_SHORT).show();
            loginEt.setError("Write your nickname");
            loginEt.setFocusable(true);
            return false;
        }
        return true;
    }

    private boolean passwordMatch()
    {
        if(passwordEt2.getText().toString().trim().matches(passwordEt.getText().toString().trim()))
            return true;
        Toast.makeText(mContext, "Password", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean chooseGender()
    {
        if(maleRadio.isChecked())
        {
            gender = "Male";
            return true;
        }

        if(femaleRadio.isChecked())
        {
            gender = "Female";
            return true;
        }else
        {
            Toast.makeText(RegistrationActivity.this, "Select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean checkEmail()
    {
        if(Patterns.EMAIL_ADDRESS.matcher(emailEt.getText().toString()).matches() && (!emailEt.getText().toString().isEmpty()))
            return true;
        else
        {
            emailEt.setError("Incorrect email");
            emailEt.setFocusable(true);
            return false;
        }

    }


    private boolean isPasswordValid()
    {
        Pattern pattern;
        Matcher matcher;

        final String PASS_DEM = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASS_DEM);
        matcher = pattern.matcher(passwordEt.getText().toString().trim());
        if(!matcher.matches()) {
            passwordEt.setError("Password must contain numbers and special characters and big letters");
            passwordEt.setFocusable(true);
            return false;
        }
        else if(passwordEt.getText().toString().trim().isEmpty() || passwordEt.getText().toString().trim().length() < 6)
            return false;

        return true;
    }

    private boolean isNicknameValid()
    {
        String usernick = loginEt.getText().toString();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9-.]+");
        Matcher matcher = pattern.matcher(usernick);

        if(!matcher.matches())
        {
            loginEt.setError("Nickname is not valid");
            loginEt.setFocusable(true);
            return false;
        }

        return true;
    }

    public boolean usernameExistsOrNot()
    {
        String usernick = loginEt.getText().toString();

        Query query = usersRef.orderByChild("nickname").equalTo(usernick);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    loginEt.setError("This username already exists");
                    loginEt.setFocusable(true);
                    exists = true;
                }else
                    exists = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return exists;

    }
}