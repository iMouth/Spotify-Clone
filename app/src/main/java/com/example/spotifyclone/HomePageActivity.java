package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        TextView greetingMessageView = (TextView) findViewById(R.id.greetingMessage);
        greetingMessageView.setText(setGreetingMessage());


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
        startActivity(helperPageIntent);
    }

    public void openHomePage(View v) {
        Intent homePageIntent = new Intent(this, HomePageActivity.class);
        startActivity(homePageIntent);
    }

    public void openBrowsePage(View v) {
        Intent browsePageIntent = new Intent(this, BrowsePageActivity.class);
        startActivity(browsePageIntent);
    }

    public void openSearchPage(View v) {
        Intent searchPageIntent = new Intent(this, SearchPageActivity.class);
        startActivity(searchPageIntent);
    }

    public void openMyLibraryPage(View v) {
        Intent myLibraryPageIntent = new Intent(this, MyLibraryPageActivity.class);
        startActivity(myLibraryPageIntent);
    }

    public void openNowPlayingPage(View v) {
        Intent nowPlayingPageIntent = new Intent(this, NowPlayingPageActivity.class);
        startActivity(nowPlayingPageIntent);
    }
}