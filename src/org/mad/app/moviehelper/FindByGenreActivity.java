package org.mad.app.moviehelper;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.slidingmenu.lib.SlidingMenu;

/**
 * This class shows a list of genres to choose from and shows movies from that
 * genre
 * 
 */
public class FindByGenreActivity extends SherlockFragmentActivity implements
		YouTubePlayer.OnInitializedListener {

	private SlidingMenu menu;
	private YouTubePlayerSupportFragment youTubePlayerFragment;
	// TODO:change this vidId
	/*
	 * The problem is that we need to initialize the player first, then cue a
	 * video at the same time, but we wont know the video id until we parse some
	 * stuff so its a race condition
	 */
	private String vidID = "txlXcJDtDwM";
	private YouTubePlayer player;
	private MyPlaybackEventListener playbackEventListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Cant do anything w/o internet
		if (isNetworkAvailable()) {
			// Initialize the youtube player. Check the initialize() function to
			// see what this does
			youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
					.findFragmentById(R.id.youtube_fragment);
			youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY,
					FindByGenreActivity.this);
			
			TextView txt = (TextView) findViewById(R.id.info);
			txt.setText("Choose a genre to begin!");

			// configure the SlidingMenu
			menu = new SlidingMenu(FindByGenreActivity.this);
			menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			menu.setShadowWidthRes(R.dimen.shadow_width);
			menu.setShadowDrawable(R.drawable.shadow);
			menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			menu.setFadeDegree(0.35f);
			menu.attachToActivity(FindByGenreActivity.this,
					SlidingMenu.SLIDING_CONTENT);
			menu.setMenu(R.layout.menu_frame);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.menu_frame,
							new OptionsMenuListFragment(
									FindByGenreActivity.this)).commit();

			playbackEventListener = new MyPlaybackEventListener();

			// set the ActionBar stuff
			ActionBar bar = getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayUseLogoEnabled(true);
			bar.setDisplayShowHomeEnabled(true);
			bar.setTitle("MovieHelper");

			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(
					this, R.array.genres,
					android.R.layout.simple_spinner_dropdown_item);
			bar.setListNavigationCallbacks(mSpinnerAdapter,
					new ActionBar.OnNavigationListener() {
						// Get the same strings provided for the drop-down's
						// ArrayAdapter
						public boolean onNavigationItemSelected(int position,
								long itemId) {
							String chosengenre = getResources().getStringArray(
									R.array.genres)[position];
							// Start by connecting to IMDB to get movies of
							// genre
							IMDBConnection imdbconn = new IMDBConnection(
									FindByGenreActivity.this);
							ArrayList<IMDBMovie> movies = imdbconn
									.getMoviesByGenre(chosengenre);
							if (movies == null) {
								Toast.makeText(
										FindByGenreActivity.this,
										"Error with getting movies from IMDBConn",
										Toast.LENGTH_LONG).show();
								// TODO error case, show cached movie?
								return true;
							}

							// Create a omdb connection to get info about movie
							OMDbConnection omdbconn = new OMDbConnection(
									FindByGenreActivity.this);
							// TODO: only showing the first movie now
							Movie m = omdbconn.getMovieInfo(movies.get(0)
									.getImdbid());
							if (m == null) {
								Toast.makeText(
										FindByGenreActivity.this,
										"Error with getting movies from OMDBConn",
										Toast.LENGTH_LONG).show();
								// TODO error case, show cached movie?
								return true;
							}

							ImageView img = (ImageView) findViewById(R.id.test);
							img.setImageBitmap(m.getIcon());

							// Show movie info
							TextView txt = (TextView) findViewById(R.id.info);
							txt.setText(m.toString());

							// Connect to YouTube to get video info
							YoutubeConnection conn = new YoutubeConnection(
									FindByGenreActivity.this);
							ArrayList<Trailer> results = conn.getVideo(m.title);
							if (results == null) {
								Toast.makeText(
										FindByGenreActivity.this,
										"Error with getting videos from youtube",
										Toast.LENGTH_LONG).show();
								return true;
							}
							// Sort by score
							Collections.sort(results);
							vidID = results.get(0).getId();
							// if (player == null)
							// System.out.println();
							// player.setFullscreen(true);
//							if (player == null) {
//								Toast.makeText(FindByGenreActivity.this, 
//										"Player was not initialized yet. enjoy aerosmith", Toast.LENGTH_LONG).show();
//							} else 
							if (player != null)
								player.cueVideo(vidID);
							// TODO this is where we want to cue video
							return true;
						}
					});
		} else {
			Toast.makeText(this,
					"No internet connection. Show cached movies here",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// do nothing. just here so we dont call onCreate again
	}

	@Override
	public void onBackPressed() {
		if (!menu.isMenuShowing()) {
			menu.showMenu();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (!menu.isShown())
				menu.showMenu();
			else
				menu.toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onInitializationFailure(Provider provider,
			YouTubeInitializationResult error) {
		if (error.isUserRecoverableError()) {
			error.getErrorDialog(this, 1).show();
		} else {
			String errorMessage = String.format(
					getString(R.string.error_player), error.toString());
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer play, boolean wasRestored) {
		this.player = play;
		player.setPlaybackEventListener(playbackEventListener);
		if (!wasRestored)
			player.cueVideo(vidID);
	}

	private final class MyPlaybackEventListener implements
			YouTubePlayer.PlaybackEventListener {
		@Override
		public void onBuffering(boolean arg0) {

		}

		@Override
		public void onPaused() {
			player.setFullscreen(false);
		}

		@Override
		public void onPlaying() {
			player.setFullscreen(true);
		}

		@Override
		public void onSeekTo(int arg0) {

		}

		@Override
		public void onStopped() {
		}
	}

	/**
	 * Checks if the network is available
	 * 
	 * @return
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
