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
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SearchPageActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "eb56514e9b9f49a3addef0fb8da5d279";
    private static final String CLIENT_SECRET = "b68c98da213a4cebb4ea7e18bab55bc4";
    private static final String REDIRECT_URI = "http://com.example.spotifyclone/callback";
    private static String choice = "artist";
    private static SpotifyAppRemote remote;
    private static List<String> songListURI = new ArrayList<String>();
    private static List<String> songListName = new ArrayList<String>();
    private static String search;
    private static final int REQUEST_CODE = 1337;
    private static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        Button songBtn = findViewById(R.id.song);
        Button artistBtn = findViewById(R.id.artist);
        setButtons(songBtn, artistBtn, "song");
        setButtons(artistBtn, songBtn, "artist");

    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        // Sets up remote controller for spotify
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        remote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        //connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });

        // Gets token for API Calls
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    token = response.getAccessToken();
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d("Auth", "Error Auth");
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d("Auth", "Canceled Auth");
            }
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
                String uri = trackObj.getString("uri");
                String name = trackObj.getString("name");
                songListURI.add(uri);
                songListName.add(name);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, R.layout.song_row, songListName);
        ListView lw = findViewById(R.id.songs);
        lw.setAdapter(itemsAdapter);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                remote.getPlayerApi().play(songListURI.get(position));
            }
        });
        itemsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(remote);
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