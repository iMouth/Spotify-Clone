package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SearchPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
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