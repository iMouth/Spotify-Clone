package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class BrowsePageActivity extends AppCompatActivity {

    private static List<String> genres = new ArrayList<>();
    private static List<String> colors = new ArrayList<>();
    private static String token;
    private static Player player;
    private static List<String> songListName = new ArrayList<String>();
    private static HashMap<String, HashMap<String, String>> songMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_page);
        setGenres();
        setColors();
        setButtons();
        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");
        token = player.getToken();
        Button random = findViewById(R.id.randomize);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { randomize(); }});
        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeDisplay(); }});
    }

    public void setButtons() {
        randomize();
        buttonClick(findViewById(R.id.genre1));
        buttonClick(findViewById(R.id.genre2));
        buttonClick(findViewById(R.id.genre3));
        buttonClick(findViewById(R.id.genre4));
    }

    public void randomize() {
        Collections.shuffle(genres);
        Collections.shuffle(colors);
        Button genre1 = findViewById(R.id.genre1);
        Button genre2 = findViewById(R.id.genre2);
        Button genre3 = findViewById(R.id.genre3);
        Button genre4 = findViewById(R.id.genre4);
        genre1.setText(genres.get(0));
        genre2.setText(genres.get(1));
        genre3.setText(genres.get(2));
        genre4.setText(genres.get(3));
        genre1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors.get(0))));
        genre2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors.get(1))));
        genre3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors.get(2))));
        genre4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors.get(3))));
    }

    public void buttonClick(Button genre) {
        genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genreName = genre.getText().toString();
                String search = setSearch(genreName);
                Thread t = new Thread(() -> {
                    JSONObject songObj = getSongs(search);
                    runOnUiThread(() -> updateUI(songObj));
                });
                t.start();
            }
        });
    }

    public String setSearch(String genreName) {
        Random random = new Random();
        int offset = random.nextInt(1000);
        String search = "https://api.spotify.com/v1/search?q=genre%3A" + genreName  +
                "&type=track&market=NA&limit=50&offset=" + offset;
        return search;
    }

    public void updateUI(JSONObject songObj) {
        try {
            songListName.clear();
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
        changeDisplay();
    }

    public void setList(JSONObject songObj) {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, R.layout.song_row, songListName);
        ListView lw = findViewById(R.id.browseSongs);
        lw.setAdapter(itemsAdapter);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = songListName.get(position);
                player.addSong(name, songMap.get(name));
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

    public void changeDisplay() {
        int visible1;
        int visible2;
        if (findViewById(R.id.genre1).getVisibility() == View.GONE) {
            visible1 = View.VISIBLE;
            visible2 = View.GONE;
        } else {
            visible1 = View.GONE;
            visible2 = View.VISIBLE;
        }
        findViewById(R.id.genre1).setVisibility(visible1);
        findViewById(R.id.genre2).setVisibility(visible1);
        findViewById(R.id.genre3).setVisibility(visible1);
        findViewById(R.id.genre4).setVisibility(visible1);
        findViewById(R.id.randomize).setVisibility(visible1);
        findViewById(R.id.back).setVisibility(visible2);
        findViewById(R.id.browseSongs).setVisibility(visible2);
    }


    public void setGenres() {
        try {
            InputStream f = getApplicationContext().getAssets().open("genres.txt");
            BufferedReader read = new BufferedReader(new InputStreamReader(f));
            String line = read.readLine();
            while (line != null) {
                genres.add(line);
                line = read.readLine();
            }
            read.close();
        } catch (FileNotFoundException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setColors() {
        try {
            InputStream f = getApplicationContext().getAssets().open("colors.txt");
            BufferedReader read = new BufferedReader(new InputStreamReader(f));
            String line = read.readLine();
            while (line != null) {
                String[] getHex = line.split("#");
                String hex = getHex[1];
                colors.add("#" + hex);
                line = read.readLine();
            }
            read.close();
        } catch (FileNotFoundException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getSongs(String search) {
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