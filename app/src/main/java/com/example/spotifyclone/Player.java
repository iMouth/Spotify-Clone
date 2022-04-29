package com.example.spotifyclone;


import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Player implements Serializable {
    private static SpotifyAppRemote remote;
    protected static TreeMap<String, HashMap<String, String>> songs = new TreeMap<>();
    protected static ArrayList<String> songList = new ArrayList<>();
    private static String token;
    protected static Activity act;
    private static String curSong;
    protected boolean playing = false;
    private static int songPos = 0;

    public Player(SpotifyAppRemote remote, String token) {
        this.remote = remote;
        this.token = token;

    }

    public String getToken() {
        return this.token;
    }

    public void addSong(String name, HashMap info) {
        songs.put(name, info);
        songList.add(name);
        playSong(name);
        songPos = songList.size() - 1;
        Button btn = act.findViewById(R.id.playPauseSongButton);
        if ( btn.getText().equals("play")) {
            btn.setText("pause");
            btn.setBackgroundResource(R.drawable.pause_icon);
        }
    }

    public ArrayList<String> getSongs() { return songList; }

    public void removeSong(String name) {
        songList.remove(name);
        songs.remove(name);
        if (songList.isEmpty()) {
            pause();
        } else if (curSong == name) {
            curSong = songList.get(0);
            playSong(curSong);
        }
    }

    public void pause() {
        remote.getPlayerApi().pause();
        playing = false;
    }

    public void resume() {
        remote.getPlayerApi().resume();
        playing = true;
    }

    public void previous() {
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i) == getCurrentSongName()) {
                if (i == 0 ){
                    playSong(songList.get(songList.size()-1));
                } else {
                    playSong(songList.get(i-1));
                }
                break;
            }
        }
    }

    public void next() {
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i) == getCurrentSongName()) {
                if (i == songList.size()-1){
                    playSong(songList.get(0));
                } else {
                    playSong(songList.get(i+1));
                }
                break;
            }
        }
    }


    public void playSong(String name) {
        playing = true;
        curSong = name;
        System.out.println(name);
        System.out.println(songs.get(name));
        remote.getPlayerApi().play(songs.get(name).get("uri"));
        setNowPlaying();
    }

    public String getCurrentSongName() {
        return curSong;
    }

    public HashMap<String, String> getInfo(String name) {
        return songs.get(name);
    }

    public void shuffleSongs() {
        Collections.shuffle(songList);
    }

    public void setNowPlaying() {
        if (act != null && !songs.isEmpty()) {
            TextView songNameTextView = act.findViewById(R.id.songName);
            TextView artistNameTextView = act.findViewById(R.id.artistName);
            ImageView albumCoverImageView = act.findViewById(R.id.albumCoverImage);
            String name = getCurrentSongName();
            if (name != null) {
                songNameTextView.setText(getCurrentSongName());
                artistNameTextView.setText(songs.get(name).get("artist"));
                Picasso.get().load(songs.get(name).get("picture")).into(albumCoverImageView);
            }
        }
    }
}
