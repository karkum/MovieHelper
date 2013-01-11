package org.mad.app.moviehelper;

import java.util.ArrayList;
import java.util.Collections;

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
import com.actionbarsherlock.widget.SearchView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.slidingmenu.lib.SlidingMenu;

public class SearchMovieActivity extends SherlockFragmentActivity implements
		YouTubePlayer.OnInitializedListener {

	private SlidingMenu menu;
	private YouTubePlayerSupportFragment youTubePlayerFragment;
	private String vidID = "txlXcJDtDwM";
	private YouTubePlayer player;
	private MyPlaybackEventListener playbackEventListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (isNetworkAvailable()) {
			TextView txt = (TextView) findViewById(R.id.info);
			txt.setText("Ignore the above video and just search something!");

			// configure the SlidingMenu
			menu = new SlidingMenu(SearchMovieActivity.this);
			menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			menu.setShadowWidthRes(R.dimen.shadow_width);
			menu.setShadowDrawable(R.drawable.shadow);
			menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			menu.setFadeDegree(0.35f);
			menu.attachToActivity(SearchMovieActivity.this,
					SlidingMenu.SLIDING_CONTENT);
			menu.setMenu(R.layout.menu_frame);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.menu_frame,
							new OptionsMenuListFragment(
									SearchMovieActivity.this)).commit();

			youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
					.findFragmentById(R.id.youtube_fragment);
			youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY,
					SearchMovieActivity.this);
			playbackEventListener = new MyPlaybackEventListener();

			// set the Above View
			ActionBar bar = getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayUseLogoEnabled(true);
			bar.setDisplayShowHomeEnabled(true);
			bar.setTitle("MovieHelper");
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
		// getSupportMenuInflater().inflate(R.menu.search, menu);
		//
		// SearchManager searchManager = (SearchManager)
		// getSystemService(Context.SEARCH_SERVICE);
		// SearchView searchView = (SearchView)
		// menu.findItem(R.id.menu_search_mine).getActionView();
		// searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		// searchView.setIconifiedByDefault(false); // Do not iconify the
		// widget; expand it by default
		SearchView searchView = new SearchView(getSupportActionBar()
				.getThemedContext());
		searchView.setQueryHint("Search for movies…");
		menu.add("Search")
				.setActionView(searchView)
				.setIcon(R.drawable.ic_action_search)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				OMDbConnection test = new OMDbConnection(
						SearchMovieActivity.this);
				Movie m = test.searchMovie(query);

				TextView info = (TextView) findViewById(R.id.info);

				ImageView img = (ImageView) findViewById(R.id.test);
				img.setImageBitmap(m.getIcon());
				info.setText(m.toString());
				YoutubeConnection conn = new YoutubeConnection(
						SearchMovieActivity.this);
				ArrayList<Trailer> results = conn.getVideo(m.title);

				Collections.sort(results);
				vidID = results.get(0).getId();
				player.cueVideo(vidID);
				// player.setFullscreen(true);
				// player.cueVideo(vidID);//TODO this is where we want to cue
				// video
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

		});
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