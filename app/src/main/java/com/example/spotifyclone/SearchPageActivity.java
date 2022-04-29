package com.example.spotifyclone;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchPageActivity extends AppCompatActivity {
    private static String choice = "artist";
    private static List<String> songListURI = new ArrayList<String>();
    private static List<String> songListName = new ArrayList<String>();
    private static String search;
    private static String token;
    private static Player player;
    private static HashMap<String, HashMap<String, String>> songMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        Button songBtn = findViewById(R.id.song);
        Button artistBtn = findViewById(R.id.artist);
        setButtons(songBtn, artistBtn, "song");
        setButtons(artistBtn, songBtn, "artist");
        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");
        player.act = this;
        player.setNowPlaying();
        token = player.getToken();
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



    private void setButtons(Button change, Button reset, String choiceUser) {
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = choiceUser;
                change.setBackgroundTintList
                        (ColorStateList.valueOf(getResources().getColor(R.color.green)));
                reset.setBackgroundTintList
                        (ColorStateList.valueOf(getResources().getColor(R.color.white)));
                setSearch();
                Thread t = new Thread(() -> {
                    JSONObject songObj = getSongs();
                    runOnUiThread(() -> updateUI(songObj));
                });
                t.start();
            }
        });
    }

    public void setSearch() {
        EditText edit = findViewById(R.id.searchText);
        String name = edit.getText().toString();
        if (choice.equals("artist")) {
            search = "https://api.spotify.com/v1/search?q=artist%3A" +
                    name + "&type=track&market=NA&limit=50&offset=0";
        } else if (choice.equals("song")) {
            search = "https://api.spotify.com/v1/search?q=" +
                    name + "&type=track&market=NA&limit=50&offset=0";
        } else {
            Log.d("setSearch", "Problem: Choice wasn't artist or song");
        }
    }

    public JSONObject getSongs() {
        try {
            String json = "";
            String line;
            URL object = new URL(search);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + token);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((line = in.readLine()) != null) {
                json += line;
            }
            in.close();
            return new JSONObject(json);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private void updateUI(JSONObject songObj) {
        try {
            songListName.clear();
            songListURI.clear();
            JSONArray items = songObj.getJSONObject("tracks").getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject trackObj = items.getJSONObject(i);
                String name = trackObj.getString("name");
                songListName.add(name);
                String picture = trackObj.getJSONObject("album").getJSONArray("images").getJSONObject(2).getString("url");
                String artist = trackObj.getJSONArray("artists").getJSONObject(0).getString("name");
                String uri = trackObj.getString("uri");
                HashMap<String, String> info = new HashMap<>();
                info.put("uri", uri);
                info.put("picture", picture);
                info.put("artist", artist);
                songMap.put(name, info);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setList(songObj);
    }

    public void setList(JSONObject songObj) {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, R.layout.song_row, songListName);
        ListView lw = findViewById(R.id.listOfSongs);
        lw.setAdapter(itemsAdapter);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = songListName.get(position);
                player.addSong(name, songMap.get(name));
                //player.playSong(name);
                TextView songNameTextView = findViewById(R.id.songName);
                TextView artistNameTextView = findViewById(R.id.artistName);
                ImageView albumCoverImageView = findViewById(R.id.albumCoverImage);
                songNameTextView.setText(name);
                artistNameTextView.setText(songMap.get(name).get("artist"));
                System.out.println(songMap.get(name).get("picture"));
                Picasso.get().load(songMap.get(name).get("picture")).into(albumCoverImageView);
            }
        });
        itemsAdapter.notifyDataSetChanged();
    }

    public void openHomePage(View v) {
        Intent homePageIntent = new Intent(this, HomePageActivity.class);
        homePageIntent.putExtra("player", player);
        startActivity(homePageIntent);
    }

    public void openBrowsePage(View v) {
        Intent browsePageIntent = new Intent(this, BrowsePageActivity.class);
        browsePageIntent.putExtra("player", player);
        startActivity(browsePageIntent);
    }

    public void openSearchPage(View v) {
        Intent searchPageIntent = new Intent(this, SearchPageActivity.class);
        searchPageIntent.putExtra("player", player);
        startActivity(searchPageIntent);
    }

    public void openMyLibraryPage(View v) {
        Intent myLibraryPageIntent = new Intent(this, MyLibraryPageActivity.class);
        myLibraryPageIntent.putExtra("player", player);
        startActivity(myLibraryPageIntent);
    }

    public void openNowPlayingPage(View v) {
        Intent nowPlayingPageIntent = new Intent(this, NowPlayingPageActivity.class);
        nowPlayingPageIntent.putExtra("player", player);
        startActivity(nowPlayingPageIntent);
    }
}