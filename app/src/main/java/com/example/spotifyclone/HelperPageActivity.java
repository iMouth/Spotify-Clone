package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelperPageActivity extends AppCompatActivity {

    private static Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_page);
        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");
        player.act = this;
        player.setNowPlaying();
        Button playPause = findViewById(R.id.playPauseSongButton);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePlay(playPause); }});
        if (player.playing) changePlay(playPause);
    }

    public void changePlay(Button button) {
        if (!player.songList.isEmpty()) {
            if (button.getText().equals("play")) {
                button.setText("pause");
                button.setBackgroundResource(R.drawable.pause_icon);
                player.resume();
            } else {
                button.setText("play");
                button.setBackgroundResource(R.drawable.play_icon);
                player.pause();
            }
            button.setTextScaleX(0);
        }
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