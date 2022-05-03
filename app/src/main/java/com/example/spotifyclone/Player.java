/*
 * Player.java
 * @author: Daniel and Kelvin
 *
 * This program sets up the player class for the application. This class is used to hold all the
 * song information such as song name, song uri, song artist, and song picture. You can add and
 * remove a song, play, pause, or resume a song, set up now playing segment of the application,
 * shuffle song playlist, get songs and song info, get current song, and play next or previous song.
 *
 */

package com.example.spotifyclone;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Sets up the Player class that holds the songs and song information. It also plays and stops
 * songs from playing.
 */
public class Player implements Serializable {
    private static SpotifyAppRemote remote;
    protected static TreeMap<String, HashMap<String, String>> songs = new TreeMap<>();
    protected static ArrayList<String> songList = new ArrayList<>();
    private static String token;
    protected static View act;
    private static String curSong;
    protected boolean playing = false;

    /**
     * Constructor for player class
     * @param remote Spotify remote to play/pause songs
     * @param token for making Spotify API calls
     */
    public Player(SpotifyAppRemote remote, String token) {
        this.remote = remote;
        this.token = token;

    }

    /**
     * gets token for Spotify API calls
     * @return String of token
     */
    public String getToken() {
        return this.token;
    }

    /***
     * Adds songs to playlist
     * @param name of of song to add
     * @param info uri artist and picture of song
     */
    public void addSong(String name, HashMap info) {
        songs.put(name, info);
        songList.add(name);
        playSong(name);
        Button btn = act.findViewById(R.id.playPauseSongButton);
        if ( btn.getText().equals("play")) {
            btn.setText("pause");
            btn.setBackgroundResource(R.drawable.pause_icon);
        }
        if (!songs.isEmpty() && act.findViewById(R.id.nowPlayingText) != null) {
            act.findViewById(R.id.nowPlayingText).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Gets list of songs
     * @return list of songs
     */
    public ArrayList<String> getSongs() { return songList; }

    /**
     * removes song from playlist
     * @param name name of song to remove
     */
    public void removeSong(String name) {
        songList.remove(name);
        songs.remove(name);
        if (songList.isEmpty()) {
            pause();
            Button btn = act.findViewById(R.id.playPauseSongButton);
            if ( btn.getText().equals("pause")) {
                btn.setText("play");
                btn.setBackgroundResource(R.drawable.play_icon);
            }
        } else if (curSong == name) {
            curSong = songList.get(0);
            playSong(curSong);
        }
        if (songs.isEmpty() &&  act.findViewById(R.id.nowPlayingText) != null) {
            act.findViewById(R.id.nowPlayingText).setVisibility(View.GONE);
            act.findViewById(R.id.playPauseSongButton).setVisibility(View.GONE);
            act.findViewById(R.id.albumCoverImage).setVisibility(View.GONE);
        }
    }

    /**
     * Pauses current song
     */
    public void pause() {
        remote.getPlayerApi().pause();
        playing = false;
    }

    /**
     * Resumes current song
     */
    public void resume() {
        remote.getPlayerApi().resume();
        playing = true;
    }

    /**
     * Goes to previous song in playlist
     */
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

    /**
     * Goes to next song in playlist
     */
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

    /**
     * Play song
     * @param name name of song to get played
     */
    public void playSong(String name) {
        playing = true;
        curSong = name;
        remote.getPlayerApi().play(songs.get(name).get("uri"));
        setNowPlaying();
    }

    /**
     * Gets and returns current song name
     * @return String of name of current song playing
     */
    public String getCurrentSongName() {
        return curSong;
    }

    /**
     * Gets the info for a current song name the info being uri artist and picture
     * @param name Name of song to get info from
     * @return Hashmap of info of song
     */
    public HashMap<String, String> getInfo(String name) {
        return songs.get(name);
    }

    /**
     * Shuffle splaylist
     */
    public void shuffleSongs() {
        Collections.shuffle(songList);
    }

    /**
     * Sets up now playing for the view
     */
    public void setNowPlaying() {
        if (songs.isEmpty() && act.findViewById(R.id.nowPlayingText) != null) {
            act.findViewById(R.id.nowPlayingText).setVisibility(View.GONE);
            act.findViewById(R.id.playPauseSongButton).setVisibility(View.GONE);
        } else if (!songs.isEmpty() && act.findViewById(R.id.nowPlayingText) != null ) {
            act.findViewById(R.id.playPauseSongButton).setVisibility(View.VISIBLE);
            act.findViewById(R.id.nowPlayingText).setVisibility(View.VISIBLE);
            act.findViewById(R.id.playPauseSongButton).setVisibility(View.VISIBLE);
        }
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
