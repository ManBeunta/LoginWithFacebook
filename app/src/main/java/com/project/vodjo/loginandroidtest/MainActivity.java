package com.project.vodjo.loginandroidtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private LoginButton btnLogin;
    private CallbackManager callbackManager;
    private CircleImageView profilePictureView;
    private LinearLayout infoLayout;
    private TextView email;
    private TextView gender;
    private TextView facebookName;
    private TextView phone;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        btnLogin = (LoginButton) findViewById(R.id.login_button);
        email = (TextView) findViewById(R.id.email);
        facebookName = (TextView) findViewById(R.id.name);
        //phone = (TextView) findViewById(R.id.nomorhape);
        gender = (TextView) findViewById(R.id.gender);
        infoLayout = (LinearLayout) findViewById(R.id.layout_info);
        profilePictureView = (CircleImageView) findViewById(R.id.image);

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        btnLogin.setReadPermissions("public_profile", "email");
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {
                Bundle bundle = new Bundle();
                bundle.putString("fields", "name,email,gender,cover,picture.width(200).height(200)");
                new GraphRequest(loginResult.getAccessToken(), "me", bundle, HttpMethod.GET, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            setProfileToView(response.getJSONObject());
                        }
                    }
                }).executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login Cancle", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, "error to Login Facebook", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setProfileToView(JSONObject jsonObject) {
        try {
            if (jsonObject.has("email"))
                email.setText(jsonObject.getString("email"));
            if (jsonObject.has("gender"))
                gender.setText(jsonObject.getString("gender"));
            if (jsonObject.has("name"))
                facebookName.setText(jsonObject.getString("name"));
            /*if (jsonObject.has("phone_number"))
                phone.setText(jsonObject.getString("phone_number"));*/
            Picasso.with(getApplicationContext())
                    .load(Profile.getCurrentProfile().getProfilePictureUri(100,100).toString())
                    .into(profilePictureView);
            infoLayout.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}