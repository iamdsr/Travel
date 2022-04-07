package com.iamdsr.travel.AppLaunchSetup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.iamdsr.travel.AppLaunchSetup.AuthUtils.EmailPasswordAuth;
import com.iamdsr.travel.R;

public class LoginActivity extends AppCompatActivity {

    //Widgets variables
    private TextView toSignUpActivity;
    private Button phoneNumberVerification;
    private TextInputEditText mUserEmail, mUserPassword;
    private Button mLoginBtn;
    //Firebase
    private FirebaseAuth mAuth;
    //Utility Variables
    private static final String TAG = "LoginActivity";
    private EmailPasswordAuth emailPasswordAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpWidgets();
        setUpFirebase();
        toSignUpActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
        phoneNumberVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PhoneLoginActivity.class));
                finish();
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = mUserEmail.getText().toString().trim();
                String userPassword = mUserPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)){
                    ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Signing in...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    emailPasswordAuth.loginWithEmailAndPassword(userEmail, userPassword, progressDialog, mUserPassword);
                }
                else {

                }
            }
        });
    }

    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        emailPasswordAuth = new EmailPasswordAuth(this,mAuth);
    }
    private void setUpWidgets(){
        toSignUpActivity = findViewById(R.id.to_sign_up_activity);
        phoneNumberVerification = findViewById(R.id.otp_sign_in);
        mUserEmail = findViewById(R.id.user_email);
        mUserPassword = findViewById(R.id.user_password);
        mLoginBtn = findViewById(R.id.login_button);
    }
}