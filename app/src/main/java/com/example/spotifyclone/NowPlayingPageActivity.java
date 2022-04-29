package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class NowPlayingPageActivity extends AppCompatActivity {

    private static Player player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing_page);
        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");
        player.act = this;
        player.setNowPlaying();
        TextView songNameTextView = findViewById(R.id.songName);
        songNameTextView.setText(player.getCurrentSongName());
        String name = player.getCurrentSongName();
        HashMap<String, String> info = player.getInfo(name);
        System.out.println("info is: " + info.get("artist"));
        TextView artistNameTextView = findViewById(R.id.artistName);
        artistNameTextView.setText(info.get("artist"));
        ImageView albumCoverImageView = findViewById(R.id.albumCoverImage);
        Picasso.get().load(info.get("picture")).into(albumCoverImageView);
        Button playPause = findViewById(R.id.playPauseSongButton);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePlay(playPause); }});
        if (player.playing) changePlay(playPause);
        Button prev = findViewById(R.id.prevSongButton);
        Button next = findViewById(R.id.nextSongButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { player.next(); }});
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { player.previous(); }});
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
        this.finish();
    }
}