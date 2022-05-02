//package com.example.spotifyclone;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.spotify.android.appremote.api.ConnectionParams;
//import com.spotify.android.appremote.api.Connector;
//import com.spotify.android.appremote.api.SpotifyAppRemote;
//import com.spotify.sdk.android.auth.AuthorizationClient;
//import com.spotify.sdk.android.auth.AuthorizationRequest;
//import com.spotify.sdk.android.auth.AuthorizationResponse;
//
//import java.util.Calendar;
//
//public class HomePageActivity extends AppCompatActivity {
//
//    private static Player player;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_page);
//        TextView greetingMessageView = (TextView) findViewById(R.id.greetingMessage);
//        greetingMessageView.setText(setGreetingMessage());
//        Bundle extras = getIntent().getExtras();
//        player = (Player) extras.get("player");
//        player.act = this;
//        player.setNowPlaying();
//        Button playPause = findViewById(R.id.playPauseSongButton);
//        playPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { changePlay(playPause); }});
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
//    public String setGreetingMessage() {
//        Calendar calendar = Calendar.getInstance();
//        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
//        String message = "";
//        if (timeOfDay < 6) {
//            message = "Good Night!";
//        }
//        if (6 <= timeOfDay && timeOfDay < 12) {
//            message = "Good Morning!";
//        }
//        if (12 <= timeOfDay && timeOfDay < 18) {
//            message = "Good Afternoon!";
//        }
//        if (18 <= timeOfDay && timeOfDay < 24) {
//            message = "Good Evening!";
//        }
//        return message;
//    }
//
//    public void openHelperPage(View v) {
//        Intent helperPageIntent = new Intent(this, HelperPageActivity.class);
//        helperPageIntent.putExtra("player", player);
//        startActivity(helperPageIntent);
//    }
//
//    public void openHomePage(View v) {
//        HomePageFragment homePageFragment = new HomePageFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("player", player);
//        homePageFragment.setArguments(args);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.home_page_layout, homePageFragment)
//                .addToBackStack(null)
//                .commit();
//    }
//
//    public void openBrowsePage(View v) {
//        BrowsePageFragment browsePageFragment = new BrowsePageFragment();
//                Bundle args = new Bundle();
//                args.putSerializable("player", player);
//                browsePageFragment.setArguments(args);
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.home_page_layout, browsePageFragment)
//                        .addToBackStack(null)
//                        .commit();
//    }
//
//    public void openSearchPage(View v) {
//        SearchPageFragment searchPageFragment = new SearchPageFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("player", player);
//        searchPageFragment.setArguments(args);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.home_page_layout, searchPageFragment)
//                .addToBackStack(null)
//                .commit();
//    }
//
//    public void openMyLibraryPage(View v) {
//        MyLibraryPageFragment myLibraryPageFragment = new MyLibraryPageFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("player", player);
//        myLibraryPageFragment.setArguments(args);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.home_page_layout, myLibraryPageFragment)
//                .addToBackStack(null)
//                .commit();
//    }
//
//    public void openNowPlayingPage(View v) {
//        NowPlayingPageFragment nowPlayingPageFragment = new NowPlayingPageFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("player", player);
//        nowPlayingPageFragment.setArguments(args);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.home_page_layout, nowPlayingPageFragment)
//                .addToBackStack(null)
//                .commit();
//    }
//}