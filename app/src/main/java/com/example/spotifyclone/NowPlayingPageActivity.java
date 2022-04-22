package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NowPlayingPageActivity extends AppCompatActivity {

    int playPauseCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing_page);
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