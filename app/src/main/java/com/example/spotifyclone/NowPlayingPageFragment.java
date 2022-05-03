/*
 * NowPlayingPageFragment.java
 * @author: Daniel and Kelvin
 *
 * This program sets up the fragment for the now playing feature of the application. The user will
 * be shown the current song playing with the artist name song name and picture of the song. The
 * user will be able to go to the next song or go to the previous song as well as pause/play the
 * current song. The user can exit this with a down arrow button and will go back to the home page.
 *
 */

package com.example.spotifyclone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Sets up the now playing page of the application that displays the current song playing
 * and allows the user to go to previous song and next song as well as pause/play the current song.
 */
public class NowPlayingPageFragment extends Fragment {

    private static Player player;
    private static View view;

    /**
     * Required empty public constructor
     */
    public NowPlayingPageFragment() { }

    /**
     * Creates the Now playing fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        container.removeAllViews();
        view = inflater.inflate(R.layout.fragment_now_playing_page, container, false);

        Bundle bundle = this.getArguments();
        player = (Player) bundle.get("player");
        player.act = view;
        player.setNowPlaying();
        setButtons();
        return view;
    }

    /**
     * Sets button functionality for buttons the user can press
     */
    public void setButtons() {
        Button playPause = view.findViewById(R.id.playPauseSongButton);
        Button back = view.findViewById(R.id.smallBackArrow);
        Button prev =view.findViewById(R.id.prevSongButton);
        Button next = view.findViewById(R.id.nextSongButton);

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePlay(playPause); }});
        if (player.playing) changePlay(playPause);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { player.next(); }});
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { player.previous(); }});
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { goBack(v); }});
    }

    /**
     * Sets the now playing section of the application with the current song playing
     */
    public void setNowPlaying() {
        TextView songNameTextView = view.findViewById(R.id.songName);
        songNameTextView.setText(player.getCurrentSongName());
        String name = player.getCurrentSongName();
        HashMap<String, String> info = player.getInfo(name);
        TextView artistNameTextView = view.findViewById(R.id.artistName);
        artistNameTextView.setText(info.get("artist"));
        ImageView albumCoverImageView = view.findViewById(R.id.albumCoverImage);
        Picasso.get().load(info.get("picture")).into(albumCoverImageView);
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
     * Goes back to the home page fragment of the application
     * @param v view
     */
    public void goBack(View v) {
        player.setNowPlaying();
        HomePageFragment frag = new HomePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        frag.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.now_playing_page_layout, frag)
                .addToBackStack(null)
                .commit();
    }
}