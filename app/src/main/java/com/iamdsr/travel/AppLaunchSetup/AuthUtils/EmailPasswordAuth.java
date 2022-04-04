package com.iamdsr.travel.AppLaunchSetup.AuthUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamdsr.travel.ActivityUtils.MainActivity;
import com.iamdsr.travel.AppLaunchSetup.LoginActivity;
import com.iamdsr.travel.models.UserModel;
import com.iamdsr.travel.R;

import java.util.Locale;

public class EmailPasswordAuth {
    private Context context;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPasswordAuth";
    FirebaseFirestore firebaseFirestore;

    public EmailPasswordAuth(){

    }

    public EmailPasswordAuth(Context context, FirebaseAuth mAuth) {
        this.context = context;
        this.mAuth = mAuth;
    }

    public void addUserToDB(String id,String username, String fullName, String email){
        UserModel users = new UserModel(id,fullName, username, email, fullName.toLowerCase(Locale.getDefault()), "");
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .set(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        mAuth.signOut();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error writing document"+e.getLocalizedMessage());
            }
        });
    }

    public void signupWithEmailAndPassword(String email, String password, String username, String fullName, ProgressDialog progressDialog, TextView mUserPassword, TextView mErrorText) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    addUserToDB(user.getUid(), username, fullName, email);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        context.startActivity(new Intent(context, LoginActivity.class));
                                    }
                                }
                            });
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    mErrorText.setText(R.string.auth_failed);
                    mErrorText.setVisibility(View.VISIBLE);
                    mUserPassword.setText(null);
                }
            }
        });
    }

    public void loginWithEmailAndPassword(String email, String password, ProgressDialog progressDialog, TextView mUserPassword, TextView mErrorText){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    progressDialog.dismiss();
                    Log.d(TAG, "onComplete: "+context);
                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    mErrorText.setText(R.string.auth_failed);
                    mErrorText.setVisibility(View.VISIBLE);
                    mUserPassword.setText(null);
                }
            }
        });
    }
}
