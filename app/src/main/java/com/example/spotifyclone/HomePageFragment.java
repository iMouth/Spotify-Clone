package com.example.spotifyclone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;


public class HomePageFragment extends Fragment {

    private static Player player;
    public Activity containerActivity = null;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        TextView greetingMessageView = view.findViewById(R.id.greetingMessage);
        greetingMessageView.setText(setGreetingMessage());
        Bundle bundle = this.getArguments();
        player = (Player) bundle.get("player");
        player.act = this;
        player.setNowPlaying();
        Button playPause = view.findViewById(R.id.playPauseSongButton);
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

    public String setGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String message = "";
        if (timeOfDay < 6) {
            message = "Good Night!";
        }
        if (6 <= timeOfDay && timeOfDay < 12) {
            message = "Good Morning!";
        }
        if (12 <= timeOfDay && timeOfDay < 18) {
            message = "Good Afternoon!";
        }
        if (18 <= timeOfDay && timeOfDay < 24) {
            message = "Good Evening!";
        }
        return message;
    }

    public void openHelperPage(View v) {
        Intent helperPageIntent = new Intent(v.getContext(), HelperPageActivity.class);
        helperPageIntent.putExtra("player", player);
        startActivity(helperPageIntent);
    }

    public void openHomePage(View v) {
        HomePageFragment homePageFragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        homePageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_layout, homePageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openBrowsePage(View v) {
        BrowsePageFragment browsePageFragment = new BrowsePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        browsePageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_layout, browsePageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openSearchPage(View v) {
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        searchPageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_layout, searchPageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openMyLibraryPage(View v) {
        MyLibraryPageFragment myLibraryPageFragment = new MyLibraryPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        myLibraryPageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_layout, myLibraryPageFragment)
                .addToBackStack(null)
                .commit();
    }

    public void openNowPlayingPage(View v) {
        NowPlayingPageFragment nowPlayingPageFragment = new NowPlayingPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        nowPlayingPageFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_layout, nowPlayingPageFragment)
                .addToBackStack(null)
                .commit();
    }
}