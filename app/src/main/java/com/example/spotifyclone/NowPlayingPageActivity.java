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
    int playPauseCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing_page);
        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");
        TextView songNameTextView = findViewById(R.id.songName);
        songNameTextView.setText(player.getCurrentSongName());
        String name = player.getCurrentSongName();
        HashMap<String, String> info = player.getInfo(name);
        System.out.println("info is: " + info.get("artist"));
        TextView artistNameTextView = findViewById(R.id.artistName);
        artistNameTextView.setText(info.get("artist"));
        ImageView albumCoverImageView = findViewById(R.id.albumCoverImage);
        Picasso.get().load(info.get("picture")).into(albumCoverImageView);

    }

    public void changeIcon(View v) {
        Button playPauseButton = findViewById(R.id.playPauseSongButton);
        switch (playPauseCounter) {
            case (0):
                playPauseButton.setBackgroundResource(R.drawable.pause_icon);
                playPauseCounter = 1;
                break;
            case (1):
                playPauseButton.setBackgroundResource(R.drawable.play_icon);
                playPauseCounter = 0;
                break;
        }
    }

    public void goBack(View v) {
        this.finish();
    }
}