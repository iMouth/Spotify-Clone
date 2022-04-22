package com.example.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class BrowsePageActivity extends AppCompatActivity {

    private static List<String> genres = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_page);
        setGenres();
        setButtons();

    }

    public void setButtons() {
        Button genre1 = findViewById(R.id.genre1);
        Button genre2 = findViewById(R.id.genre2);
        Button genre3 = findViewById(R.id.genre3);
        Button genre4 = findViewById(R.id.genre4);
        Collections.shuffle(genres);
        genre1.setText(genres.get(0));
        genre2.setText(genres.get(1));
        genre3.setText(genres.get(2));
        genre4.setText(genres.get(3));
    }

    public void setGenres() {
        try {
            BufferedReader read = new BufferedReader(new FileReader("genres.txt"));
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