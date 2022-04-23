package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.Calendar;

public class HomePageActivity extends AppCompatActivity {

    private static Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        TextView greetingMessageView = (TextView) findViewById(R.id.greetingMessage);
        greetingMessageView.setText(setGreetingMessage());
        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");
    }


    public String setGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String message = "";
        if (timeOfDay < 6) {
            message = "Good Night!";
        }
        if (6 <= timeOfDay && timeOfDay < 12) {
            message = "Good Morning!";
        }
        if (12 <= timeOfDay && timeOfDay < 18) {
            message = "Good Afternoon!";
        }
        if (18 <= timeOfDay && timeOfDay < 24) {
            message = "Good Evening!";
        }
        return message;
    }

    public void openHelperPage(View v) {
        Intent helperPageIntent = new Intent(this, HelperPageActivity.class);
        helperPageIntent.putExtra("player", player);
        startActivity(helperPageIntent);
    }

    public void openHomePage(View v) {
        Intent homePageIntent = new Intent(this, HomePageActivity.class);
        homePageIntent.putExtra("player", player);
        startActivity(homePageIntent);
    }

    public void openBrowsePage(View v) {
        Intent browsePageIntent = new Intent(this, BrowsePageActivity.class);
        browsePageIntent.putExtra("player", player);
        startActivity(browsePageIntent);
    }

    public void openSearchPage(View v) {
        Intent searchPageIntent = new Intent(this, SearchPageActivity.class);
        searchPageIntent.putExtra("player", player);
        startActivity(searchPageIntent);
    }

    public void openMyLibraryPage(View v) {
        Intent myLibraryPageIntent = new Intent(this, MyLibraryPageActivity.class);
        myLibraryPageIntent.putExtra("player", player);
        startActivity(myLibraryPageIntent);
    }

    public void openNowPlayingPage(View v) {
        Intent nowPlayingPageIntent = new Intent(this, NowPlayingPageActivity.class);
        nowPlayingPageIntent.putExtra("player", player);
        startActivity(nowPlayingPageIntent);
    }
}