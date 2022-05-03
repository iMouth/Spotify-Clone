/*
 * HelperPageActivity.java
 * @author: Daniel and Kelvin
 *
 * This program sets up the activity for the helper page activity. This activity just
 * shows the user how they can navigate and user the application.
 *
 */

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

/**
 * Displays text to let the user know how to use the application
 */
public class HelperPageActivity extends AppCompatActivity {

    private static Player player;

    /**
     * Sets the view for the helper page activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_page);

        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");

        setButtons();
        if (!player.songs.isEmpty()) {
            findViewById(R.id.nowPlayingText).setVisibility(View.VISIBLE);
            findViewById(R.id.playPauseSongButton).setVisibility(View.VISIBLE);
            setNowPlaying();
        } else {
            findViewById(R.id.nowPlayingText).setVisibility(View.GONE);
            findViewById(R.id.playPauseSongButton).setVisibility(View.GONE);
        }
    }

    /**
     * Sets nav buttons the user can click on
     */
    public void setButtons() {
        Button home = findViewById(R.id.homePageButton);
        Button browse = findViewById(R.id.browsePageButton);
        Button search = findViewById(R.id.searchPageButton);
        Button library = findViewById(R.id.myLibraryButton);
        Button playPause = findViewById(R.id.playPauseSongButton);

        setOnClick(home, new HomePageFragment());
        setOnClick(browse, new BrowsePageFragment());
        setOnClick(search, new SearchPageFragment());
        setOnClick(library, new MyLibraryPageFragment());
        LinearLayout ll = findViewById(R.id.nowPlayingText);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openNowPlayingPage(v); }});
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePlay(playPause); }});
        if (player.playing) changePlay(playPause);

    }

    /**
     * Sets the now playing section of the application with the current song playing
     */
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

    /**
     * Change what the play button looks like
     * @param button play pause button for now playing section
     */
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

    /**
     * Sets an on click listener to replace the search fragment with
     * @param btn Button to set Click Listener to
     * @param frag Fragment to replace search page layout with
     */
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

    /**
     * Launches now playing fragment
     * @param v View
     */
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