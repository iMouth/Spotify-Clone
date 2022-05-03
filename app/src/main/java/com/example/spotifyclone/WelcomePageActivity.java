/*
 * WelcomePageActivity.java
 * @author: Daniel and Kelvin
 *
 * This program sets up the welcome page activity. The activity starts by getting the token
 * for making Spotify API calls from the user, setting up the remote for spotify, and setting
 * up the Player Object user throughout the application. The user can start by pressing the start
 * exploring button.
 */

package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Displays welcome page for application and gets the token to make API calls and sets up
 * remote for spotify
 */
public class WelcomePageActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "eb56514e9b9f49a3addef0fb8da5d279";
    private static final String REDIRECT_URI = "http://com.example.spotifyclone/callback";
    private static SpotifyAppRemote remote;
    private static String token;
    private static final int REQUEST_CODE = 1337;


    /**
     * Creates view for activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
    }

    /**
     * Sets functionality for welcome page
     */
    @Override
    protected void onStart() {
        super.onStart();
        moveLogo();
        setToken();
        setRemote();
        Button btn = findViewById(R.id.startExploringButton);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.GONE);
                Player player = new Player(remote, token);
                HomePageFragment homePageFragment = new HomePageFragment();
                Bundle args = new Bundle();
                args.putSerializable("player", player);
                homePageFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.welcome_page_layout, homePageFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    /**
     * connects spotify to remote
     */
    public void setRemote() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        // Sets up remote controller for spotify
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        remote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    /**
     * Gets token for API calls by displaying auth to the user
     */
    public void setToken() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    /**
     * Gets the token user to send API calls to Spotify API.
     * @param requestCode 1337 representing a API call to get a token
     * @param resultCode code representing what the auth returned
     * @param intent Intent for authorization
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    token = response.getAccessToken();
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d("Auth", "Error Auth");
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d("Auth", "Canceled Auth");
            }
        }
    }

    /**
     * Moves spotify logo around the home page
     */
    public void moveLogo() {
        ImageView spotifyLogo = findViewById(R.id.spotifyLogo);
        Path path = new Path();
        RectF circle = new RectF(-200f, -100f, 200f, 300f);
        path.arcTo(circle, 0, 180);
        path.arcTo(circle, 180, 180);
        final PathMeasure pathMeasure = new PathMeasure(path, false);
        ValueAnimator animator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        final float[] pos = new float[2];
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                pathMeasure.getPosTan(v, pos, null);
                spotifyLogo.setTranslationX(pos[0]);
                spotifyLogo.setTranslationY(pos[1]);
            }
        });
        animator.setDuration(3000);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
}