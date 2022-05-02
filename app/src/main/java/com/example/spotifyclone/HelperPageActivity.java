package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class HelperPageActivity extends AppCompatActivity {

    private static Player player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_page);

        Button home = findViewById(R.id.homePageButton);
        Button browse = findViewById(R.id.browsePageButton);
        Button search = findViewById(R.id.searchPageButton);
        Button library = findViewById(R.id.myLibraryButton);
        setOnClick(home, new HomePageFragment());
        setOnClick(browse, new BrowsePageFragment());
        setOnClick(search, new SearchPageFragment());
        setOnClick(library, new MyLibraryPageFragment());
        LinearLayout ll = findViewById(R.id.nowPlayingText);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openNowPlayingPage(v); }});

        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");
        player.setNowPlaying();
        Button playPause = findViewById(R.id.playPauseSongButton);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePlay(playPause); }});

        setNowPlaying();
        if (player.playing) changePlay(playPause);
    }

    public void setNowPlaying() {
        TextView songNameTextView = findViewById(R.id.songName);
        TextView artistNameTextView = findViewById(R.id.artistName);
        ImageView albumCoverImageView = findViewById(R.id.albumCoverImage);
        String name = player.getCurrentSongName();
        if (name != null) {
            songNameTextView.setText(player.getCurrentSongName());
            artistNameTextView.setText(player.songs.get(name).get("artist"));
            Picasso.get().load(player.songs.get(name).get("picture")).into(albumCoverImageView);
        }
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


    public void setOnClick(Button btn, Fragment frag) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable("player", player);
                frag.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.helper_page_layout, frag)
                        .addToBackStack(null)
                        .commit();
            }
        });
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