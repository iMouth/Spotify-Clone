/*
 * SearchPageFragment.java
 * @author: Daniel and Kelvin
 *
 * This program sets up the fragment for the search feature of the application. The user can type
 * a name of a song or artist and click the song or artist button and the program makes an API
 * call with the spotify web API to get 50 songs of whatever the user typed in and displays them.
 * The user can then click on the songs and it will start playing as well as add it to the library
 * of songs in the users playlist.
 *
 */

package com.example.spotifyclone;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Search page fragment that allows the user to search for songs based on the song name
 * or artist name
 */
public class SearchPageFragment extends Fragment {

    private static String choice = "artist";
    private static List<String> songListURI = new ArrayList<String>();
    private static List<String> songListName = new ArrayList<String>();
    private static String search;
    private static String token;
    private static Player player;
    private static HashMap<String, HashMap<String, String>> songMap = new HashMap<>();
    private static View view;

    /**
     * Required empty public constructor
     */
    public SearchPageFragment() { }

    /**
     * Creates the view for the search page fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();
        view = inflater.inflate(R.layout.fragment_search_page, container, false);

        Bundle bundle = this.getArguments();
        player = (Player) bundle.get("player");
        player.act = view;
        player.setNowPlaying();
        token = player.getToken();
        setButtons();
        return view;
    }

    /**
     * Sets all the buttons the user can click on with appropriate functionality
     */
    public void setButtons() {
        Button songBtn = view.findViewById(R.id.song);
        Button artistBtn = view.findViewById(R.id.artist);
        Button home = view.findViewById(R.id.homePageButton);
        Button browse = view.findViewById(R.id.browsePageButton);
        Button search = view.findViewById(R.id.searchPageButton);
        Button library = view.findViewById(R.id.myLibraryButton);
        Button playPause = view.findViewById(R.id.playPauseSongButton);
        LinearLayout ll = view.findViewById(R.id.nowPlayingText);

        setSearchButtons(songBtn, artistBtn, "song");
        setSearchButtons(artistBtn, songBtn, "artist");
        setOnClick(home, new HomePageFragment());
        setOnClick(browse, new BrowsePageFragment());
        setOnClick(search, new SearchPageFragment());
        setOnClick(library, new MyLibraryPageFragment());
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openNowPlayingPage(v); }});
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePlay(playPause); }});
        if (player.playing) changePlay(playPause);
    }

    /**
     * Change what the play button looks like
     * @param button play pause button for now playing section
     */
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

    /**
     * Sets the Artist and Song Button clicks when the user searches for a song.
     * Starts a thread to get the JSON Object with the songs and displays them
     * @param change Button to change to green signing clicked
     * @param reset Button to change to white signifying not clicked
     * @param choiceUser either artist or song based on what user clicked
     */
    private void setSearchButtons(Button change, Button reset, String choiceUser) {
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
                    if (songObj != null) getActivity().runOnUiThread(() -> updateUI(songObj));
                });
                t.start();
            }
        });
    }

    /**
     * Sets the search url given the users choice whether is is artist of song
     */
    public void setSearch() {
        EditText edit = getActivity().findViewById(R.id.searchText);
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

    /**
     * send API call to Spotify API with the given search URL
     * @return JSON Object from API call
     */
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

    /**
     * Parses the JSON object to the the song name, uri, picture, and artist of all the songs
     * in the object
     * @param songObj JSON Object from Spotify API call
     */
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
        setList();
    }

    /**
     * Sets the list of songs to the adapter. If a song is clicked it adds it to the
     * playlist and starts playing it.
     */
    public void setList() {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(getContext(), R.layout.song_row, songListName);
        ListView lw = getActivity().findViewById(R.id.listOfSongs);
        lw.setAdapter(itemsAdapter);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = songListName.get(position);
                player.addSong(name, songMap.get(name));
                player.playSong(name);
            }
        });
        itemsAdapter.notifyDataSetChanged();
    }

    /**
     * Sets an on click listener to replace the search fragment with
     * @param btn Button to set Click Listener to
     * @param frag Fragment to replace search page layout with
     */
    public void setOnClick(Button btn, Fragment frag) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable("player", player);
                frag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.search_page_layout, frag)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * Launches now playing fragment
     * @param v View
     */
    public void openNowPlayingPage(View v) {
        NowPlayingPageFragment nowPlayingPageFragment = new NowPlayingPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        nowPlayingPageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_page_layout, nowPlayingPageFragment)
                .addToBackStack(null)
                .commit();
    }
}