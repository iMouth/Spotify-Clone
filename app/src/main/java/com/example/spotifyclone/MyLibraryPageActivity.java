//package com.example.spotifyclone;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.FileProvider;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.ContactsContract;
//import android.text.Layout;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.spotify.android.appremote.api.ConnectionParams;
//import com.spotify.android.appremote.api.Connector;
//import com.spotify.android.appremote.api.SpotifyAppRemote;
//
//import java.io.File;
//import java.util.ArrayList;
//
//public class MyLibraryPageActivity extends AppCompatActivity {
//
//    private static Player player;
//    private static ArrayList<String> contacts = new ArrayList<String>();
//    private static ArrayList<String> songs = new ArrayList<String>();;
//    private static ArrayAdapter<String> contactsAdapter = null;
//    private static ArrayAdapter<String> songsAdapter = null;
//    private static Activity act;
//    private static int change = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        act = this;
//        setContentView(R.layout.activity_my_library_page);
//        Bundle extras = getIntent().getExtras();
//        player = (Player) extras.get("player");
//        player.act = this;
//        player.setNowPlaying();
//        getContacts();
//        songs = player.getSongs();
//        setupAdapter(findViewById(R.id.contacts), contacts, contactsAdapter);
//        setupAdapter(findViewById(R.id.librarySongs), songs, songsAdapter);
//        Button playPause = findViewById(R.id.playPauseSongButton);
//        playPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { changePlay(playPause); }});
//        setButtons();
//        if (player.playing) changePlay(playPause);
//    }
//
//    public void changePlay(Button button) {
//        if (!player.songList.isEmpty()) {
//            if (button.getText().equals("play")) {
//                button.setText("pause");
//                button.setBackgroundResource(R.drawable.pause_icon);
//                player.resume();
//            } else {
//                button.setText("play");
//                button.setBackgroundResource(R.drawable.play_icon);
//                player.pause();
//            }
//            button.setTextScaleX(0);
//        }
//    }
//
//    public void setButtons() {
//        Button shuffle = findViewById(R.id.shufflePlay);
//        Button share = findViewById(R.id.shareBtn);
//        ListView lvContacts = findViewById(R.id.contacts);
//        Button back = findViewById(R.id.libraryBack);
//        ListView lvSongs = findViewById(R.id.librarySongs);
//        Button edit = findViewById(R.id.editSongs);
//        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String songName= (String) parent.getItemAtPosition(position);
//                player.setNowPlaying();
//                player.playSong(songName);}});
//        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                onShareClick(position+1); }});
//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { changeView(); }});
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { changeView(); }});
//        shuffle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                player.shuffleSongs();
//                songs = player.getSongs();
//                System.out.println(songsAdapter);
//                setupAdapter(findViewById(R.id.librarySongs), songs, songsAdapter);
//            }});
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { setEdit(edit); }});
//    }
//
//    public void setEdit(Button edit) {
//        if (change == 1) {
//            change = 0;
//            edit.setBackgroundTintList(getResources().getColorStateList(R.color.white));
//            ListView lvSongs = findViewById(R.id.librarySongs);
//            lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String songName= (String) parent.getItemAtPosition(position);
//                    player.setNowPlaying();
//                    player.playSong(songName);}});
//
//        } else {
//            change = 1;
//            edit.setBackgroundTintList(getResources().getColorStateList(R.color.red));
//            ListView lvSongs = findViewById(R.id.librarySongs);
//            lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String songName= (String) parent.getItemAtPosition(position);
//                    player.setNowPlaying();
//                    player.removeSong(songName);
//                    setupAdapter(findViewById(R.id.librarySongs), songs, songsAdapter);
//                }});
//        }
//    }
//
//    public void changeView() {
//        int vis = findViewById(R.id.libraryBack).getVisibility();
//        if (View.VISIBLE == vis){
//            findViewById(R.id.libraryBack).setVisibility(View.GONE);
//            findViewById(R.id.shareBtn).setVisibility(View.VISIBLE);
//            findViewById(R.id.shufflePlay).setVisibility(View.VISIBLE);
//            findViewById(R.id.librarySongs).setVisibility(View.VISIBLE);
//            findViewById(R.id.contacts).setVisibility(View.GONE);
//            findViewById(R.id.editSongs).setVisibility(View.VISIBLE);
//        } else {
//            findViewById(R.id.shareBtn).setVisibility(View.GONE);
//            findViewById(R.id.shufflePlay).setVisibility(View.GONE);
//            findViewById(R.id.libraryBack).setVisibility(View.VISIBLE);
//            findViewById(R.id.librarySongs).setVisibility(View.GONE);
//            findViewById(R.id.contacts).setVisibility(View.VISIBLE);
//            findViewById(R.id.editSongs).setVisibility(View.GONE);
//        }
//    }
//
//    private void setupAdapter(ListView lv, ArrayList<String> array, ArrayAdapter<String> adapter) {
//        adapter = new ArrayAdapter<String>(this, R.layout.song_row, R.id.text1, array);
//        lv.setAdapter(adapter);
//    }
//
//    @SuppressLint("Range")
//    public void onShareClick(int position) {
//        Cursor emails = this.getContentResolver().query(
//                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                ContactsContract.CommonDataKinds.Email.CONTACT_ID+" = " + position,
//                null, null);
//        String email   = "";
//        Cursor cursorEmail      = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                null,
//                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                new String[] {String.valueOf(position)},
//                null);
//        if(cursorEmail.moveToFirst()) {
//            email = cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
//        }
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("vnd.android.cursor.dir/email");
//        StringBuilder sb = new StringBuilder();
//        for (String s : songs) {
//            sb.append(s);
//            sb.append("\n");
//        }
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Spotify Playlist");
//        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
//        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {email});
//        startActivity(intent);
//    }
//
//    public void getContacts() {
//        Cursor cursor = getContentResolver().query(
//                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//        while (cursor.moveToNext()) {
//            @SuppressLint("Range") String id = cursor.getString(
//                    cursor.getColumnIndex(ContactsContract.Contacts._ID));
//            @SuppressLint("Range") String given = cursor.getString(
//                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//            contacts.add(given + " :: " + id);
//        }
//        cursor.close();
//    }
//
//    public void openHomePage(View v) {
//        Intent homePageIntent = new Intent(this, HomePageActivity.class);
//        homePageIntent.putExtra("player", player);
//        startActivity(homePageIntent);
//    }
//
//    public void openBrowsePage(View v) {
//        Intent browsePageIntent = new Intent(this, BrowsePageActivity.class);
//        browsePageIntent.putExtra("player", player);
//        startActivity(browsePageIntent);
//    }
//
//    public void openSearchPage(View v) {
//        Intent searchPageIntent = new Intent(this, SearchPageActivity.class);
//        searchPageIntent.putExtra("player", player);
//        startActivity(searchPageIntent);
//    }
//
//    public void openMyLibraryPage(View v) {
//        Intent myLibraryPageIntent = new Intent(this, MyLibraryPageActivity.class);
//        myLibraryPageIntent.putExtra("player", player);
//        startActivity(myLibraryPageIntent);
//    }
//
//    public void openNowPlayingPage(View v) {
//        Intent nowPlayingPageIntent = new Intent(this, NowPlayingPageActivity.class);
//        nowPlayingPageIntent.putExtra("player", player);
//        startActivity(nowPlayingPageIntent);
//    }
//}