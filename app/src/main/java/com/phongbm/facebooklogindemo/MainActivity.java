package com.phongbm.facebooklogindemo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.phongbm.common.CommonValue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CallbackManager callbackManager;
    private LoginButton btnLoginWithFacebook;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.printKeyHash();
        this.initializeComponent();

        this.setupAccessTokenTracker();
        this.setupProfileTracker();
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    private void initializeComponent() {
        callbackManager = CallbackManager.Factory.create();

        btnLoginWithFacebook = (LoginButton) findViewById(R.id.btnLoginWithFacebook);
        btnLoginWithFacebook.setReadPermissions("user_friends");
        btnLoginWithFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess...");
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    Log.i(TAG, "ID: " + profile.getId());
                    Log.i(TAG, "FirstName: " + profile.getFirstName());
                    Log.i(TAG, "MiddleName: " + profile.getMiddleName());
                    Log.i(TAG, "LastName: " + profile.getLastName());
                    Log.i(TAG, "Name: " + profile.getName());
                    Log.i(TAG, "Uri: " + profile.getLinkUri());
                    Log.i(TAG, "ProfilePictureUri: " + profile.getProfilePictureUri(300, 300));
                } else {
                    Log.i(TAG, "onSuccess... Null Profile");
                }
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel...");
            }

            @Override
            public void onError(FacebookException e) {
                Log.i(TAG, "onError...");
                e.printStackTrace();
            }
        });
    }

    private void printKeyHash() {
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(
                    CommonValue.PACKAGE_NAME, PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.i("KeyHash", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void setupAccessTokenTracker() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    Log.i(TAG, "CurrentAccessToken: " + currentAccessToken.toString());
                } else {
                    Log.i(TAG, "Null CurrentAccessToken");
                }
            }
        };
    }

    private void setupProfileTracker() {
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    Log.i(TAG, "CurrentProfile: " + currentProfile.getName());
                } else {
                    Log.i(TAG, "Null CurrentProfile");
                }
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

}