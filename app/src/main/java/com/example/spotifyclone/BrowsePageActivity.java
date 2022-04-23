package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class BrowsePageActivity extends AppCompatActivity {

    private static List<String> genres = new ArrayList<>();
    private static String token;
    private static Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_page);
        setGenres();
        setButtons();
        Bundle extras = getIntent().getExtras();
        player = (Player) extras.get("player");
        token = player.getToken();
    }

    public void setButtons() {
        Collections.shuffle(genres);
        Button genre1 = findViewById(R.id.genre1);
        Button genre2 = findViewById(R.id.genre2);
        Button genre3 = findViewById(R.id.genre3);
        Button genre4 = findViewById(R.id.genre4);
        genre1.setText(genres.get(0));
        genre2.setText(genres.get(1));
        genre3.setText(genres.get(2));
        genre4.setText(genres.get(3));
        buttonClick(genre1);
        buttonClick(genre2);
        buttonClick(genre3);
        buttonClick(genre4);
    }

    public void randomize(View view) {
        Collections.shuffle(genres);
        Button genre1 = findViewById(R.id.genre1);
        Button genre2 = findViewById(R.id.genre2);
        Button genre3 = findViewById(R.id.genre3);
        Button genre4 = findViewById(R.id.genre4);
        genre1.setText(genres.get(0));
        genre2.setText(genres.get(1));
        genre3.setText(genres.get(2));
        genre4.setText(genres.get(3));
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
        findViewById(R.id.genre1).setVisibility(View.GONE);
        findViewById(R.id.genre2).setVisibility(View.GONE);
        findViewById(R.id.genre3).setVisibility(View.GONE);
        findViewById(R.id.genre4).setVisibility(View.GONE);
        findViewById(R.id.randomize).setVisibility(View.GONE);

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
        startActivity(homePageIntent);
    }

    public void openBrowsePage(View v) {
        Intent browsePageIntent = new Intent(this, BrowsePageActivity.class);
        startActivity(browsePageIntent);
    }

    public void openSearchPage(View v) {
        Intent searchPageIntent = new Intent(this, SearchPageActivity.class);
        startActivity(searchPageIntent);
    }

    public void openMyLibraryPage(View v) {
        Intent myLibraryPageIntent = new Intent(this, MyLibraryPageActivity.class);
        startActivity(myLibraryPageIntent);
    }

    public void openNowPlayingPage(View v) {
        Intent nowPlayingPageIntent = new Intent(this, NowPlayingPageActivity.class);
        startActivity(nowPlayingPageIntent);
    }

}