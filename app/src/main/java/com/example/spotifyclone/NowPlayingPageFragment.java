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

public class NowPlayingPageFragment extends Fragment {

    private static Player player;

    public NowPlayingPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing_page, container, false);

        Bundle bundle = this.getArguments();
        player = (Player) bundle.get("player");
        player.act = this;
        player.setNowPlaying();
        TextView songNameTextView = getActivity().findViewById(R.id.songName);
        songNameTextView.setText(player.getCurrentSongName());
        String name = player.getCurrentSongName();
        HashMap<String, String> info = player.getInfo(name);
        System.out.println("info is: " + info.get("artist"));
        TextView artistNameTextView = getActivity().findViewById(R.id.artistName);
        artistNameTextView.setText(info.get("artist"));
        ImageView albumCoverImageView = getActivity().findViewById(R.id.albumCoverImage);
        Picasso.get().load(info.get("picture")).into(albumCoverImageView);
        Button playPause = getActivity().findViewById(R.id.playPauseSongButton);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePlay(playPause); }});
        if (player.playing) changePlay(playPause);
        Button prev = getActivity().findViewById(R.id.prevSongButton);
        Button next = getActivity().findViewById(R.id.nextSongButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { player.next(); }});
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { player.previous(); }});
        return view;
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

    public void goBack(View v) {
        player.setNowPlaying();
        getActivity().finish();
    }
}