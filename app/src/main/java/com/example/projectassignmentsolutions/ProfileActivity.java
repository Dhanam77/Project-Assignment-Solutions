package com.example.projectassignmentsolutions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectassignmentsolutions.HelperClasses.PeopleAPI;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button logoutBtn;
    private TextView userName, userEmail, userId, ageText;
    private ImageView profileImage;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private ProgressBar loadProfile;
    private boolean isGoogleProfile = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.activity_profile);


        InitializeFields();

        if (isGoogleProfile) {
            SetupGoogle();

        }


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGoogleProfile) {
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    if (status.isSuccess()) {
                                        gotoMainActivity();
                                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Session not closed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


    }


    private void SetupGoogle() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(PeopleAPI.BIRTHDAY_SCOPE))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)

                .build();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (isGoogleProfile) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            userName.setText(account.getDisplayName());
            userEmail.setText(account.getEmail());
            loadProfile.setVisibility(View.GONE);
            userId.setVisibility(View.GONE);
            ageText.setVisibility(View.GONE);


            try {
                Glide.with(this).load(account.getPhotoUrl()).into(profileImage);
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "image not found", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void InitializeFields() {
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        userName = (TextView) findViewById(R.id.name);
        userEmail = (TextView) findViewById(R.id.email);
        userId = (TextView) findViewById(R.id.age);
        ageText = (TextView) findViewById(R.id.ageText);
        profileImage = (ImageView) findViewById(R.id.account_image);
        loadProfile = (ProgressBar) findViewById(R.id.load_account_details);

        Bundle inBundle = getIntent().getExtras();

        if (inBundle != null) {
            isGoogleProfile = false;

            String name = inBundle.get("name").toString();
           String imageUrl = inBundle.get("imageUrl").toString();


            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String age = preferences.getString("age", "Hold on! There's something wrong!");
            String email = preferences.getString("email", "Hold on! There's something wrong!");


            userName.setText(name);
           Glide.with(ProfileActivity.this)
                    .load(imageUrl)
                    .into(profileImage);

            userEmail.setText(email);
            userId.setText(age);
            loadProfile.setVisibility(View.INVISIBLE);
        }

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}