/*
 * MyLibraryPageFragment.java
 * @author: Daniel and Kelvin
 *
 * This program sets up the fragment for the library fragment of the application. This fragment
 * shows the current list of songs the user has added from the playlist from the browse or search
 *fragment. The user can shuffle this playlist. The user can also share this playlist with someone
 * from their contact. The User can also remove songs from the playlist by clicking edit and
 * clicking a song they wish to remove. The user can play a song by clicking on it when not in
 * edit mode. Edit mode is in red, white otherwise.
 *
 */

package com.example.spotifyclone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Allows the user to see what songs are in their playlist. The user can click the songs from their
 * playlist or shuffle them. The user can also edit the songs in their playlist. The user can
 * also share the playlist with someone from their contacts.
 */
public class MyLibraryPageFragment extends Fragment {

    private static Player player;
    private static ArrayList<String> contacts = new ArrayList<String>();
    private static ArrayList<String> songs = new ArrayList<String>();;
    private static ArrayAdapter<String> contactsAdapter = null;
    private static ArrayAdapter<String> songsAdapter = null;
    private static int change = 0;
    private static View view;

    public MyLibraryPageFragment() {
        // Required empty public constructor
    }

    /**
     * Creates view for library fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        container.clearDisappearingChildren();
        view = inflater.inflate(R.layout.fragment_my_library_page, container, false);

        Bundle bundle = this.getArguments();
        player = (Player) bundle.get("player");
        player.act = view;
        player.setNowPlaying();

        getContacts();
        songs = player.getSongs();
        setupAdapter(view.findViewById(R.id.contacts), contacts, contactsAdapter);
        setupAdapter(view.findViewById(R.id.librarySongs), songs, songsAdapter);
        setFragButtons();
        setUserButtons();

        return view;
    }

    /**
     * Sets the buttons used for navigation to different fragments
     */
    public void setFragButtons() {
        Button home = view.findViewById(R.id.homePageButton);
        Button browse = view.findViewById(R.id.browsePageButton);
        Button search = view.findViewById(R.id.searchPageButton);
        Button library = view.findViewById(R.id.myLibraryButton);
        Button playPause = view.findViewById(R.id.playPauseSongButton);

        setOnClick(home, new HomePageFragment());
        setOnClick(browse, new BrowsePageFragment());
        setOnClick(search, new SearchPageFragment());
        setOnClick(library, new MyLibraryPageFragment());
        LinearLayout ll = view.findViewById(R.id.nowPlayingText);
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
     * Sets the buttons the user can click on on this fragment excluding nav buttons
     */
    public void setUserButtons() {
        Button shuffle = view.findViewById(R.id.shufflePlay);
        Button share = view.findViewById(R.id.shareBtn);
        ListView lvContacts = view.findViewById(R.id.contacts);
        Button back = view.findViewById(R.id.libraryBack);
        ListView lvSongs = view.findViewById(R.id.librarySongs);
        Button edit = view.findViewById(R.id.editSongs);
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName= (String) parent.getItemAtPosition(position);
                player.setNowPlaying();
                player.playSong(songName);}});
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onShareClick(position+1); }});
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeView(); }});
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeView(); }});
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.shuffleSongs();
                songs = player.getSongs();
                System.out.println(songsAdapter);
                setupAdapter(getActivity().findViewById(R.id.librarySongs), songs, songsAdapter);
            }});
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setEdit(edit); }});
    }

    /**
     * Sets the edit button from editing clicking to delete songs or clicking to play songs
     * @param edit Edit button
     */
    public void setEdit(Button edit) {
        if (change == 1) {
            change = 0;
            edit.setBackgroundTintList(getResources().getColorStateList(R.color.white));
            ListView lvSongs = getActivity().findViewById(R.id.librarySongs);
            lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String songName= (String) parent.getItemAtPosition(position);
                    player.setNowPlaying();
                    player.playSong(songName);}});

        } else {
            change = 1;
            edit.setBackgroundTintList(getResources().getColorStateList(R.color.red));
            ListView lvSongs = getActivity().findViewById(R.id.librarySongs);
            lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String songName= (String) parent.getItemAtPosition(position);
                    player.setNowPlaying();
                    player.removeSong(songName);
                    setupAdapter(getActivity().findViewById(R.id.librarySongs), songs, songsAdapter);
                }});
        }
    }

    /**
     * Changes the view from list of songs to contacts or vice versa
     */
    public void changeView() {
        int vis = getActivity().findViewById(R.id.libraryBack).getVisibility();
        if (View.VISIBLE == vis){
            getActivity().findViewById(R.id.libraryBack).setVisibility(View.GONE);
            getActivity().findViewById(R.id.shareBtn).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.shufflePlay).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.librarySongs).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.contacts).setVisibility(View.GONE);
            getActivity().findViewById(R.id.editSongs).setVisibility(View.VISIBLE);
        } else {
            getActivity().findViewById(R.id.shareBtn).setVisibility(View.GONE);
            getActivity().findViewById(R.id.shufflePlay).setVisibility(View.GONE);
            getActivity().findViewById(R.id.libraryBack).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.librarySongs).setVisibility(View.GONE);
            getActivity().findViewById(R.id.contacts).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.editSongs).setVisibility(View.GONE);
        }
    }

    /**
     * Sets up adapter for a listview to display either contacts of songs
     * @param lv listview of what items to be displaying
     * @param array Array that holds the info in the listview
     * @param adapter arrayadapter for displaying content of listview
     */
    private void setupAdapter(ListView lv, ArrayList<String> array, ArrayAdapter<String> adapter) {
        adapter = new ArrayAdapter<String>(getContext(), R.layout.song_row, R.id.text1, array);
        lv.setAdapter(adapter);
    }

    /**
     * Finds the email that the user clicked on and starts an intent to that email
     * @param position of email in list
     */
    @SuppressLint("Range")
    public void onShareClick(int position) {
        String email   = "";
        Cursor cursorEmail      = getActivity().getContentResolver()
                .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[] {String.valueOf(position)},
                null);
        if(cursorEmail.moveToFirst()) {
            email = cursorEmail.getString(cursorEmail.
                    getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
        }
        startIntent(email);
    }

    /**
     * Starts an email intent to a given email with the entire playlist song names
     * @param email email to be send to
     */
    public void startIntent(String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("vnd.android.cursor.dir/email");
        StringBuilder sb = new StringBuilder();
        for (String s : songs) {
            sb.append(s);
            sb.append("\n");
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, "Spotify Playlist");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {email});
        startActivity(intent);
    }

    /**
     * Gets the contacts from the users phone and puts it into a Name :: ID format
     */
    public void getContacts() {
        Cursor cursor = getActivity().getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String id = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID));
            @SuppressLint("Range") String given = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contacts.add(given + " :: " + id);
        }
        cursor.close();
    }

    /**
     * Sets an on click listener to replace the library fragment with
     * @param btn Button to set Click Listener to
     * @param frag Fragment to replace library page layout with
     */
    public void setOnClick(Button btn, Fragment frag) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable("player", player);
                frag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.my_library_page_layout, frag)
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
                .replace(R.id.my_library_page_layout, nowPlayingPageFragment)
                .addToBackStack(null)
                .commit();
    }
}