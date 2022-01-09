package com.company.android.myapplication.Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.android.myapplication.Firebase.FirebaseMethods;
import com.company.android.myapplication.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private String emailStr, passwordStr;
    private TextView forgotPassorword;
    private ImageView back;

    private FirebaseMethods mFirebaseMethods;
    private Context mContext;

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        mFirebaseMethods = new FirebaseMethods(mContext);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        email = findViewById(R.id.email_loginEt);
        password = findViewById(R.id.password_loginEt);
        login = findViewById(R.id.loginBut);
        forgotPassorword = findViewById(R.id.forgot_password);
        back = findViewById(R.id.back_button_login);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailStr = email.getText().toString().trim();
                passwordStr = password.getText().toString().trim();

                if(!checkEmail() || !checkPassword())
                {
                    return;
                }else
                {
                    mFirebaseMethods.login(emailStr, passwordStr);
                    finish();
                }
            }
        });

        forgotPassorword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View dialogLayout = getLayoutInflater().inflate(R.layout.popup_dialog, null);
        builder.setView(dialogLayout);

        builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText email = dialogLayout.findViewById(R.id.email_dialogEt);
                emailStr = email.getText().toString().trim();
                mFirebaseMethods.recoverPassword(emailStr);
            }
        });


        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private boolean checkEmail()
    {
        if(emailStr.isEmpty())
        {
            email.setError("Empty field");
            email.setFocusable(true);
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches())
        {
            email.setError("This email doesn't exist");
            email.setFocusable(true);
            return false;
        }

        return true;
    }

    private boolean checkPassword()
    {
        if(passwordStr.isEmpty())
        {
            password.setError("Empty field");
            password.setFocusable(true);
            return false;
        }

        return true;
    }
}