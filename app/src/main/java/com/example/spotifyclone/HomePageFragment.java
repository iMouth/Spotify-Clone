/*
 * HomePageFragment.java
 * @author: Daniel and Kelvin
 *
 * This program sets up the fragment for the home page fragment for the application. It displays
 * a greeting and lets the user know to click one of the buttons at the bottom to continue.
 * Also has a helper icon to click on to let the user know who to use the application.
 *
 */

package com.example.spotifyclone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Sets up welcome page for home page for the applcation
 */
public class HomePageFragment extends Fragment {

    private static Player player;
    private static View view;
    public Activity containerActivity = null;

    /**
     * Required empty public constructor
     */
    public HomePageFragment() { }

    /**
     * Sets containerAcitvity to this
     * @param containerActivity
     */
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Creates view fore home page fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        container.removeAllViews();
        view = inflater.inflate(R.layout.fragment_home_page, container, false);

        TextView greetingMessageView = view.findViewById(R.id.greetingMessage);
        greetingMessageView.setText(setGreetingMessage());
        setButtons();

        Bundle bundle = this.getArguments();
        player = (Player) bundle.get("player");
        player.act = view;
        player.setNowPlaying();

        return view;
    }

    /**
     * Sets nav buttons the user can click on
     */
    public void setButtons() {
        Button home = view.findViewById(R.id.homePageButton);
        Button browse = view.findViewById(R.id.browsePageButton);
        Button search = view.findViewById(R.id.searchPageButton);
        Button library = view.findViewById(R.id.myLibraryButton);
        setOnClick(home, new HomePageFragment());
        setOnClick(browse, new BrowsePageFragment());
        setOnClick(search, new SearchPageFragment());
        setOnClick(library, new MyLibraryPageFragment());
        LinearLayout ll = view.findViewById(R.id.nowPlayingText);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openNowPlayingPage(v); }});
        Button helper = view.findViewById(R.id.helperButton);
        helper.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) { openHelperPage(v);}
        }));
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
     * Finds greeting to display based on time of day for user
     * @return message of greeting to give user
     */
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

    /**
     * Sets an on click listener to replace the home fragment with
     * @param btn Button to set Click Listener to
     * @param frag Fragment to replace home page layout with
     */
    public void setOnClick(Button btn, Fragment frag) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable("player", player);
                frag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.home_page_layout, frag)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * Opens intent for helper page wish shows user how to use the application
     * @param v View
     */
    public void openHelperPage(View v) {
        Intent helperPageIntent = new Intent(v.getContext(), HelperPageActivity.class);
        helperPageIntent.putExtra("player", player);
        startActivity(helperPageIntent);
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
                .replace(R.id.home_page_layout, nowPlayingPageFragment)
                .addToBackStack(null)
                .commit();
    }
}