package com.iamdsr.travel.AppLaunchSetup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iamdsr.travel.ActivityUtils.MainActivity;
import com.iamdsr.travel.AppLaunchSetup.AuthUtils.EmailPasswordAuth;
import com.iamdsr.travel.R;

public class SignUpActivity extends AppCompatActivity {

    //Widgets variables
    private TextView toLoginActivity, mErrorText;
    private ImageButton phoneNumberVerification;
    private Button mSignUpBtn;
    private EditText mUserEmail, mUserPassword, mUsername, mFullName;
    //Firebase
    private FirebaseAuth mAuth;
    //Utility Variables
    private static final String TAG = "SignUpActivity";
    private EmailPasswordAuth emailPasswordAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setUpWidgets();
        setUpFirebase();
        toLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
        phoneNumberVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, PhoneLoginActivity.class));
                finish();
            }
        });
        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = mUserEmail.getText().toString().trim();
                String userPassword = mUserPassword.getText().toString().trim();
                String username = mUsername.getText().toString().trim();
                String fullName = mFullName.getText().toString().trim();
                if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(fullName)){
                    mErrorText.setVisibility(View.INVISIBLE);
                    ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setMessage("Signing in...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    emailPasswordAuth.signupWithEmailAndPassword(userEmail, userPassword, username, fullName, progressDialog, mErrorText, mUserPassword);
                }
                else {
                    mErrorText.setVisibility(View.VISIBLE);
                    mErrorText.setText(R.string.error_text_empty_fields);
                }
            }
        });
    }

    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        emailPasswordAuth = new EmailPasswordAuth(this,mAuth);
    }
    private void setUpWidgets(){
        toLoginActivity = findViewById(R.id.login);
        phoneNumberVerification = findViewById(R.id.otp_sign_in);
        mUserEmail = findViewById(R.id.user_email);
        mUserPassword = findViewById(R.id.user_password);
        mUsername = findViewById(R.id.user_name);
        mFullName = findViewById(R.id.user_full_name);
        mSignUpBtn = findViewById(R.id.sign_up_btn);
        mErrorText = findViewById(R.id.error_text);
    }
}