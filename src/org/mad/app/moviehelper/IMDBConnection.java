package org.mad.app.moviehelper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * An API for getting information about movies
 * 
 * @author karthik
 * 
 */
@SuppressWarnings("unused")
public class IMDBConnection {

	private URL url = null;

	private HttpURLConnection conn;
	private BufferedReader buffer;
	private IMDBHTMLParser parser;
	private static Context context;
	private final String PREFIX = "http://www.imdb.com/";
	String line;

	public IMDBConnection(Context c) {
		context = c;
		parser = new IMDBHTMLParser();
	}

	public ArrayList<IMDBMovie> getMoviesByGenre(String genre) {
		ArrayList<IMDBMovie> result = new ArrayList<IMDBMovie>();
		String request = PREFIX + "search/title?genres=" + genre.toLowerCase().replace("-", "_");
		GetTask task = new GetTask("Getting movies...");
		task.execute(request);
		String HTMLResult;
		try {
			HTMLResult = (String) task.get(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			HTMLResult = null;
		} catch (ExecutionException e) {
			HTMLResult = null;
		} catch (TimeoutException e) {
			HTMLResult = null;
		}
		if (HTMLResult != null && HTMLResult.length() > 5) {//weird case
			result = parser.parseGenreMovieList(HTMLResult);
		} else {
			return null;
		}
		return result;
	}


	/**
	 * Task that performs the network transaction
	 * 
	 * @author karthik
	 * 
	 */
	@SuppressWarnings({})
	private class GetTask extends AsyncTask<String, String, String> {
		private String task;

		public GetTask(String t) {
			task = t;
		}

		private String get(final String request) {
			String XMLResult = "";
			try {
				StringBuffer buff = new StringBuffer();
				url = new URL(request);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.addRequestProperty("User-Agent", "Mozilla/3.6");
				buffer = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = buffer.readLine()) != null) {
					buff.append(line);
				}
				XMLResult = buff.toString();
				buffer.close();
			} catch (MalformedURLException e) {
				System.out.println("BAD URL");
				e.printStackTrace();
			} catch (ProtocolException e) {
				System.out.println("Could not connect");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Could not read");
				e.printStackTrace();
			}
			return XMLResult;
		}

		@Override
		protected String doInBackground(String... arg0) {
			return get((String) arg0[0]);
		}
	}

	
}
