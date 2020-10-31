package com.matthewthung.flixter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.matthewthung.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

    // CONSTANTS:
    private static final String YOUTUBE_API_KEY = "AIzaSyD-D3gaS9_asXbKmGtjaNiEieqAVce8wLA";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final int UNI_FIRE = 0x1F525;   // The unicode value for fire emoji. Used to help display popularity details.
    public static final int UNI_MEDAL = 0x1F396;  // The unicode value for medal emoji. Used to help display popularity details.
    public static final int UNI_EYE = 0x1F611;  // The unicode value for eye in speech bubble emoji. Used to help display popularity details.
    public static final int UNI_WORRY = 0x1F61F;  // The unicode value for worried face emoji. Used to help display popularity details.


    TextView tvTitle;
    TextView tvOverview;
    TextView tvPopularity;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvPopularity = findViewById(R.id.tvPopularity);
        ratingBar = findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getRating());
        tvPopularity.setText("Popularity: ");

        // Figure out what popularity text to add:
        if ((float)movie.getPopularity() >= 1000.0) {
            tvPopularity.append(getEmoji(UNI_FIRE) + "HOT" + getEmoji(UNI_FIRE));
        } else if ((float)movie.getPopularity() >= 750) {
            tvPopularity.append(getEmoji(UNI_MEDAL) + "Noteworthy" + getEmoji(UNI_MEDAL));
        } else if ((float)movie.getPopularity() >= 350) {
            tvPopularity.append(getEmoji(UNI_EYE) + "Average" + getEmoji(UNI_EYE));
        } else {
            tvPopularity.append(getEmoji(UNI_WORRY) + "Unpopular" + getEmoji(UNI_WORRY));
        }

        // For transition element (to ignore UI):
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0) {
                        return;
                    }
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    Log.d("DetailActivity", youtubeKey);
                    initializeYoutube(youtubeKey);
                } catch (JSONException e) {
                    Log.e("DetailActivity", "Failed to parse JSON", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });

    }

    private void initializeYoutube(final String youtubeKey) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity", "onSuccess");
                youTubePlayer.cueVideo(youtubeKey);

                // If the video have greater than 5 star rating, automatically play.
                if (ratingBar.getRating() > 5.0){
                    youTubePlayer.loadVideo(youtubeKey);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    public String getEmoji (int uniCode) {
        return new String(Character.toChars(uniCode));
    }
}