package com.example.spotifyclone;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class SearchPageFragment extends Fragment {

    private static String choice = "artist";
    private static List<String> songListURI = new ArrayList<String>();
    private static List<String> songListName = new ArrayList<String>();
    private static String search;
    private static String token;
    private static Player player;
    private static HashMap<String, HashMap<String, String>> songMap = new HashMap<>();

    public SearchPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_page, container, false);

        Button songBtn = getActivity().findViewById(R.id.song);
        Button artistBtn = getActivity().findViewById(R.id.artist);
        setButtons(songBtn, artistBtn, "song");
        setButtons(artistBtn, songBtn, "artist");
        Bundle bundle = this.getArguments();
        player = (Player) bundle.get("player");
        player.act = this;
        player.setNowPlaying();
        token = player.getToken();
        Button playPause = getActivity().findViewById(R.id.playPauseSongButton);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePlay(playPause); }});
        if (player.playing) changePlay(playPause);

        return view;
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
                    getActivity().runOnUiThread(() -> updateUI(songObj));
                });
                t.start();
            }
        });
    }

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
                new ArrayAdapter<String>(getContext(), R.layout.song_row, songListName);
        ListView lw = getActivity().findViewById(R.id.listOfSongs);
        lw.setAdapter(itemsAdapter);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = songListName.get(position);
                player.addSong(name, songMap.get(name));
                //player.playSong(name);
                TextView songNameTextView = getActivity().findViewById(R.id.songName);
                TextView artistNameTextView = getActivity().findViewById(R.id.artistName);
                ImageView albumCoverImageView = getActivity().findViewById(R.id.albumCoverImage);
                songNameTextView.setText(name);
                artistNameTextView.setText(songMap.get(name).get("artist"));
                System.out.println(songMap.get(name).get("picture"));
                Picasso.get().load(songMap.get(name).get("picture")).into(albumCoverImageView);
            }
        });
        itemsAdapter.notifyDataSetChanged();
    }

    public void openHomePage(View v) {
        HomePageFragment homePageFragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        homePageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_page_layout, homePageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openBrowsePage(View v) {
        BrowsePageFragment browsePageFragment = new BrowsePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        browsePageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_page_layout, browsePageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openSearchPage(View v) {
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        searchPageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_page_layout, searchPageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openMyLibraryPage(View v) {
        MyLibraryPageFragment myLibraryPageFragment = new MyLibraryPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        myLibraryPageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_page_layout, myLibraryPageFragment)
                .addToBackStack(null)
                .commit();
    }

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