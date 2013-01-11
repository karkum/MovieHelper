package org.mad.app.moviehelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
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

public class RandomMovieActivity extends SherlockFragmentActivity implements
YouTubePlayer.OnInitializedListener {

	private SlidingMenu menu;
	private YouTubePlayerSupportFragment youTubePlayerFragment;
	private String vidID = "txlXcJDtDwM";
	private YouTubePlayer player;
	private MyPlaybackEventListener playbackEventListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (isNetworkAvailable()) {
			youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
					.findFragmentById(R.id.youtube_fragment);
			youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY,
					RandomMovieActivity.this);
			playbackEventListener = new MyPlaybackEventListener();

			TextView txt = (TextView) findViewById(R.id.info);
			txt.setText("Choose a genre to begin!");

			// configure the SlidingMenu
			menu = new SlidingMenu(RandomMovieActivity.this);
			menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			menu.setShadowWidthRes(R.dimen.shadow_width);
			menu.setShadowDrawable(R.drawable.shadow);
			menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			menu.setFadeDegree(0.35f);
			menu.attachToActivity(RandomMovieActivity.this,
					SlidingMenu.SLIDING_CONTENT);
			menu.setMenu(R.layout.menu_frame);
			getSupportFragmentManager()
			.beginTransaction()
			.replace(
					R.id.menu_frame,
					new OptionsMenuListFragment(
							RandomMovieActivity.this)).commit();



			// set the Above View
			ActionBar bar = getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayUseLogoEnabled(true);
			bar.setDisplayShowHomeEnabled(true);
			bar.setTitle("MovieHelper");

			showNextRandomMovie();

		} else {
			Toast.makeText(this,
					"No network connection. Please try again later",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
		getSupportMenuInflater().inflate(R.menu.randommenu, menu);
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
			break;
		case R.id.actionbar_next:
			showNextRandomMovie();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showNextRandomMovie() {
		if (isNetworkAvailable()) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(getAssets().open("movielist.txt")));
				Random ran = new Random();
				int num = ran.nextInt(2000);
				for (int i = 0; i < num; ++i)
					reader.readLine();
				String movie = reader.readLine();
				OMDbConnection test = new OMDbConnection(
						RandomMovieActivity.this);
				Movie m = test.searchMovie(movie);
				if (m == null) {
					Toast.makeText(
							RandomMovieActivity.this,
							"Error with getting info from omdb",
							Toast.LENGTH_LONG).show();
					return;
				}
				TextView info = (TextView) findViewById(R.id.info);

				ImageView img = (ImageView) findViewById(R.id.test);
				img.setImageBitmap(m.getIcon());
				info.setText(m.toString());
				YoutubeConnection conn = new YoutubeConnection(
						RandomMovieActivity.this);
				ArrayList<Trailer> results = conn.getVideo(m.title);
				if (results == null) {
					Toast.makeText(
							RandomMovieActivity.this,
							"Error with getting videos from youtube",
							Toast.LENGTH_LONG).show();
					return;
				}
				Collections.sort(results);
				vidID = results.get(0).getId();
				// player.setFullscreen(true);
				//			if (player == null) {
				//				Toast.makeText(RandomMovieActivity.this, 
				//						"Player was not initialized yet. enjoy aerosmith", Toast.LENGTH_LONG).show();
				//			} else 
				if (player != null)
					player.cueVideo(vidID);//TODO this is where we want to cue video

			} catch (IOException e) {
				Toast.makeText(this, "Unknown Error. Please try again later",
						Toast.LENGTH_LONG).show();
			} 
		} else {
			Toast.makeText(this,
					"No network connection. Please try again later",
					Toast.LENGTH_LONG).show();
		}
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