package com.iamdsr.travel.AppLaunchSetup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.iamdsr.travel.R;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendOTPBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        setupWidgets();
        sendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PhoneLoginActivity.this, OtpActivity.class));
            }
        });
    }

    private void setupWidgets() {
        sendOTPBtn = findViewById(R.id.send_otp);
    }
}