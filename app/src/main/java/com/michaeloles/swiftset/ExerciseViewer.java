package com.michaeloles.swiftset;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
        import com.google.android.youtube.player.YouTubeInitializationResult;
        import com.google.android.youtube.player.YouTubePlayer;
        import com.google.android.youtube.player.YouTubePlayer.Provider;
        import com.google.android.youtube.player.YouTubePlayerView;

import java.text.NumberFormat;
import java.text.ParseException;

public class ExerciseViewer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private YoutubeData ytData;
    private static String selectedExercise = "";
    private static final String fullUrl = "youtube.com/watch?v=";
    private static final String shortUrl = "youtu.be/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_viewer);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.setVisibility(View.GONE);

        //Get Url From Calling Activity
        Bundle extras = getIntent().getExtras();
        selectedExercise = extras.getString("selected_exercise");
        ExerciseDb remaining = MainActivity.getRemainingDb();
        if(remaining==null) {
            startActivity(new Intent(this, MainActivity.class));
        }else {
            String selectedUrl = remaining.getUrlFromExerciseId(Integer.parseInt(selectedExercise));

            ytData = parseYoutubeUrl(selectedUrl);

            TextView t = (TextView) findViewById(R.id.exerciseTitle);
            t.setText(ExerciseDb.getNameFromExerciseId(selectedExercise));
        }
    }

    //Finds the start time and video code of a youtube video and returns it as a YoutubeData class
    public static YoutubeData parseYoutubeUrl(String selectedUrl) {
        //separate the youtube video code and time from the url
        String youtubeCode = "", videoCode;
        int startTimeMillis = 0;

        if (selectedUrl.toLowerCase().contains(fullUrl)) {//different depending on youtube.com and youtu.be urls
            youtubeCode = selectedUrl.substring(selectedUrl.lastIndexOf(fullUrl) + fullUrl.length());
        } else if (selectedUrl.toLowerCase().contains(shortUrl)) {
            youtubeCode = selectedUrl.substring(selectedUrl.lastIndexOf(shortUrl) + shortUrl.length());
        } else {
            //TODO handle badUrl
        }

        //Find the video code from the url
        //Ex: Url = https://www.youtube.com/watch?v=D5d_rkxPfuE&t=1m4s videoCode = D5d_rkxPfuE
        int endOfVideoCode = findFirstSeperator(youtubeCode);
        videoCode = youtubeCode.substring(0, endOfVideoCode);
        //If a specific time is designated in the video set the start time in milliseconds
        if ( (youtubeCode.contains("&t=") || youtubeCode.contains("?t=")) && (endOfVideoCode < youtubeCode.length()) ) {
            //removes the video code from the youtubeCode
            youtubeCode = youtubeCode.substring(endOfVideoCode + 1, youtubeCode.length());
            //Timecode is everything after t=
            String timecode = "";
            if(youtubeCode.contains("&t=")){
                timecode = youtubeCode.substring(youtubeCode.indexOf("&t=") + 3, youtubeCode.length());
            }else{
                timecode = youtubeCode.substring(youtubeCode.indexOf("?t=") + 3, youtubeCode.length());
            }
            //and everything before the first seperator
            timecode = timecode.substring(0, findFirstSeperator(timecode));
            //Ex: t=1m5s&index=2&list=WL&index=3 -> 1m5s
            if (!timecode.contains("m") && !timecode.contains("s")) {//timecode is just listed as an interger of seconds
                try {
                    startTimeMillis += startTimeMillis += NumberFormat.getInstance().parse(timecode).intValue() * 1000;
                } catch (ParseException e) {
                    //TODO Bad URl found here too
                }
            }

            if (timecode.contains("m")) {//m in url d
                String[] parts = timecode.split("m");
                startTimeMillis = Integer.parseInt(parts[0]) * 60000;//Convert the url minutes time to milliseconds
                if(parts.length>1) {
                    timecode = parts[1];
                }
            }

            if (timecode.contains("s")) {
                try {
                    startTimeMillis += NumberFormat.getInstance().parse(timecode).intValue() * 1000;
                } catch (ParseException e) {
                    //TODO badURL found here too
                }
            }
        }

        return new YoutubeData(videoCode,startTimeMillis);
    }

    //Finds the index of the first location of a seperator character in the URL
    //Returns the end of the string if none are found
    private static int findFirstSeperator(String s) {
        int endOfVideoCode = s.length();
        if (s.contains("&")) {
            endOfVideoCode = Math.min(endOfVideoCode, s.indexOf("&"));
        }
        if (s.contains("?")) {
            endOfVideoCode = Math.min(endOfVideoCode, s.indexOf("?"));
        }
        if (s.contains("\n")) {
            endOfVideoCode = Math.min(endOfVideoCode, s.indexOf("\n"));
        }
        //Code is after url and before the first seperator character (?/&)
        return endOfVideoCode;
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(ytData.videoCode, ytData.startTimeMillis);
            player.play();
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    //Saves current exercise to current workout and displays the number of exercises now in the workout
    public void saveExercise(View view) {
        int numExercises = SavedExercises.addExercise(selectedExercise, this);
        Toast.makeText(this, "Saved! (" + numExercises + " exercises in current workout)", Toast.LENGTH_SHORT).show();
    }

    //When the user clicks on the button to show the video, this hides that button and loads the video
    //Prevents wasting data by loading unwanted videos, stops music from being interrupted
    public void showVideo(View view) {
        Button showVideoButton = (Button) findViewById(R.id.view_exercise_video);
        showVideoButton.setVisibility(View.GONE);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.setVisibility(View.VISIBLE);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}

