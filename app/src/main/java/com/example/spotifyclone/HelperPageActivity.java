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
        HomePageFragment homePageFragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        homePageFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.helper_page_layout, homePageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openBrowsePage(View v) {
        BrowsePageFragment browsePageFragment = new BrowsePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        browsePageFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.helper_page_layout, browsePageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openSearchPage(View v) {
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        searchPageFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.helper_page_layout, searchPageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openMyLibraryPage(View v) {
        MyLibraryPageFragment myLibraryPageFragment = new MyLibraryPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        myLibraryPageFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.helper_page_layout, myLibraryPageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openNowPlayingPage(View v) {
        NowPlayingPageFragment nowPlayingPageFragment = new NowPlayingPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        nowPlayingPageFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.helper_page_layout, nowPlayingPageFragment)
                .addToBackStack(null)
                .commit();
    }
}